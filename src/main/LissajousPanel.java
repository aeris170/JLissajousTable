package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.JPanel;

public class LissajousPanel extends JPanel {

	private static final long serialVersionUID = 7482055619892226429L;

	private static final double HALF_PI = Math.PI / 2;
	private static final Color TRANSLUCENT_WHITE = new Color(255, 255, 255, 150);

	private Dimension parentSize;

	float angle = 0;
	// try changing this and see what happens. Recommended: 20, 80, 120, 200. Use
	// the Mouse Wheel.
	int w = 200;
	int cols;
	int rows;
	transient Curve[][] curves;
	boolean renderingComplete = false;

	public LissajousPanel() {
		parentSize = Window.SCREEN_SIZE;
		init();

		super.setBackground(Color.BLACK);
		super.addMouseListener(new Listener());
		super.addMouseWheelListener(new Listener());
	}

	@Override
	public void paintComponent(final Graphics g) {
		final Graphics2D g2d = (Graphics2D) g;

		setRenderingHints(g2d);

		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, getWidth(), getHeight());

		if (!renderingComplete) {
			final float d = w * 0.8f;
			final float r = d / 2;
			for (int i = 0; i < cols; i++) {
				final float cx = w + i * w + w / 2f;
				final float cy = w / 2f;
				g2d.setStroke(new BasicStroke(1));
				g2d.setColor(Color.WHITE);
				g2d.drawOval((int) (cx - d / 2), (int) (cy - d / 2), (int) d, (int) d);
				final float x = (float) (r * Math.cos(Math.toRadians(angle * (i + 1)) - HALF_PI));
				final float y = (float) (r * Math.sin(Math.toRadians(angle * (i + 1)) - HALF_PI));
				g2d.setStroke(new BasicStroke(8));
				g2d.drawOval((int) (cx + x) - 1, (int) (cy + y) - 1, 2, 2);
				g2d.setColor(TRANSLUCENT_WHITE);
				g2d.setStroke(new BasicStroke(1));
				g2d.drawLine((int) (cx + x), 0, (int) (cx + x), parentSize.height);
				for (int j = 0; j < rows; j++) {
					curves[j][i].setX(cx + x);
				}
			}
			for (int j = 0; j < rows; j++) {
				final float cx = w / 2f;
				final float cy = w + j * w + w / 2f;
				g2d.setStroke(new BasicStroke(1));
				g2d.setColor(Color.WHITE);
				g2d.drawOval((int) (cx - d / 2), (int) (cy - d / 2), (int) d, (int) d);
				final float x = (float) (r * Math.cos(Math.toRadians(angle * (j + 1)) - HALF_PI));
				final float y = (float) (r * Math.sin(Math.toRadians(angle * (j + 1)) - HALF_PI));
				g2d.setStroke(new BasicStroke(8));
				g2d.drawOval((int) (cx + x) - 1, (int) (cy + y) - 1, 2, 2);
				g2d.setColor(TRANSLUCENT_WHITE);
				g2d.setStroke(new BasicStroke(1));
				g2d.drawLine(0, (int) (cy + y), parentSize.width, (int) (cy + y));
				for (int i = 0; i < cols; i++) {
					curves[j][i].setY(cy + y);
				}
			}
			for (int j = 0; j < rows; j++) {
				for (int i = 0; i < cols; i++) {
					curves[j][i].addPoint();
				}
			}
			angle -= 1;
		}
		for (int j = 0; j < rows; j++) {
			for (int i = 0; i < cols; i++) {
				curves[j][i].show(g2d);
			}
		}
		if (angle < -360) {
			renderingComplete = true;
		}
	}

	public int getW() {
		return w;
	}

	public void setW(int w) {
		if (w == 0) {
			return;
		}
		this.w = w;
		init();
	}

	public void init() {
		cols = parentSize.width / w - 1;
		rows = parentSize.height / w - 1;
		if (cols > 0 && rows > 0) {
			curves = new Curve[rows][cols];
			for (int j = 0; j < rows; j++) {
				for (int i = 0; i < cols; i++) {
					curves[j][i] = new Curve();
				}
			}
		}
	}

	public void reset() {
		for (int j = 0; j < rows; j++) {
			for (int i = 0; i < cols; i++) {
				curves[j][i].reset();
			}
		}
		angle = 0;
		renderingComplete = false;
	}

	private static void setRenderingHints(final Graphics2D g2d) {
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

	}

	private class Listener extends MouseAdapter {

		public Listener() {}

		@Override
		public void mousePressed(final MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				System.exit(-1);
			} else if (e.getButton() == MouseEvent.BUTTON3) {
				reset();
			}
		}

		@Override
		public void mouseWheelMoved(final MouseWheelEvent e) {
			int num = e.getWheelRotation();
			if (num > 0) {
				setW(getW() - 10);
			} else if (num < 0) {
				setW(getW() + 10);
			}
			if (num != 0) {
				reset();
			}
		}
	}
}
