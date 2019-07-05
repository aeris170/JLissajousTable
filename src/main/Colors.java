package main;

import java.awt.Color;

public final class Colors {

	private static Color[] colors = new Color[] { new Color(0xFF0000), new Color(0xFF7F00), new Color(0xFFFF00), new Color(0x00FF00), new Color(0x0000FF),
	        new Color(0x4B0082), new Color(0x9400D3) };
	private static int i = 0;

	private Colors() {}

	public static Color getNextColor() {
		return colors[i++ % colors.length];
	}

	public static void reset() {
		i = 0;
	}

	public static void set(int i) {
		Colors.i = i % colors.length;
	}
}
