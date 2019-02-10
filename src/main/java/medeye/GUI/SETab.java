package medeye.GUI;

import javax.swing.*;
import java.awt.*;

public class SETab extends JScrollPane{
    private SETable table;

    public SETab(){
        Dimension size = getPreferredSize();
        size.width = 500;
        setPreferredSize(size);
        setBorder(BorderFactory.createTitledBorder("Alternatives"));

        table = new SETable(1,2);
        table.removeEditor();
        table.setName("Active Ingredients");
        table.setBounds(0,0,500,700);
        table.setShowGrid(true);
        table.setGridColor(new Color(0));
        table.setValueAt("Name",0,0);
        table.setValueAt("Known Side Effects", 0, 1);

        add(table);
    }
}
