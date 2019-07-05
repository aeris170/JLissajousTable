package main;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class Window extends JFrame {

	private static final long serialVersionUID = 1L;

	public static final GraphicsDevice FIRST_MONITOR = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0];
	public static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();

	private Window() {}

	public static Window createAndShowGUI() {
		final Window w = new Window();
		if (FIRST_MONITOR.isFullScreenSupported()) {
			w.setUndecorated(true);
			FIRST_MONITOR.setFullScreenWindow(w);
		} else {
			w.setBounds(200, 200, SCREEN_SIZE.width / 2, SCREEN_SIZE.height / 2);
			System.err.println("Full screen not supported");
		}
		w.setTitle("JLisajousTable");
		w.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		w.add(new LissajousPanel());
		w.setVisible(true);
		return w;
	}
}
