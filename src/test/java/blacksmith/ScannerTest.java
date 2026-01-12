package blacksmith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScannerTest {
    @Mock
    private Robot robot;

    private BufferedImage img;
    private Rectangle rect;

    static Stream<Arguments> imageParameters() {
        return Stream.of(
                Arguments.of("image.png", 1.0, 113, 43, 26, 47), Arguments.of("scale150.png", 1.5, 69, 24, 10, 25));
    }

    private MockedStatic<CoordinateTransformer> coordinateTransformerMock(double scale) {
        MockedStatic<CoordinateTransformer> mocked = mockStatic(CoordinateTransformer.class);
        mocked.when(() -> CoordinateTransformer.getScaleX()).thenReturn(scale);
        mocked.when(() -> CoordinateTransformer.getScaleY()).thenReturn(scale);
        return mocked;
    }

    private Scanner setUp(String imageName, double scale) throws Exception {
        img = ImageIO.read(getClass().getClassLoader().getResourceAsStream(imageName));
        assertNotNull(img, "Test image " + imageName + " could not be loaded");
        rect = new Rectangle(0, 0, img.getWidth(), img.getHeight());
        when(robot.createScreenCapture(any(Rectangle.class))).thenReturn(img);
        return new Scanner(robot, rect);
    }

    @ParameterizedTest(name = "{0}: distance={2}, greenPos={3}")
    @MethodSource("imageParameters")
    void testScanDistance(
            String imageName,
            double scale,
            int expectedDistance,
            int expectedGreenPos,
            int expectedRedRow,
            int expectedGreenRow)
            throws Exception {
        Scanner scanner = setUp(imageName, scale);
        try (var mocked = coordinateTransformerMock(scale)) {
            int distance = scanner.ScanDistance();
            assertEquals(expectedDistance, distance, "Expected distance based on " + imageName);
        }
    }

    @ParameterizedTest(name = "{0}: greenPos={3}")
    @MethodSource("imageParameters")
    void testScanGreenPosition(
            String imageName,
            double scale,
            int expectedDistance,
            int expectedGreenPos,
            int expectedRedRow,
            int expectedGreenRow)
            throws Exception {
        Scanner scanner = setUp(imageName, scale);
        try (var mocked = coordinateTransformerMock(scale)) {
            scanner.ScanDistance(); // Initialize row positions
            int greenPos = scanner.ScanGreenPosition();
            assertEquals(expectedGreenPos, greenPos, "Expected green position based on " + imageName);
        }
    }

    @ParameterizedTest(name = "{0}: full test")
    @MethodSource("imageParameters")
    void testScanDistanceAndGreenPosition(
            String imageName,
            double scale,
            int expectedDistance,
            int expectedGreenPos,
            int expectedRedRow,
            int expectedGreenRow)
            throws Exception {
        Scanner scanner = setUp(imageName, scale);
        try (var mocked = coordinateTransformerMock(scale)) {
            int distance = scanner.ScanDistance();
            assertEquals(expectedDistance, distance, "Expected distance based on " + imageName);

            int greenPos = scanner.ScanGreenPosition();
            assertEquals(expectedGreenPos, greenPos, "Expected green position based on " + imageName);
            assertEquals(
                    expectedRedRow, scanner.getRedRow(), "Expected red row position to be cached for " + imageName);
            assertEquals(
                    expectedGreenRow,
                    scanner.getGreenRow(),
                    "Expected green row position to be cached for " + imageName);
        }
    }
}
