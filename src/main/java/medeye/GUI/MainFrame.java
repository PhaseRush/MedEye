package medeye.GUI;

import javax.swing.*;
import java.awt.*;
public class MainFrame extends JFrame{

    private AltTab altTab;
    private SETab seTab;
    public MainFrame(String title) {
        super(title);

        altTab = new AltTab();
        seTab = new SETab();

        //Set layout manager (how to arrange components on the frame
        setLayout(new BorderLayout());


        //Create Swing Component
        Dimension d = new Dimension();

        JPanel dArea = new JPanel();
        dArea.setFont(new Font("Arial", 0, 36));
        dArea.setVisible(true);
        dArea.setAutoscrolls(false);

        JScrollPane scrollPane = new JScrollPane(dArea);
        scrollPane.setPreferredSize(new Dimension(500,300));
        dArea.setAlignmentX(0);
        dArea.setAlignmentY(0);

        JTabbedPane tabbedPane = new JTabbedPane();
        d.setSize(500,500);
        tabbedPane.setPreferredSize(d);

        tabbedPane.addTab("Known side effects of this drug",seTab);
        tabbedPane.addTab("Alternatives",altTab);

        add(scrollPane, BorderLayout.NORTH);
        add(tabbedPane,BorderLayout.CENTER);
    }
}
