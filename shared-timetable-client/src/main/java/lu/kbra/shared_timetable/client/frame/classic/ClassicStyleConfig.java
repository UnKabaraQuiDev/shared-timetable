package lu.kbra.shared_timetable.client.frame.classic;

import java.awt.Color;

import org.springframework.stereotype.Component;

import lu.kbra.shared_timetable.client.config.StyleConfig;

@Component
public class ClassicStyleConfig implements StyleConfig {

	private Color main = Color.CYAN, background = Color.WHITE, accent = Color.YELLOW, progress = Color.GREEN.brighter();

	public Color getMain() {
		return main;
	}

	public void setMain(Color main) {
		this.main = main;
	}

	public Color getBackground() {
		return background;
	}

	public void setBackground(Color background) {
		this.background = background;
	}

	public Color getAccent() {
		return accent;
	}

	public void setAccent(Color accent) {
		this.accent = accent;
	}

	public Color getProgress() {
		return progress;
	}

	public void setProgress(Color progress) {
		this.progress = progress;
	}

	@Override
	public String toString() {
		return "ClassicStyleConfig [main=" + main + ", background=" + background + ", accent=" + accent + ", progress=" + progress + "]";
	}

}
