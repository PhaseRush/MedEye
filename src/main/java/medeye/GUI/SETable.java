package medeye.GUI;

import javax.swing.*;

public class SETable extends JTable {

    public SETable(int row, int column){
        super(row,column);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        //all cells false
        return false;
    }
}
