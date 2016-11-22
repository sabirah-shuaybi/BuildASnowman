import java.awt.*;
import objectdraw.*;
/**
 * Creates a program that allows user to construct a snowman from three different sized circles. The user must drag
 * the circles into position. When the circle is generally in the right position, the program helps secure the circles
 * into place. When the circles are snapped into place, the program displays a message to the user stating, "Good Job!" and at this point,
 * the program locks the circles into place, preventing any further movement of the snowman spheres.
 *
 * @author Sabirah Shuaybi
 * @version 9/20/16
 */
public class BuildASnowman extends WindowController
{

    //Start x distance for head
    private static final int HEAD_LEFT = 10;
    //Start x distance for body
    private static final int BODY_LEFT = 70;
    //Start x distance for base
    private static final int BASE_LEFT = 180;
    //Start distance from top of display to tops of balls
    private static final int TOP = 10;

    //Snowman’s head size (smallest)
    private static final int HEAD_SIZE = 50;
    //Snowman's body size (next smallest)
    private static final int BODY_SIZE = 100;
    //Snowman's base size (largest)
    private static final int BASE_SIZE = 150;

    //Snowman’s head
    private FilledOval head;
    //Snowman's body
    private FilledOval body;
    //Snowman's base
    private FilledOval base;

    //To keep track of which snowman part is grabbed by user
    private boolean headGrabbed = false;
    private boolean bodyGrabbed = false;
    private boolean baseGrabbed = false;

    //To capture the snap events of the head and body
    private boolean headSnapped = false;
    private boolean bodySnapped = false;

    //To label mouse position before the drag to determine the extent of travel for ball
    //To hold the location of the point where mouse was last seen
    private Location lastPoint;


    /* Creates and displays the 3 parts of snowman with their corresponding sizes when program begins
       Sets snowman parts to the color turquoise */
    public void begin () {

        head = new FilledOval (HEAD_LEFT, TOP, HEAD_SIZE, HEAD_SIZE, canvas);
        head.setColor(new Color(64, 224, 208));

        body = new FilledOval (BODY_LEFT, TOP, BODY_SIZE, BODY_SIZE, canvas);
        body.setColor(new Color(64, 224, 208));

        base = new FilledOval (BASE_LEFT, TOP, BASE_SIZE, BASE_SIZE, canvas);
        base.setColor(new Color(64, 224, 208));
    }

    /* Evaluates whether user's mouse press was contained within any of the 3 snowman parts
       Sets boolean flags accordingly to the contain conditions */
    public void onMousePress (Location currentPoint) {

        if (head.contains (currentPoint)) {
            headGrabbed = true;}
        else if (body.contains (currentPoint)) {
            bodyGrabbed = true;}
        else if (base.contains (currentPoint)) {
            baseGrabbed = true;}

        //Capture this current location of click for the onMouseDrag method in instance var lastPoint
        lastPoint = currentPoint;
    }

    /* Dynamically returns the location of the bottom of any FramedOval */
    private Location getBottom(FilledOval bottom) {

        double bottomLocX = bottom.getX() + (bottom.getWidth()/2);
        double bottomLocY = bottom.getY() + bottom.getHeight();

        Location bottomLoc = new Location(bottomLocX, bottomLocY);

        return bottomLoc;
    }

    /* Dynamically returns the location of the top of any FramedOval */
    private Location getTop(FilledOval top) {

        double topLocX = top.getX() + (top.getWidth()/2);
        double topLocY = top.getY();

        Location topLoc = new Location (topLocX, topLocY);

        return topLoc;
    }

    /* Uses the API distanceTo feature of Location.
       Takes two locations as parameters and returns distance between Location 1 and Location 2 */
    private double getDistance(Location loc1, Location loc2) {

        return loc1.distanceTo(loc2);
    }

    /* Snaps head to body */
    private void snapHeadToBody(Location bodyTopLoc) {

        //Define new head x and y positions relative to body for snapping head in place
        double newHeadLocX = bodyTopLoc.getX()-(head.getWidth()/2);
        double newHeadLocY = bodyTopLoc.getY()-(head.getHeight());

        Location newHeadLoc = new Location(newHeadLocX, newHeadLocY);

        //"Snap" head to this new location on top of body
        head.moveTo(newHeadLoc);

        //Set boolean flag headSnapped to true
        //This will help for later to determine when both headSnapped && bodySnapped
        headSnapped = true;
    }

    /* Snaps body to base
       Uses moveTo method to then "snap" the body to this new location on top of base */
    private void snapBodyToBase(Location baseTopLoc) {

        //Define new body x and y positions relative to base for snapping body in place
        double newBodyLocX = baseTopLoc.getX()-(body.getWidth()/2);
        double newBodyLocY = baseTopLoc.getY()-(body.getHeight());

        Location newBodyLoc = new Location(newBodyLocX, newBodyLocY);

        //"Snap" body to this new location on top of base
        body.moveTo(newBodyLoc);

        //Set boolean flag bodySnapped to true
        //This will help for later to determine when both headSnapped && bodySnapped
        bodySnapped = true;
    }

    /* Evaluates whether head, body or base was grabbed
       Then allows user to drag the grabbed oval from last point to current point */
    public void onMouseDrag (Location currentPoint) {

        //Ff both head and body are snapped, exit method
        //This will prevent user from being able to move the ovals once everything has been snapped in place
        if (headSnapped && bodySnapped)
            return;

        //If head is grabbed, move head across the distance it is being dragged
        if (headGrabbed) {

            //Allow user to drag head from point A (last point) to point B (current point)
            head.move (currentPoint.getX() - lastPoint.getX(), currentPoint.getY() - lastPoint.getY()) ;

            //If head is snapped to body, also drag body along with head
            if (headSnapped)
                body.move (currentPoint.getX() - lastPoint.getX(), currentPoint.getY() - lastPoint.getY()) ;


            Location headBottomLoc = getBottom(head);
            Location bodyTopLoc = getTop(body);

            //Compute distance b/w bottom of head and top of body
            double distance = getDistance(headBottomLoc, bodyTopLoc);

            //If distance b/w bottom of head and top of body is less than or equal to 14.1421, snap head to body
            //This value was determined from calculating the hypotenuse of of a 10 pixel by 10 pixel triangle
            //10 pixel by 10 pixel values obtained from error margin approach
            if (distance <= 14.1421) {
                snapHeadToBody(bodyTopLoc);
            }

        }
        //If body grabbed, move body across the distance it is being dragged
        else if (bodyGrabbed) {

            //Allow user to drag body from point A (last point) to point B (current point)
            body.move (currentPoint.getX() - lastPoint.getX(), currentPoint.getY() - lastPoint.getY());

            //If head is snapped to body, also drag head along with body
            if (headSnapped)
                head.move (currentPoint.getX() - lastPoint.getX(), currentPoint.getY() - lastPoint.getY()) ;

            //If body is snapped to base, also drag base will along with body
            if (bodySnapped)
                base.move (currentPoint.getX() - lastPoint.getX(), currentPoint.getY() - lastPoint.getY()) ;

            Location bodyBottomLoc = getBottom(body);
            Location baseTopLoc = getTop(base);

            //Compute distance b/w bottom of body and top of base
            double distanceBetweenBodyAndBase = getDistance(bodyBottomLoc, baseTopLoc);

            //If distance between bottom of body and top of base is less than or equal to 14.1421
            //Then, snap body to the base
            if (distanceBetweenBodyAndBase <= 14.1421) {
                snapBodyToBase(baseTopLoc);
                if (headSnapped)
                    snapHeadToBody(getTop(body));
            }

        }

        //If base is grabbed, move base across the distance it is being dragged
        else if (baseGrabbed) {

            //Allows user to drag base from point A (last point) to point B (current point)
            base.move (currentPoint.getX() - lastPoint.getX(), currentPoint.getY() - lastPoint.getY());

            //If body is snapped to base, also drag body along with base
            if (bodySnapped)
                body.move (currentPoint.getX() - lastPoint.getX(), currentPoint.getY() - lastPoint.getY());
        }

        //Capture current click
        lastPoint = currentPoint;

        //If both the conditions of headSnapped and bodySnapped are fulfilled, congratulate user
        if (headSnapped && bodySnapped)
            displayCongrats();

    }

    /* Congratulates user when snowman has been constructed */
    private void displayCongrats() {

        //Create text object displaying "Good Job!"
        Text goodJob = new Text ("Good Job!", 80, 20, canvas);
    }

    //Sets boolean variables to false to prevent any movement of snowman balls after user releases mouse
    //Permanently locks everything into place
    public void onMouseRelease (Location currentPoint) {

        headGrabbed = false;
        bodyGrabbed = false;
        baseGrabbed = false;
    }


}
