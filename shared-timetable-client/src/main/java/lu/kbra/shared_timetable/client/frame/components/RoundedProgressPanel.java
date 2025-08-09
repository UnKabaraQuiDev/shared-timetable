package lu.kbra.shared_timetable.client.frame.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JPanel;

import lu.pcy113.pclib.PCUtils;

public class RoundedProgressPanel extends JPanel {

	private static final int CORNER_RADIUS = 20, BORDER_WIDTH = 5;
	private int progress = 0; // 0 to 100
	private boolean vertical = false; // false = horizontal
	private Color progressColor = Color.GREEN.brighter();
	private Color borderColor = null;

	public void setProgress(int progress) {
		this.progress = PCUtils.clamp(0, 100, progress);
		repaint();
	}

	public void setVertical(boolean vertical) {
		this.vertical = vertical;
		repaint();
	}

	public void setVertical() {
		setVertical(true);
	}

	public void setHorizontal() {
		setVertical(false);
	}

	public void setProgressColor(Color color) {
		this.progressColor = color;
		repaint();
	}

	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
		repaint();
	}

	public void clearBorderColor() {
		this.borderColor = null;
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		// super.paintComponent(g);

		g.setColor(getParent().getBackground());
		g.fillRect(0, 0, getWidth(), getHeight()); // Clear background

		int width = getWidth();
		int height = getHeight();

		final Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Draw background with rounded corners
		g2.setColor(borderColor != null ? borderColor : getBackground());
		g2.fillRoundRect(0, 0, width, height, CORNER_RADIUS, CORNER_RADIUS);

		// Create clipping shape - the rounded rectangle area
		final Shape clip = new RoundRectangle2D.Float(BORDER_WIDTH, BORDER_WIDTH, width - BORDER_WIDTH * 2, height - BORDER_WIDTH * 2, Math.abs(CORNER_RADIUS - BORDER_WIDTH), Math.abs(CORNER_RADIUS - BORDER_WIDTH));
		g2.setClip(clip);

		if (borderColor != null) { // fill internal rect
			g2.setColor(getBackground());
			g2.fill(clip);
		}

		// Calculate progress bar fill area
		if (vertical) {
			int fillHeight = (int) (height * (progress / 100.0));
			// Draw from bottom up
			g2.setColor(progressColor);
			g2.fillRect(0, height - fillHeight, width, fillHeight);
		} else {
			int fillWidth = (int) (width * (progress / 100.0));
			// Draw from left to right
			g2.setColor(progressColor);
			g2.fillRect(0, 0, fillWidth, height);
		}

		g2.dispose();
	}

}
