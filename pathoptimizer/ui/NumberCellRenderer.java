package pathoptimizer.ui;

import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class NumberCellRenderer extends DefaultTableCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	DecimalFormat numberFormat = new DecimalFormat("#;-#");

	public Component getTableCellRendererComponent(JTable jTable, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Component cellData = super.getTableCellRendererComponent(jTable, value, isSelected, hasFocus, row, column);

		// Check for numbers
		if (cellData instanceof JLabel && value instanceof Number) {
			JLabel label = (JLabel) cellData;

			label.setHorizontalAlignment(JLabel.CENTER);
			Number num = (Number) value;
			String text = numberFormat.format(num);
			label.setText(text);

			label.setForeground(num.doubleValue() < 0 ? Color.RED : Color.BLACK);
		}
		return cellData;
	}
}
