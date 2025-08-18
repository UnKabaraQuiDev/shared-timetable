package lu.kbra.shared_timetable.client.config;

import java.awt.Color;

public class TimetableFrameConfig {

	private String style;
	private Color main, background, accent;

	public String getStyle() {
		return style;
	}

	public Color getMain() {
		return main;
	}

	public void setMain(Color main) {
		if (this.main == null)
			this.main = main;
	}

	public Color getBackground() {
		return background;
	}

	public void setBackground(Color background) {
		if (this.background == null)
			this.background = background;
	}

	public Color getAccent() {
		return accent;
	}

	public void setAccent(Color accent) {
		if (this.accent == null)
			this.accent = accent;
	}

	@Override
	public String toString() {
		return "TimetableFrameConfig [style=" + style + ", main=" + main + ", background=" + background + ", accent=" + accent + "]";
	}

}
