package szczepan;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class RecipeTableModel extends AbstractTableModel {
    private final String[] columnNames = {"Nazwa", "Kategoria", "Liczba Dopasowanych", "Liczba Relevantnych Skladnikow", "Proporcja"};
    private List<String[]> data = new ArrayList<>();

    public void setData(List<String[]> data) {
        this.data = data;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        String[] row = data.get(rowIndex);
        if (columnIndex < row.length) {
            return row[columnIndex];
        } else {
            return ""; // Zwróć pusty ciąg, jeśli indeks kolumny jest poza zakresem
        }
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
}
