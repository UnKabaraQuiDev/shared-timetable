package lu.kbra.shared_timetable.client.data;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
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
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import lu.kbra.shared_timetable.client.frame.components.DelegatingDrawPanel;
import lu.kbra.shared_timetable.client.utils.JLabelBuilder;

public class TimetableEvent {

	public enum Category {
		STUDENTS, TEACHERS, STAFF
	}

	private String name;
	private String location;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private List<Category> categories;

	private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");
	private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	public TimetableEvent(String name, String location, String rdvLocation, String endLocation, LocalDateTime startTime, LocalDateTime endTime, List<Category> categories) {
		this.name = name;
		this.location = location;
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
		panel.add(new JLabel(location));
		panel.setBackground(Color.BLUE);
		return panel;
	}

	// Full detail: name + time range + location + end location + progress bar
	public JComponent getFullView() {
		final JPanel panel = new DelegatingDrawPanel();
		panel.setLayout(new BorderLayout());

		final JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setOpaque(false);
		topPanel.add(new JLabelBuilder(name).font(new Font("Arial", Font.BOLD, 30)).build(), BorderLayout.WEST);
		topPanel.add(new JLabelBuilder(startTime.format(TIME_FMT) + " - " + endTime.format(TIME_FMT)).font(new Font("Arial", Font.BOLD, 30)).build(), BorderLayout.EAST);
		panel.add(topPanel, BorderLayout.NORTH);

		panel.add(new JLabelBuilder(location).font(new Font("Arial", Font.PLAIN, 24)).build(), BorderLayout.CENTER);

		// Progress bar
		JProgressBar progressBar = new JProgressBar(0, 100);
		updateProgressBar(progressBar);
		// Timer to refresh progress bar
		new Timer(1000, e -> updateProgressBar(progressBar)).start();
		panel.add(progressBar, BorderLayout.SOUTH);
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

	public boolean isPast() {
		return LocalDateTime.now().isAfter(this.getEndTime());
	}

	public boolean isOngoing() {
		LocalDateTime now = LocalDateTime.now();
		return !now.isBefore(this.getStartTime()) && !now.isAfter(this.getEndTime());
	}

	/**
	 * if the event starts in < 30 minutes
	 */
	public boolean isUpcoming() {
		return Duration.between(LocalDateTime.now(), this.getStartTime()).toMinutes() <= 30 && !this.isOngoing();
	}

	public static final int CORNER_RADIUS = 20;

	public Component getCellComponent() {
		final LocalDateTime now = LocalDateTime.now();
		final long minsToStart = Duration.between(now, this.getStartTime()).toMinutes();

		JComponent comp;

		if (!now.isBefore(this.getStartTime()) && !now.isAfter(this.getEndTime())) { // currently ongoing
			comp = this.getFullView();
		} else if (minsToStart <= 30) { // starting soon (within 30 minutes)
			comp = this.getInfoView();
		} else {
			comp = this.getCompactView();
		}

		if (comp instanceof DelegatingDrawPanel ddp) {
			if (ddp.getDrawFunction() == null) {
				ddp.setDrawFunction((g, p) -> {
					Graphics2D g2 = (Graphics2D) g.create();

					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

					g2.setColor(p.getBackground());
					g2.fillRoundRect(0, 0, p.getWidth(), p.getHeight(), CORNER_RADIUS, CORNER_RADIUS);

					g2.dispose();
				});
			}
		}

		comp.setBackground(Color.CYAN);

		if (this.categories.contains(Category.STUDENTS)) {
			comp.setBackground(Color.YELLOW);
		}

		comp.setBorder(new EmptyBorder(CORNER_RADIUS, CORNER_RADIUS, CORNER_RADIUS, CORNER_RADIUS));

		final JPanel ret = new JPanel(new BorderLayout());
		ret.add(comp, BorderLayout.CENTER);
		ret.setMaximumSize(new Dimension(500, 1000));
		ret.setBorder(new EmptyBorder(CORNER_RADIUS, CORNER_RADIUS, CORNER_RADIUS, CORNER_RADIUS));

		return ret;
	}
}
