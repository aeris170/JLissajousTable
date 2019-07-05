package main;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

public class Curve {

	private List<Point> path = new ArrayList<>();
	private Point current = new Point();

	public void setX(final float x) {
		current.x = (int) x;
	}

	public void setY(final float y) {
		current.y = (int) y;
	}

	public void addPoint() {
		path.add(current);
	}

	public void reset() {
		path.clear();
	}

	public void show(final Graphics2D g2d) {
		g2d.setColor(Colors.getNextColor());
		g2d.setStroke(new BasicStroke(1));
		final GeneralPath polyline = new GeneralPath(Path2D.WIND_EVEN_ODD, path.size());
		polyline.moveTo(path.get(0).x, path.get(0).y);
		for (int i = 1; i < path.size(); i++) {
			final Point p = path.get(i);
			polyline.lineTo(p.x, p.y);
		}

		g2d.draw(polyline);

		g2d.setStroke(new BasicStroke(8));
		g2d.drawOval(current.x - 1, current.y - 1, 2, 2);
		current = new Point();
	}
}
