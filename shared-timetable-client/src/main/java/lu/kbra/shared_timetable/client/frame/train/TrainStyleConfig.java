package lu.kbra.shared_timetable.client.frame.train;

import java.awt.Color;

import org.springframework.stereotype.Component;

import lu.kbra.shared_timetable.client.config.StyleConfig;

@Component
public class TrainStyleConfig implements StyleConfig {

	private Color background = Color.BLACK, foreground = Color.WHITE;
	private boolean switchColor = true;

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

	@Override
	public String toString() {
		return "TrainStyleConfig [background=" + background + ", foreground=" + foreground + ", switchColor=" + switchColor + "]";
	}

}
