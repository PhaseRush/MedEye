package medeye.GUI;

import medeye.MedEye;
import medeye.wrapper.AlternativePrices;

import javax.swing.*;
import java.awt.*;

public class AltTab extends JScrollPane{
    private AltTable table;

    public AltTab(){
        Dimension size = getPreferredSize();
        size.width = 500;
        setPreferredSize(size);
        //setBorder(BorderFactory.createTitledBorder("Alternatives"));

        AlternativePrices alternativePrices = MedEye.alternativePrices;

        table = new AltTable(alternativePrices.list.size()+1,3);
        table.setName("Alternatives");
        table.setBounds(0,0,500,700);
        table.setGridColor(new Color(0));
        table.setValueAt("Name",0,0);
        table.setValueAt("Price", 0, 1);
        table.setValueAt("Similarity score", 0,2);

        // leftmost column
        for (int i = 0; i < alternativePrices.list.size(); i++) {
            String name = alternativePrices.list.get(i).getName();
            String price = alternativePrices.list.get(i).getPrice();
            String score = alternativePrices.list.get(i).getSimilarityPercent();
            table.setValueAt(name,i+1,0 );
            table.setValueAt(price,i+1,1);
            table.setValueAt(score,i+1,2);
        }

        //add(table);
        setViewportView(table);
    }
}
