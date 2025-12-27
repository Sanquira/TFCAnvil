package Demos.mocks;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class RobotMock extends Robot {

    public RobotMock(GraphicsDevice screen) throws AWTException {
        super(screen);
    }

    @Override
    public synchronized BufferedImage createScreenCapture(Rectangle screenRect) {
        try {
            URL testFileRes = RobotMock.class.getClassLoader().getResource("tests/panelStrip.png");
            if (testFileRes == null) {
                return null;
            }
            URI testFileURI = testFileRes.toURI();
            return ImageIO.read(new File(testFileURI));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
}
