package blacksmith;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * Utility class for transforming coordinates between physical (native event) and logical (Robot) coordinate systems.
 * This is necessary because on high-DPI displays, native events report physical pixels while Robot API expects logical pixels.
 */
public class CoordinateTransformer {

    private static final double scaleX;
    private static final double scaleY;

    static {
        double tempScaleX = 1.0;
        double tempScaleY = 1.0;
        try {
            GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice()
                    .getDefaultConfiguration();
            AffineTransform tx = gc.getDefaultTransform();
            tempScaleX = tx.getScaleX();
            tempScaleY = tx.getScaleY();
        } catch (HeadlessException e) {
            // In headless environment (e.g., tests), use 1.0 scale (no transformation)
            System.err.println("Headless environment detected. Using 1.0 scale for coordinate transformation.");
        }
        scaleX = tempScaleX;
        scaleY = tempScaleY;
    }

    /**
     * Transforms a physical X coordinate to logical coordinate.
     * @param physicalX physical X coordinate from native event
     * @return logical X coordinate for Robot API
     */
    public static int physicalToLogicalX(int physicalX) {
        return (int) Math.round(physicalX / getScaleX());
    }

    /**
     * Transforms a physical Y coordinate to logical coordinate.
     * @param physicalY physical Y coordinate from native event
     * @return logical Y coordinate for Robot API
     */
    public static int physicalToLogicalY(int physicalY) {
        return (int) Math.round(physicalY / getScaleY());
    }

    /**
     * Transforms a physical Point to logical Point.
     * @param physicalPoint physical Point from native event
     * @return logical Point for Robot API
     */
    public static Point physicalToLogical(Point physicalPoint) {
        return new Point(physicalToLogicalX(physicalPoint.x), physicalToLogicalY(physicalPoint.y));
    }

    /**
     * Transforms a physical Rectangle to logical Rectangle.
     * @param physicalRect physical Rectangle from native event
     * @return logical Rectangle for Robot API
     */
    public static Rectangle physicalToLogical(Rectangle physicalRect) {
        return new Rectangle(
                physicalToLogicalX(physicalRect.x),
                physicalToLogicalY(physicalRect.y),
                (int) Math.round(physicalRect.width / getScaleX()),
                (int) Math.round(physicalRect.height / getScaleY()));
    }

    /**
     * Transforms a logical X coordinate to physical coordinate.
     * @param logicalX logical X coordinate from Robot API
     * @return physical X coordinate for native event comparison
     */
    public static int logicalToPhysicalX(int logicalX) {
        return (int) Math.round(logicalX * getScaleX());
    }

    /**
     * Transforms a logical Y coordinate to physical coordinate.
     * @param logicalY logical Y coordinate from Robot API
     * @return physical Y coordinate for native event comparison
     */
    public static int logicalToPhysicalY(int logicalY) {
        return (int) Math.round(logicalY * getScaleY());
    }

    /**
     * Transforms a logical Point to physical Point.
     * @param logicalPoint logical Point from Robot API
     * @return physical Point for native event comparison
     */
    public static Point logicalToPhysical(Point logicalPoint) {
        return new Point(logicalToPhysicalX(logicalPoint.x), logicalToPhysicalY(logicalPoint.y));
    }

    /**
     * Transforms a logical Rectangle to physical Rectangle.
     * @param logicalRect logical Rectangle from Robot API
     * @return physical Rectangle for native event comparison
     */
    public static Rectangle logicalToPhysical(Rectangle logicalRect) {
        return new Rectangle(
                logicalToPhysicalX(logicalRect.x),
                logicalToPhysicalY(logicalRect.y),
                (int) Math.round(logicalRect.width * getScaleX()),
                (int) Math.round(logicalRect.height * getScaleY()));
    }

    /**
     * Gets the X scale factor.
     * @return scale factor for X axis
     */
    public static double getScaleX() {
        return scaleX;
    }

    /**
     * Gets the Y scale factor.
     * @return scale factor for Y axis
     */
    public static double getScaleY() {
        return scaleY;
    }
}
