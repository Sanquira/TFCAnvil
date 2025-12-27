package Demos;

import Demos.mocks.RobotMock;
import blacksmith.Scanner;

import javax.swing.*;
import java.awt.*;

public class ScannerTest {
    static JFrame frame = new JFrame();

    public static void main(String[] args) {
        Robot robot = null;
        try {
            robot = new RobotMock(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice());
        } catch (AWTException e) {
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(robot, new Rectangle());
        System.out.println(scanner.ScanDistance());
        System.out.println(scanner.ScanGreenPosition());
    }
}
