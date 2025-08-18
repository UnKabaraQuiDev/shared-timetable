package lu.kbra.shared_timetable.client.frame;

import javax.swing.JFrame;

public abstract class AbstractTimetableJFrame extends JFrame implements AbstractTimetableFrame {

	protected String name;

	public AbstractTimetableJFrame() {
		super("no name.");
	}

	@Override
	public void setName(String name) {
		this.name = name;
		super.setTitle("Timetable Display (" + name + ")");
	}

	@Override
	public String getName() {
		return name;
	}

}
