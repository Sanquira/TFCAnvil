package blacksmith;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Scanner {

    private static final Color RED_ARROW_COLOR = new Color(255, 0, 0);
    private static final Color GREEN_ARROW_COLOR = new Color(0, 255, 6);

    private final Robot robot;
    private Rectangle rectangle;

    public Scanner(Robot robot, Rectangle rectangle) {
        this.robot = robot;
        this.rectangle = rectangle;
    }

    public int ScanDistance() {
        BufferedImage progress = robot.createScreenCapture(rectangle);

        int y = 0;
        int myRedPoint = -1;
        int myGreenPoint = -1;
        int redSize = 2;
        label30:
        for (; y < progress.getHeight(); y++) {
            for (int x = 0; x < progress.getWidth(); x++) {
                Color tmpColor = new Color(progress.getRGB(x, y));
                if (tmpColor.equals(RED_ARROW_COLOR) && myRedPoint == -1) {
                    myRedPoint = x;
                    for (int xr = x + 1; xr < progress.getWidth(); xr++) {
                        if (!(new Color(progress.getRGB(xr, y))).equals(RED_ARROW_COLOR)) {
                            redSize = xr - x;
                            break;
                        }
                    }
                }
                if (tmpColor.equals(GREEN_ARROW_COLOR) && myGreenPoint == -1) {
                    myGreenPoint = x;
                }
                if (myRedPoint != -1 && myGreenPoint != -1) {
                    break label30;
                }
            }
        }
        if (myRedPoint == -1 || myGreenPoint == -1) {
            System.err.println("Red or green point not found");
            return 0;
        }
        return (myRedPoint - myGreenPoint) / redSize;
    }

    public int ScanGreenPosition() {
        BufferedImage progress = robot.createScreenCapture(rectangle);
        Color myGreen = new Color(0, 255, 6);

        for (int y = progress.getHeight() - 1; y >= 0; y--) {
            for (int x = 0; x < progress.getWidth(); x++) {
                Color tmpColor = new Color(progress.getRGB(x, y));
                if (tmpColor.equals(myGreen)) {
                    return x;
                }
            }
        }
        return -1;
    }
}
