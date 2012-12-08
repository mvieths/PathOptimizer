/**
 * 
 */
package pathoptimizer;

import pathoptimizer.data.PathOpt;

/**
 * @author Michael Vieths
 * 
 */
public class Launcher {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});

	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be invoked from the event-dispatching thread.
	 */
	private static void createAndShowGUI() {
		String pathwayFile = System.getenv("PATHWAY_FILE");
		String defaultsFile = System.getenv("DEFAULTS_FILE");

		PathOpt path;

		if (defaultsFile == null) {
			path = new PathOpt(pathwayFile);
		} else {
			System.out.println("Using defaults file " + defaultsFile);
			path = new PathOpt(pathwayFile, defaultsFile);
		}

		// // Disable boldface controls.
		// UIManager.put("swing.boldMetal", Boolean.FALSE);
		//
		// // Create and set up the window.
		// JFrame frame = new JFrame("Pathway Simulator");
		// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//
		// // Create and set up the content pane.
		// SimPanel newContentPane = new SimPanel(path);
		// newContentPane.setOpaque(true); // content panes must be opaque
		// frame.setContentPane(newContentPane);
		//
		// // Display the window.
		// frame.pack();
		// frame.setVisible(true);
	}
}
