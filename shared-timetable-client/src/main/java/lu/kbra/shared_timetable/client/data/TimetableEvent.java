package lu.kbra.shared_timetable.client.data;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import lu.kbra.shared_timetable.client.frame.components.RoundedProgressPanel;
import lu.kbra.shared_timetable.client.utils.DurationUtils;
import lu.kbra.shared_timetable.client.utils.JLabelBuilder;

public class TimetableEvent {

	public static final int PADDING = 10;
	public static final int MARGIN = 5;
	public static final int UPCOMING_MINUTES = 30;

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

	public RoundedProgressPanel getCompactView() {
		final RoundedProgressPanel panel = new RoundedProgressPanel();
		
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(new JLabelBuilder(startTime.format(TIME_FMT) + " - " + endTime.format(TIME_FMT)).font(new Font("Arial", Font.PLAIN, 20)).build());
		panel.add(new JLabelBuilder(name).font(new Font("Arial", Font.BOLD, 20)).build());
		panel.setBackground(Color.CYAN);
		panel.setPreferredSize(new Dimension(500, 150));
		
		return panel;
	}

	public RoundedProgressPanel getUpcomingView() {
		final RoundedProgressPanel panel = new RoundedProgressPanel();
		panel.setLayout(new BorderLayout());

		panel.setProgressColor(Color.GREEN.brighter());
		panel.setVertical();
		panel.setProgress(getStartDuration() * 100 / UPCOMING_MINUTES);

		final JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setOpaque(false);
		topPanel.add(new JLabelBuilder(name).font(new Font("Arial", Font.BOLD, 30)).build(), BorderLayout.WEST);
		topPanel.add(new JLabelBuilder(startTime.format(TIME_FMT) + " - " + endTime.format(TIME_FMT)).font(new Font("Arial", Font.BOLD, 30)).build(), BorderLayout.EAST);
		panel.add(topPanel, BorderLayout.NORTH);

		final JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
		centerPanel.setOpaque(false);

		centerPanel.add(Box.createVerticalGlue());
		centerPanel.add(new JLabelBuilder(location).font(new Font("Arial", Font.PLAIN, 24)).build());

		if (categories != null && !categories.isEmpty()) {
			String categoriesText = categories.stream().map(Enum::name).reduce((a, b) -> a + ", " + b).orElse("");
			centerPanel.add(new JLabelBuilder(categoriesText).font(new Font("Arial", Font.ITALIC, 20)).build());
		}
		centerPanel.add(Box.createVerticalGlue());

		panel.add(centerPanel, BorderLayout.CENTER);

		panel.add(new JLabelBuilder(DurationUtils.formatDuration(getStartTime())).font(new Font("Arial", Font.BOLD, 38)).horizontalAlignment(JLabel.CENTER).build(), BorderLayout.SOUTH);

		return panel;
	}

	// Full detail: name + time range + location + end location + progress bar
	public RoundedProgressPanel getOngoingView() {
		final RoundedProgressPanel panel = getUpcomingView();

		panel.setProgressColor(Color.GREEN.brighter());
		panel.setProgress(getElapsedDuration() * 100 / getTotalDuration());
		panel.setHorizontal();

		final JPanel bottomPanel = new JPanel(new BorderLayout());

		bottomPanel.setOpaque(false);

		bottomPanel.add(new JLabelBuilder(DurationUtils.formatDuration(getStartTime())).font(new Font("Arial", Font.BOLD, 38)).build(), BorderLayout.WEST);
		bottomPanel.add(new JLabelBuilder(DurationUtils.formatDuration(getEndTime())).font(new Font("Arial", Font.BOLD, 38)).build(), BorderLayout.EAST);

		panel.add(bottomPanel, BorderLayout.SOUTH);

		return panel;
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
		return LocalDateTime.now().isAfter(getUpcomingTime()) && !this.isOngoing();
	}

	public int getTotalDuration() {
		return (int) Duration.between(this.getStartTime(), this.getEndTime()).toMinutes();
	}

	public int getElapsedDuration() {
		return (int) Duration.between(LocalDateTime.now(), this.getEndTime()).toMinutes();
	}

	public int getRemainingDuration() {
		return (int) Duration.between(LocalDateTime.now(), this.getStartTime()).toMinutes();
	}

	public int getUpcomingDuration() {
		return (int) Duration.between(LocalDateTime.now(), this.getUpcomingTime()).toMinutes();
	}

	public int getStartDuration() {
		return (int) Duration.between(LocalDateTime.now(), this.getStartTime()).toMinutes();
	}

	/**
	 * should flash if the event transitions to upcoming (10s after) or from upcoming to ongoing (20s before ongoing)
	 */
	public boolean shouldFlash() {
		final LocalDateTime now = LocalDateTime.now();
		return (isUpcoming() && Math.abs(Duration.between(now, getUpcomingTime()).toSeconds()) <= 10) || (Math.abs(Duration.between(now, getStartTime()).toSeconds()) <= 20);
	}

	public LocalDateTime getUpcomingTime() {
		return this.getStartTime().minusMinutes(UPCOMING_MINUTES);
	}

	public static final int CORNER_RADIUS = 20;

	public JPanel getCellComponent() {
		RoundedProgressPanel comp;

		if (isOngoing()) { // currently ongoing
			comp = this.getOngoingView();
		} else if (isUpcoming()) { // starting soon (within 30 minutes)
			comp = this.getUpcomingView();
		} else {
			comp = this.getCompactView();
		}

		comp.setBackground(Color.CYAN);

		if (this.categories.contains(Category.STUDENTS)) {
			comp.setBackground(Color.YELLOW);
		}

		comp.setBorder(new EmptyBorder(PADDING, PADDING, PADDING, PADDING));

		final JPanel ret = new JPanel(new BorderLayout());
		ret.setPreferredSize(new Dimension(comp.getPreferredSize().width + MARGIN * 2, comp.getPreferredSize().height + MARGIN * 2));
		ret.add(comp, BorderLayout.CENTER);
		ret.setMaximumSize(new Dimension(500, ret.getPreferredSize().height));
		ret.setBorder(new EmptyBorder(MARGIN, MARGIN, MARGIN, MARGIN));

		return ret;
	}
}
