package medeye.GUI;

import javax.swing.*;
import java.awt.*;

public class AltTab extends JScrollPane{
    private AltTable table;

    public AltTab(){
        Dimension size = getPreferredSize();
        size.width = 500;
        setPreferredSize(size);
        setBorder(BorderFactory.createTitledBorder("Alternatives"));

        table = new AltTable(1,3);
        table.removeEditor();
        table.setName("Alternatives");
        table.setBounds(0,0,500,700);
        table.setShowGrid(true);
        table.setGridColor(new Color(0));
        table.setValueAt("Name",0,0);
        table.setValueAt("Price", 0, 1);
        table.setValueAt("Similarity score", 0,2);

        add(table);
    }
}
