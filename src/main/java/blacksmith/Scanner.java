package blacksmith;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Scanner {

    private static final Color RED_ARROW_COLOR = new Color(255, 0, 0);
    private static final Color GREEN_ARROW_COLOR = new Color(0, 255, 6);
    private static final int COLOR_TOLERANCE = 25;

    private final Robot robot;
    private Rectangle rectangle;
    private int redRow = 0;
    private int greenRow = 0;

    public Scanner(Robot robot, Rectangle rectangle) {
        this.robot = robot;
        this.rectangle = rectangle;
    }

    private boolean isColorClose(Color a, Color b) {
        return Math.abs(a.getRed() - b.getRed()) <= Scanner.COLOR_TOLERANCE
                && Math.abs(a.getGreen() - b.getGreen()) <= Scanner.COLOR_TOLERANCE
                && Math.abs(a.getBlue() - b.getBlue()) <= Scanner.COLOR_TOLERANCE;
    }

    public int ScanDistance() {
        BufferedImage progress = robot.createScreenCapture(rectangle);

        int y = redRow;
        int myRedPoint = -1;
        int myGreenPoint = -1;
        int redSize = 2;
        label30:
        for (; y < progress.getHeight(); y++) {
            for (int x = 0; x < progress.getWidth(); x++) {
                Color tmpColor = new Color(progress.getRGB(x, y));
                if (isColorClose(tmpColor, RED_ARROW_COLOR) && myRedPoint == -1) {
                    myRedPoint = x;
                    redRow = y;
                    y = greenRow == 0 ? y : greenRow;
                    for (int xr = x + 1; xr < progress.getWidth(); xr++) {
                        if (!isColorClose(new Color(progress.getRGB(xr, redRow)), RED_ARROW_COLOR)) {
                            redSize = xr - x;
                            break;
                        }
                    }
                }
                if (isColorClose(tmpColor, GREEN_ARROW_COLOR) && myGreenPoint == -1) {
                    myGreenPoint = x;
                    greenRow = y;
                }
                if (myRedPoint != -1 && myGreenPoint != -1) {
                    break label30;
                }
            }
        }
        if (myRedPoint == -1 || myGreenPoint == -1) {
            System.err.println("Red or green point not found");
            return -1;
        }
        return (myRedPoint - myGreenPoint) / redSize;
    }

    public int ScanGreenPosition() {
        BufferedImage progress = robot.createScreenCapture(rectangle);

        for (int y = greenRow; y < progress.getHeight() - 1; y++) {
            for (int x = 0; x < progress.getWidth(); x++) {
                Color tmpColor = new Color(progress.getRGB(x, y));
                if (isColorClose(tmpColor, GREEN_ARROW_COLOR)) {
                    return x;
                }
            }
        }
        return -1;
    }

    public int getRedRow() {
        return redRow;
    }

    public int getGreenRow() {
        return greenRow;
    }
}
