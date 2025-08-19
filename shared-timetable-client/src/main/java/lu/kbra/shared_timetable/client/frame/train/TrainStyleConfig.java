package lu.kbra.shared_timetable.client.frame.train;

import java.awt.Color;

import org.springframework.stereotype.Component;

import lu.kbra.shared_timetable.client.config.StyleConfig;

@Component
public class TrainStyleConfig implements StyleConfig {

	private Color background = Color.BLACK, foreground = Color.WHITE, flashColor = Color.RED, progressColor = Color.GREEN;
	private boolean switchColor = true, flashEnabled = true;

	public Color getBackground() {
		return background;
	}

	public void setBackground(Color background) {
		this.background = background;
	}

	public Color getForeground() {
		return foreground;
	}

	public void setForeground(Color foreground) {
		this.foreground = foreground;
	}

	public boolean isSwitchColor() {
		return switchColor;
	}

	public void setSwitchColor(boolean switchColor) {
		this.switchColor = switchColor;
	}

	public boolean isFlashEnabled() {
		return flashEnabled;
	}

	public void setFlashEnabled(boolean flashEnabled) {
		this.flashEnabled = flashEnabled;
	}

	public Color getFlashColor() {
		return flashColor;
	}

	public void setFlashColor(Color flashColor) {
		this.flashColor = flashColor;
	}

	public Color getProgressColor() {
		return progressColor;
	}

	public void setProgressColor(Color progressColor) {
		this.progressColor = progressColor;
	}

	@Override
	public String toString() {
		return "TrainStyleConfig [background=" + background + ", foreground=" + foreground + ", flashColor=" + flashColor
				+ ", progressColor=" + progressColor + ", switchColor=" + switchColor + ", flashEnabled=" + flashEnabled + "]";
	}

}
