/**
 * 
 */
package pathoptimizer.ui;

import javax.swing.table.AbstractTableModel;

import pathoptimizer.data.MoleculeList;
import pathoptimizer.data.Pathway;


/**
 * @author Foeclan
 * 
 */
public class SimTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;

	private String[] columnNames = { "Molecules", "Reactant?", "Product?", "Starting Quantity", "Ending Quantity" };
	private Object[][] data;
	MoleculeList list;
	MoleculeList curList;

	public SimTableModel(Pathway path) {
		list = path.getMolecules();
		curList = path.getMolecules();

		data = new Object[list.size()][columnNames.length];

		loadStaticData(list);
		loadDynamicData(curList);
	}

	public void loadStaticData(MoleculeList list) {
		for (int i = 0; i < list.size(); i++) {
			// Add the name of the molecule
			data[i][0] = list.get(i).getName();
			// Is it a Product?
			data[i][1] = new Boolean(list.get(i).isReactant());
			// Is it a reagent?
			data[i][2] = new Boolean(list.get(i).isProduct());
			// Starting quantity
			data[i][3] = list.get(i).getQuantity();
		}
	}

	public void loadDynamicData(MoleculeList list) {
		for (int i = 0; i < list.size(); i++) {
			// Ending quantity
			data[i][4] = list.get(i).getQuantity();
		}
	}

	public void refresh() {
		loadDynamicData(list);
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		return data.length;
	}

	public String getColumnName(int col) {
		return columnNames[col];
	}

	public Object getValueAt(int row, int col) {
		return data[row][col];
	}

	/*
	 * JTable uses this method to determine the default renderer/ editor for each cell. If we didn't implement this method, then the last column would contain text ("true"/"false"), rather than a check box.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Class getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

	/*
	 * Don't need to implement this method unless your table's editable.
	 */
	public boolean isCellEditable(int row, int col) {
		// Note that the data/cell address is constant,
		// no matter where the cell appears onscreen.
		if (col < 2) {
			return false;
		} else {
			return true;
		}
	}
}
