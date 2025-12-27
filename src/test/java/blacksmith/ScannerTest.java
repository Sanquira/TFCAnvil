package blacksmith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScannerTest {
    private BufferedImage img;

    @Mock
    private Robot robot;

    private Rectangle rect;

    @BeforeEach
    void setUp() throws Exception {
        img = ImageIO.read(getClass().getClassLoader().getResourceAsStream("image.png"));
        assertNotNull(img, "Test image could not be loaded");
        rect = new Rectangle(0, 0, img.getWidth(), img.getHeight());
        when(robot.createScreenCapture(any(Rectangle.class))).thenReturn(img);
    }

    @Test
    void testScanDistanceWithImage() {
        Scanner scanner = new Scanner(robot, rect);
        int distance = scanner.ScanDistance();
        assertEquals(113, distance, "Expected distance based on image.png");
    }

    @Test
    void testScanGreenPositionWithImage() {
        Scanner scanner = new Scanner(robot, rect);
        int greenPos = scanner.ScanGreenPosition();
        assertEquals(43, greenPos, "Expected green position based on image.png");
    }

    @Test
    void testScanDistanceAndGreenPosition() {
        Scanner scanner = new Scanner(robot, rect);
        int distance = scanner.ScanDistance();
        assertEquals(113, distance, "Expected distance based on image.png");
        int greenPos = scanner.ScanGreenPosition();
        assertEquals(43, greenPos, "Expected green position based on image.png");
        assertEquals(26, scanner.getRedRow(), "Expected red row position to be cached.");
        assertEquals(47, scanner.getGreenRow(), "Expected green row position to be cached.");
    }
}
