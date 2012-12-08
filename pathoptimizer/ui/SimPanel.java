/**
 * 
 */
package pathoptimizer.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.sbml.libsbml.Model;

import pathoptimizer.data.PathOpt;

/**
 * @author Foeclan
 * 
 */
public class SimPanel extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTable table;
	private JTextArea output;
	private JTextField numRuns;
	private Model myModel;
	PathOpt pathway;

	public SimPanel(PathOpt path) {
		super();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		pathway = path;

		table = new JTable(new SimTableModel(path));
		table.setPreferredScrollableViewportSize(new Dimension(800, 500));
		table.setFillsViewportHeight(true);
		table.getSelectionModel().addListSelectionListener(new RowListener());
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setDefaultRenderer(Double.class, new NumberCellRenderer());

		add(new JScrollPane(table));

		// Put some controls across the middle
		JPanel controlsPanel = new JPanel(new FlowLayout());

		JButton runSimButton = new JButton("Run Simulation");
		runSimButton.setActionCommand("Run Simulation");
		runSimButton.addActionListener(this);

		numRuns = new JTextField("1", 5);

		JButton resetButton = new JButton("Reset");
		resetButton.setActionCommand("Reset Simulation");
		resetButton.addActionListener(this);

		controlsPanel.add(runSimButton);
		controlsPanel.add(numRuns);
		controlsPanel.add(resetButton);

		add(controlsPanel);

		output = new JTextArea(5, 40);
		output.setEditable(false);
		add(new JScrollPane(output));
	}

	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand();

		if (command == "Run Simulation") {
			try {
				int iterations = Integer.parseInt(numRuns.getText());
				System.out.println("Starting sim");
				// pathway.runSimulation(iterations);
				System.out.println("Sim over");

				SimTableModel myTableModel = (SimTableModel) table.getModel();
				myTableModel.refresh();

				table.repaint();
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
		} else if (command == "Reset Simulation") {
			pathway.resetSimulation();

			SimTableModel myTableModel = (SimTableModel) table.getModel();
			myTableModel.refresh();

			table.repaint();
		}
	}

	private class RowListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent event) {
			if (event.getValueIsAdjusting()) {
				return;
			}
			String speciesName = table.getValueAt(table.getSelectionModel().getLeadSelectionIndex(), 0).toString();
			output.setText(myModel.getSpecies(speciesName).getName());
		}
	}
}
