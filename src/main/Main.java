package main;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

public class Main {

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(() -> {
			final Window w = Window.createAndShowGUI();
			new Thread(() -> {
				while (true) {
					// LissajousPanel is returned
					final JComponent c = (JComponent) w.getComponent(0);
					c.revalidate();
					c.repaint();
					try {
						Thread.sleep(1);
					} catch (final InterruptedException ex) {
						Thread.currentThread().interrupt();
						ex.printStackTrace();
					}
				}
			}).start();
		});
	}
}
