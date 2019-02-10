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

        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("Arial", 0, 36));
        textArea.setColumns(1);
        textArea.setVisible(true);
        textArea.setAutoscrolls(true);
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500,300));
        textArea.setAlignmentX(0);
        textArea.setAlignmentY(0);

        JTabbedPane tabbedPane = new JTabbedPane();
        d.setSize(500,500);
        tabbedPane.setPreferredSize(d);

        tabbedPane.addTab("Known side effects of this drug",seTab);
        tabbedPane.addTab("Alternatives",altTab);

        add(scrollPane, BorderLayout.NORTH);
        add(tabbedPane,BorderLayout.CENTER);
    }
}
