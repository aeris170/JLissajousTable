package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.JPanel;
import javax.swing.Timer;

public class LissajousPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final double HALF_PI = Math.PI / 2;
	private static final Color LINE_COLOR = new Color(255, 255, 255, 150);
	private static final Font TEXT_FONT = new Font("Courier New", Font.BOLD, 20);

	private Dimension parentSize;
	private Listener l = new Listener();

	float angle = 0;
	// try changing this and see what happens. Recommended: 20, 80, 120, 200. Use
	// the Mouse Wheel.
	int w = 200;
	int cols;
	int rows;
	transient Curve[][] curves;
	boolean isRenderingComplete = false;
	boolean isDashEnabled = false;
	boolean isTextVisible = true;
	float dashPhase = 0;
	float dashSpeed = 0;
	Quality quality = Quality.FASTEST;

	public LissajousPanel() {
		parentSize = Window.SCREEN_SIZE;
		init();

		new Timer(1, e -> {
			dashPhase += dashSpeed;
		}).start();
		setFocusable(true);
		requestFocusInWindow();
		setBackground(Color.BLACK);
		addKeyListener(l);
		addMouseListener(l);
		addMouseWheelListener(l);
	}

	@Override
	public void paintComponent(final Graphics g) {
		final Graphics2D g2d = (Graphics2D) g;

		setQuality(g2d, quality);

		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, getWidth(), getHeight());

		if (isTextVisible) {
			g2d.setFont(TEXT_FONT);
			FontMetrics fm = g2d.getFontMetrics();
			int height = fm.getHeight();

			Colors.reset();

			g2d.setColor(Colors.getNextColor());
			String s = "ESC or LMB: EXIT";
			g2d.drawString(s, 0, height);
			g.drawLine(0, height + 2, fm.stringWidth(s), height + 2);
			height += fm.getHeight();

			g2d.setColor(Colors.getNextColor());
			s = "RMB: Restart Render";
			g2d.drawString(s, 0, height);
			g.drawLine(0, height + 2, fm.stringWidth(s), height + 2);
			height += fm.getHeight();

			g2d.setColor(Colors.getNextColor());
			s = "MWHEEL: Zoom +/-";
			g2d.drawString(s, 0, height);
			g.drawLine(0, height + 2, fm.stringWidth(s), height + 2);
			height += fm.getHeight();

			g2d.setColor(Colors.getNextColor());
			s = "Current Zoom: " + w;
			g2d.drawString(s, 0, height);
			g.drawLine(0, height + 2, fm.stringWidth(s), height + 2);
			height += fm.getHeight();

			g2d.setColor(Colors.getNextColor());
			s = "Q: Change Quality";
			g2d.drawString(s, 0, height);
			g.drawLine(0, height + 2, fm.stringWidth(s), height + 2);
			height += fm.getHeight();

			g2d.setColor(Colors.getNextColor());
			s = "Current Quality: " + quality;
			g2d.drawString(s, 0, height);
			g.drawLine(0, height + 2, fm.stringWidth(s), height + 2);
			height += fm.getHeight();

			g2d.setColor(Colors.getNextColor());
			s = "D: Toggle Dashed Render";
			g2d.drawString(s, 0, height);
			g.drawLine(0, height + 2, fm.stringWidth(s), height + 2);
			height += fm.getHeight();

			g2d.setColor(Colors.getNextColor());
			s = "UP/DOWN Arrow: Change Dash Speed";
			g2d.drawString(s, 0, height);
			g.drawLine(0, height + 2, fm.stringWidth(s), height + 2);
			height += fm.getHeight();

			g2d.setColor(Colors.getNextColor());
			s = "Current Dash Speed: " + dashSpeed;
			g2d.drawString(s, 0, height);
			g.drawLine(0, height + 2, fm.stringWidth(s), height + 2);
			height += fm.getHeight();

			g2d.setColor(Colors.getNextColor());
			s = "SPACE: Show/Hide Text";
			g2d.drawString(s, 0, height);
			g.drawLine(0, height + 2, fm.stringWidth(s), height + 2);
		}

		if (!isRenderingComplete) {
			quality = Quality.FASTEST; // render on fastest setting
			final float d = w * 0.8f;
			final float r = d / 2;
			Colors.reset();
			for (int i = 0; i < cols; i++) {
				final float cx = w + i * w + w / 2f;
				final float cy = w / 2f;
				g2d.setStroke(new BasicStroke(1));
				g2d.setColor(Colors.getNextColor());
				g2d.drawOval((int) (cx - d / 2), (int) (cy - d / 2), (int) d, (int) d);
				final float x = (float) (r * Math.cos(Math.toRadians(angle * (i + 1)) - HALF_PI));
				final float y = (float) (r * Math.sin(Math.toRadians(angle * (i + 1)) - HALF_PI));
				g2d.setStroke(new BasicStroke(8));
				g2d.drawOval((int) (cx + x) - 1, (int) (cy + y) - 1, 2, 2);
				g2d.setColor(LINE_COLOR);
				g2d.setStroke(new BasicStroke(1));
				g2d.drawLine((int) (cx + x), 0, (int) (cx + x), parentSize.height);
				for (int j = 0; j < rows; j++) {
					curves[j][i].setX(cx + x);
				}
			}
			Colors.reset();
			for (int j = 0; j < rows; j++) {
				final float cx = w / 2f;
				final float cy = w + j * w + w / 2f;
				g2d.setStroke(new BasicStroke(1));
				g2d.setColor(Colors.getNextColor());
				g2d.drawOval((int) (cx - d / 2), (int) (cy - d / 2), (int) d, (int) d);
				final float x = (float) (r * Math.cos(Math.toRadians(angle * (j + 1)) - HALF_PI));
				final float y = (float) (r * Math.sin(Math.toRadians(angle * (j + 1)) - HALF_PI));
				g2d.setStroke(new BasicStroke(8));
				g2d.drawOval((int) (cx + x) - 1, (int) (cy + y) - 1, 2, 2);
				g2d.setColor(LINE_COLOR);
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
		if (isRenderingComplete) { // for consistent random colors, very bad design!! :D
			Colors.reset();
			Colors.set(rows);
		}
		for (int j = 0; j < rows; j++) {
			for (int i = 0; i < cols; i++) {
				curves[j][i].show(g2d, isDashEnabled, dashPhase);
			}
		}
		if (angle < -360) {
			if (!isRenderingComplete) {
				quality = Quality.BEST; // execute once the render has finished
			}
			isRenderingComplete = true;
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
		isRenderingComplete = false;
	}

	private static void setQuality(final Graphics2D g2d, Quality q) {
		if (q == Quality.BEST) {
			g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
			g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		} else if (q == Quality.BALANCED) {
			g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
			g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		} else if (q == Quality.FASTEST) {
			g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
			g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
			g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
			g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		}
	}

	private class Listener extends MouseAdapter implements KeyListener {

		private boolean escPressed = false;

		public Listener() {}

		@Override
		public void mousePressed(final MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				System.exit(0);
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

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_Q) {
				quality = quality.next();
			}
			if (e.getKeyCode() == KeyEvent.VK_D) {
				isDashEnabled = !isDashEnabled;
			}
			if (e.getKeyCode() == KeyEvent.VK_UP) {
				dashSpeed += 0.05f;
			}
			if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				dashSpeed -= 0.05f;
			}
			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				isTextVisible = !isTextVisible;
			}
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				escPressed = true;
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			if (escPressed) {
				System.exit(0);
			}
		}

		@Override
		public void keyTyped(KeyEvent e) {}
	}
}
