package lu.kbra.shared_timetable.client.data;

import java.awt.Color;
import java.awt.FlowLayout;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;

import lu.kbra.shared_timetable.client.frame.components.DelegatingDrawPanel;

public class TimetableEvent {

	public enum Category {
		STUDENTS, TEACHERS, STAFF
	}

	private String name;
	private String location;
	private String rdvLocation;
	private String endLocation;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private List<Category> categories;

	private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");
	private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	public TimetableEvent(String name, String location, String rdvLocation, String endLocation, LocalDateTime startTime, LocalDateTime endTime, List<Category> categories) {
		this.name = name;
		this.location = location;
		this.rdvLocation = rdvLocation;
		this.endLocation = endLocation;
		this.startTime = startTime;
		this.endTime = endTime;
		this.categories = categories;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	// Very compact: only name + start time
	public JComponent getCompactView() {
		JPanel panel = new DelegatingDrawPanel(new FlowLayout(FlowLayout.LEFT));
		panel.add(new JLabel(startTime.format(TIME_FMT)));
		panel.add(new JLabel(name));
		panel.setBackground(Color.BLUE);
		return panel;
	}

	// Medium detail: name + start time + rdvLocation + location + endLocation
	public JComponent getInfoView() {
		final JPanel panel = new DelegatingDrawPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(new JLabel(name + " (" + startTime.format(TIME_FMT) + ")"));
		panel.add(new JLabel("RDV: " + rdvLocation));
		panel.add(new JLabel("Location: " + location));
		panel.add(new JLabel("End: " + endLocation));
		panel.setBackground(Color.BLUE);
		return panel;
	}

	// Full detail: name + time range + location + end location + progress bar
	public JComponent getFullView() {
		final JPanel panel = new DelegatingDrawPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		panel.add(new JLabel(name));
		panel.add(new JLabel(startTime.format(TIME_FMT) + " - " + endTime.format(TIME_FMT) + " (" + startTime.format(DATE_FMT) + ")"));
		panel.add(new JLabel("Location: " + location));
		panel.add(new JLabel("End location: " + endLocation));

		// Progress bar
		JProgressBar progressBar = new JProgressBar(0, 100);
		updateProgressBar(progressBar);
		// Timer to refresh progress bar
		new Timer(1000, e -> updateProgressBar(progressBar)).start();
		panel.add(progressBar);
		panel.setBackground(Color.BLUE);

		return panel;
	}

	private void updateProgressBar(JProgressBar bar) {
		LocalDateTime now = LocalDateTime.now();
		if (now.isBefore(startTime)) {
			bar.setValue(0);
		} else if (now.isAfter(endTime)) {
			bar.setValue(100);
		} else {
			long totalSeconds = Duration.between(startTime, endTime).getSeconds();
			long elapsedSeconds = Duration.between(startTime, now).getSeconds();
			int progress = (int) ((elapsedSeconds * 100) / totalSeconds);
			bar.setValue(progress);
		}
	}
}
