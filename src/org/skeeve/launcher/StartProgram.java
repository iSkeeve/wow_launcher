package org.skeeve.launcher;

public class StartProgram {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Options.loadOptions();
		javax.swing.SwingUtilities.invokeLater(new LauncherGUI());
	}
}
