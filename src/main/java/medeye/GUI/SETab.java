package medeye.GUI;

import medeye.MedEye;
import medeye.wrapper.ActiveSideEffectWrapper;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class SETab extends JScrollPane{
    private SETable table;

    public SETab(){
        Dimension size = getPreferredSize();
        size.width = 500;
        setPreferredSize(size);
        setBorder(BorderFactory.createTitledBorder("Alternatives"));

        ActiveSideEffectWrapper activeSideEffectWrapper = MedEye.activeSideEffectWrapper;
        //table.setValueAt("Known Side Effects", 0, 1);
        JTextArea SEs = new JTextArea("Side Effects:\n");
        SEs.setSize(new Dimension(500,700));

        ArrayList<String> sideEffects = activeSideEffectWrapper.getSideEffects();
        StringBuilder sb = new StringBuilder("Ingredients:\n");
        for (String s: activeSideEffectWrapper.getIngredients()) {
            sb.append(s).append("\n");
        }
        for (int j = 0; j < activeSideEffectWrapper.getSideEffects().size(); j++) {
            String s = sideEffects.get(j);
            SEs.append(s + "\n");
        }
        SEs.append("\n");
        SEs.append(sb.toString());
        //setViewportView(table);
        setViewportView(SEs);
    }
}
