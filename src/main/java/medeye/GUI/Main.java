package medeye.GUI;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        initializer();
    }
    public static void initializer(){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new MainFrame("MedEye");
                Dimension d = new Dimension(500,800);
                frame.setSize(d);
                frame.setMaximumSize(d);
                frame.setMinimumSize(d);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }

}
