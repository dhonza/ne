package hyper.experiments.findcluster2;

import common.RND;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: drchaj1
 * Date: Jul 13, 2010
 * Time: 10:56:14 AM
 * Adapted from  Colin Green's SharpNEAT 2 HyperNEAT (Box Discrimination Task)
 * <p/>
 * Represents test cases for the Boxes visual discrimination task. The test case field is fixed at a resolution of 11x11
 * the visual field of the agents being evaluated on teh task can have a variable visual field resolution - the visual
 * field pixels sample the 11x11 pixels in the test field.
 */
public class TestCaseField {
    /// Resolution of the test field pixel grid.
    public final int testFieldResolution = 11;
    final int coordBoundIdx = testFieldResolution - 1;
    final int testFieldPixelCount = testFieldResolution * testFieldResolution;

    Point smallBoxTopLeft;
    Point largeBoxTopLeft;

    /// Default constructor.

    public TestCaseField() {
    }

    /// (Re)initialize with a fresh test case.
    /// Returns the target point (center of large box).

    public Point initTestCase(int largeBoxRelativePos) {
        // Get small and large box center positions.
        Point[] boxPosArr = generateRandomTestCase(largeBoxRelativePos);
        smallBoxTopLeft = boxPosArr[0];
        largeBoxTopLeft = boxPosArr[1];
        largeBoxTopLeft.x--;
        largeBoxTopLeft.y--;
        return boxPosArr[1];
    }

    /// Gets the value of the pixel at a position in the 'real/sensor' coordinate space (continuous x and y, -1 to 1).

    public double getPixel(double x, double y) {
        // Quantize real position to test field pixel coords.
        int pixelX = (int) (((x + 1.0) * testFieldResolution) / 2.0);
        int pixelY = (int) (((y + 1.0) * testFieldResolution) / 2.0);

        // Test for intersection with small box pixel.
        if (smallBoxTopLeft.x == pixelX && smallBoxTopLeft.y == pixelY) {
            return 1.0;
        }

        // Test for intersection with large box pixel.
        int deltaX = pixelX - largeBoxTopLeft.x;
        int deltaY = pixelY - largeBoxTopLeft.y;
        return (deltaX > -1 && deltaX < 3 && deltaY > -1 && deltaY < 3) ? 1.0 : 0.0;
    }

    /// Gets the coordinate of the small box (the small box occupies a single pixel).

    public Point getSmallBoxTopLeft() {
        return smallBoxTopLeft;
    }

    public void setSmallBoxTopLeft(Point smallBoxTopLeft) {
        this.smallBoxTopLeft = smallBoxTopLeft;
    }

    /// Gets the coordinate of the large box's top left pixel.

    public Point getLargeBoxTopLeft() {
        return largeBoxTopLeft;
    }

    public void setLargeBoxTopLeft(Point largeBoxTopLeft) {
        this.largeBoxTopLeft = largeBoxTopLeft;
    }

    private Point[] generateRandomTestCase(int largeBoxRelativePos) {
        // Randomly select a position for the small box (the box is a single pixel in size).
        Point smallBoxPos = new Point(RND.getInt(0, testFieldResolution - 1), RND.getInt(0, testFieldResolution - 1));

        // Large box center is 5 pixels to the right, down or diagonally from the small box.
        Point largeBoxPos = new Point(smallBoxPos);
        switch (largeBoxRelativePos) {
            case 0: // Right
                largeBoxPos.x += 5;
                break;
            case 1: // Down
                largeBoxPos.y += 5;
                break;
            case 2: // Diagonal
                // Two alternate position get us to exactly 5 pixels distant from the small box.
                if (RND.getBoolean()) {
                    largeBoxPos.x += 3;
                    largeBoxPos.y += 4;
                } else {
                    largeBoxPos.x += 4;
                    largeBoxPos.y += 3;
                }
                break;
        }

        // Handle cases where the large box is outside the visual field or overlapping the edge.
        if (largeBoxPos.x > coordBoundIdx) {   // Wrap around.
            largeBoxPos.x -= testFieldResolution;

            if (0 == largeBoxPos.x) {   // Move box fully into the visual field.
                largeBoxPos.x++;
            }
        } else if (coordBoundIdx == largeBoxPos.x) {   // Move box fully into the visual field.
            largeBoxPos.x--;
        } else if (0 == largeBoxPos.x) {   // Move box fully into the visual field.
            largeBoxPos.x++;
        }


        if (largeBoxPos.y > coordBoundIdx) {   // Wrap around.
            largeBoxPos.y -= testFieldResolution;

            if (0 == largeBoxPos.y) {   // Move box fully into the visual field.
                largeBoxPos.y++;
            }
        } else if (coordBoundIdx == largeBoxPos.y) {   // Move box fully into the visual field.
            largeBoxPos.y--;
        } else if (0 == largeBoxPos.y) {   // Move box fully into the visual field.
            largeBoxPos.y++;
        }
        return new Point[]{smallBoxPos, largeBoxPos};
    }
}
