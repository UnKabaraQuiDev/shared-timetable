package lu.kbra.shared_timetable.client.frame.components;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;
import javax.swing.SwingConstants;

import lu.pcy113.pclib.PCUtils;

public class ProgressBarJPanel extends JPanel {

	private int progress = 0; // 0 to 100
	private Color progressColor = Color.GREEN.brighter();
	private int size = 20;
	private int position = SwingConstants.BOTTOM;
	private int direction = SwingConstants.LEADING;

	public ProgressBarJPanel() {
	}

	public ProgressBarJPanel(int position) {
		this.position = position;
	}

	public ProgressBarJPanel(int size, int position) {
		this.size = size;
		this.position = position;
	}

	public ProgressBarJPanel(int size, int position, int direction) {
		this.size = size;
		this.position = position;
		this.direction = direction;
	}

	public ProgressBarJPanel(Color progressColor) {
		this.progressColor = progressColor;
	}

	public ProgressBarJPanel(int progress, Color progressColor, int size, int position) {
		this.progress = progress;
		this.progressColor = progressColor;
		this.size = size;
		this.position = position;
	}

	public ProgressBarJPanel(int progress, Color progressColor, int size, int position, int direction) {
		this.progress = progress;
		this.progressColor = progressColor;
		this.size = size;
		this.position = position;
		this.direction = direction;
	}

	public void setSize(int size) {
		this.size = size;
		repaint();
	}

	public void setPosition(int position) {
		this.position = position;
		repaint();
	}

	public void setProgress(int progress) {
		this.progress = PCUtils.clamp(0, 100, progress);
		repaint();
	}

	public void setProgressColor(Color color) {
		this.progressColor = color;
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.setColor(progressColor);

		switch (position) {
		case SwingConstants.TOP:
			if (direction == SwingConstants.LEADING) {
				g.fillRect(0, 0, getWidth() / 100 * progress, size);
			} else {
				g.fillRect(getWidth() - (getWidth() / 100 * progress), 0, getWidth() / 100 * progress, size);
			}
			break;
		case SwingConstants.BOTTOM:
			if (direction == SwingConstants.LEADING) {
				g.fillRect(0, getHeight() - size, getWidth() / 100 * progress, size);
			} else {
				g.fillRect(getWidth() - (getWidth() / 100 * progress), getHeight() - size, getWidth() / 100 * progress, size);
			}
			break;
		case SwingConstants.RIGHT:
			if (direction == SwingConstants.LEADING) {
				g.fillRect(getWidth() - size, 0, size, getHeight() / 100 * progress);
			} else {
				g.fillRect(getWidth() - size, getHeight() - (getHeight() / 100 * progress), size, getHeight() / 100 * progress);
			}
			break;
		case SwingConstants.LEFT:
			if (direction == SwingConstants.LEADING) {
				g.fillRect(0, 0, size, getHeight() / 100 * progress);
			} else {
				g.fillRect(0, getHeight() - (getHeight() / 100 * progress), size, getHeight() / 100 * progress);
			}
			break;
		}
	}

}
