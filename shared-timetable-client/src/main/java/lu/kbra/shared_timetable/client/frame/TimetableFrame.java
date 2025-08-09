package lu.kbra.shared_timetable.client.frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import lu.kbra.shared_timetable.client.data.TimetableEvent;
import lu.kbra.shared_timetable.client.frame.components.RoundedProgressPanel;
import lu.kbra.shared_timetable.client.utils.JLabelBuilder;
import lu.pcy113.pclib.PCUtils;

public class TimetableFrame extends JFrame {

	private static final int UPCOMING_EVENT_COLUMNS = 5;
	private static final int DATE_FONT_STYLE = 100;

	private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

	private JLabel timeLabel, dateLabel;
	private JPanel currentEventList, upcomingEventColumns;
	private List<TimetableEvent> events;
	private Timer timer;

	public TimetableFrame(List<TimetableEvent> events) {
		this.events = events;
		setTitle("Timetable Display");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		final JPanel northPanel = new JPanel(new BorderLayout());
		northPanel.setPreferredSize(new Dimension(0, 400));

		final JPanel timeDatePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

		// Time label (left)
		timeLabel = new JLabel();
		timeLabel.setFont(new Font("Arial", Font.BOLD, DATE_FONT_STYLE));
		timeDatePanel.add(timeLabel, BorderLayout.WEST);

		timeDatePanel.add(new JLabelBuilder(" | ").font(new Font("Arial", Font.BOLD, DATE_FONT_STYLE)).build());

		// Date label (right)
		dateLabel = new JLabel();
		dateLabel.setFont(new Font("Arial", Font.BOLD, DATE_FONT_STYLE));
		timeDatePanel.add(dateLabel, BorderLayout.EAST);

		northPanel.add(timeDatePanel, BorderLayout.NORTH);

		// Events display (center, top-left)
		currentEventList = new JPanel();
		currentEventList.setLayout(new BoxLayout(currentEventList, BoxLayout.X_AXIS));
		northPanel.add(currentEventList, BorderLayout.CENTER);

		add(northPanel, BorderLayout.NORTH);

		// Upcoming events display (center, bottom-left)
		upcomingEventColumns = new JPanel();
		upcomingEventColumns.setLayout(new GridLayout(1, UPCOMING_EVENT_COLUMNS));
		for (int i = 0; i < UPCOMING_EVENT_COLUMNS; i++) {
			final JPanel column = new JPanel();
			column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
			upcomingEventColumns.add(column);
		}
		add(upcomingEventColumns, BorderLayout.CENTER);

		// Update every second
		timer = new Timer(1000, e -> updateUIContents());
		timer.start();

		setSize(600, 400);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private boolean flash = false;

	private void updateUIContents() {
		flash = !flash;

		// Update clock
		final LocalDateTime now = LocalDateTime.now();
		timeLabel.setText(now.format(TIME_FMT));
		dateLabel.setText(now.format(DATE_FMT));

		events.removeIf(e -> e.isPast());

		currentEventList.removeAll();
		events.stream().filter(e -> e.isOngoing() || e.isUpcoming()).sorted((e1, e2) -> e1.getStartTime().compareTo(e2.getStartTime())).forEach(e -> {
			final JPanel cell = e.getCellComponent();
			cell.setMaximumSize(new Dimension(cell.getMaximumSize().width, Short.MAX_VALUE));
			cell.setAlignmentY(Component.TOP_ALIGNMENT);

			final RoundedProgressPanel progressPanel = (RoundedProgressPanel) cell.getComponent(0);

			if (e.shouldFlash() && flash) {
				progressPanel.setBorderColor(Color.RED);
			}

			currentEventList.add(cell);
		});
		currentEventList.revalidate();
		currentEventList.repaint();

		Arrays.stream(upcomingEventColumns.getComponents()).filter(p -> p instanceof JPanel).map(PCUtils::<JPanel>cast).forEach(p -> p.removeAll());
		final Map<LocalDate, List<TimetableEvent>> upcomingEventsByDate = events.stream().filter(e -> !(e.isOngoing() || e.isUpcoming())).sorted((e1, e2) -> e1.getStartTime().compareTo(e2.getStartTime()))
				.collect(Collectors.groupingBy(e -> e.getStartTime().toLocalDate()));
		final List<LocalDate> sortedDates = upcomingEventsByDate.keySet().stream().sorted().limit(UPCOMING_EVENT_COLUMNS).toList();

		for (int i = 0; i < sortedDates.size(); i++) {
			final LocalDate date = sortedDates.get(i);
			final JPanel columnPanel = (JPanel) upcomingEventColumns.getComponent(i);
			columnPanel.removeAll();

			columnPanel.add(new JLabelBuilder(date.format(DATE_FMT)).font(new Font("Arial", Font.BOLD, 20)).build());

			final List<TimetableEvent> dayEvents = upcomingEventsByDate.get(date);

			final LocalDateTime earliest = dayEvents.get(0).getStartTime();
			final LocalDateTime latest = dayEvents.stream().map(TimetableEvent::getEndTime).max(LocalDateTime::compareTo).orElse(earliest);

			for (TimetableEvent event : dayEvents) {
				final JPanel cell = event.getCellComponent();
				final RoundedProgressPanel panel = (RoundedProgressPanel) cell.getComponent(0);

				/*
				 * long totalRangeSeconds = Duration.between(earliest, latest).getSeconds();
				 * 
				 * long eventOffset = Duration.between(earliest, event.getStartTime()).getSeconds(); double eventRatio = eventOffset /
				 * (double) totalRangeSeconds;
				 * 
				 * long nowOffset = Duration.between(earliest, LocalDateTime.now()).getSeconds(); double nowRatio = nowOffset / (double)
				 * totalRangeSeconds;
				 * 
				 * panel.setHorizontal(); panel.setProgressColor(Color.GREEN.brighter()); panel.setProgress((int) ((eventRatio -
				 * nowRatio) * 100));
				 */

				columnPanel.add(cell);
			}
		}

		upcomingEventColumns.revalidate();
		upcomingEventColumns.repaint();
	}

	public static void main(String[] args) {
		// Example usage with dummy events
		List<TimetableEvent> dummyEvents = Arrays.asList(
				new TimetableEvent("next", "Room 101", "Main Entrance", "Library", LocalDateTime.now().plusMinutes(TimetableEvent.UPCOMING_MINUTES).plusSeconds(10), LocalDateTime.now().plusMinutes(60),
						List.of(TimetableEvent.Category.STUDENTS)),

				new TimetableEvent("Math", "Room 101", "Main Entrance", "Library", LocalDateTime.now().minusMinutes(10), LocalDateTime.now().plusMinutes(20), List.of(TimetableEvent.Category.STUDENTS)),
				new TimetableEvent("Physics", "Lab 2", "Side Gate", "Cafeteria", LocalDateTime.now().plusMinutes(20), LocalDateTime.now().plusMinutes(50), List.of(TimetableEvent.Category.STUDENTS, TimetableEvent.Category.TEACHERS)),
				new TimetableEvent("Chemistry", "Room 202", "Main Entrance", "Gym", LocalDateTime.now().plusMinutes(40), LocalDateTime.now().plusMinutes(70), List.of(TimetableEvent.Category.STUDENTS)),
				new TimetableEvent("Biology", "Room 303", "Back Door", "Auditorium", LocalDateTime.now().plusSeconds(30), LocalDateTime.now().plusMinutes(35), List.of(TimetableEvent.Category.TEACHERS)),
				new TimetableEvent("Biology 1", "Room 303", "Back Door", "Auditorium", LocalDateTime.now().plusMinutes(35), LocalDateTime.now().plusMinutes(35), List.of(TimetableEvent.Category.TEACHERS)),
				new TimetableEvent("Biology 2 ", "Room 303", "Back Door", "Auditorium", LocalDateTime.now().plusMinutes(40), LocalDateTime.now().plusMinutes(35), List.of(TimetableEvent.Category.TEACHERS)),
				new TimetableEvent("Biology 3", "Room 303", "Back Door", "Auditorium", LocalDateTime.now().plusMinutes(37), LocalDateTime.now().plusMinutes(35), List.of(TimetableEvent.Category.TEACHERS)),

				new TimetableEvent("History", "Room 404", "Main Entrance", "Library", LocalDateTime.now().plusMinutes(60), LocalDateTime.now().plusMinutes(90), List.of(TimetableEvent.Category.STUDENTS)),
				new TimetableEvent("Biology 1", "Room 303", "Back Door", "Auditorium", LocalDateTime.now().plusMinutes(35), LocalDateTime.now().plusMinutes(35), List.of(TimetableEvent.Category.TEACHERS)),
				new TimetableEvent("Biology 2 ", "Room 303", "Back Door", "Auditorium", LocalDateTime.now().plusMinutes(40), LocalDateTime.now().plusMinutes(35), List.of(TimetableEvent.Category.TEACHERS)),
				new TimetableEvent("Biology 3", "Room 303", "Back Door", "Auditorium", LocalDateTime.now().plusMinutes(37), LocalDateTime.now().plusMinutes(35), List.of(TimetableEvent.Category.TEACHERS)),

				new TimetableEvent("History", "Room 404", "Main Entrance", "Library", LocalDateTime.now().plusMinutes(60).plusDays(1), LocalDateTime.now().plusMinutes(90), List.of(TimetableEvent.Category.STUDENTS)),
				new TimetableEvent("Biology 1", "Room 303", "Back Door", "Auditorium", LocalDateTime.now().plusMinutes(35).plusDays(1), LocalDateTime.now().plusMinutes(35), List.of(TimetableEvent.Category.TEACHERS)),
				new TimetableEvent("Biology 2 ", "Room 303", "Back Door", "Auditorium", LocalDateTime.now().plusMinutes(40).plusDays(2), LocalDateTime.now().plusMinutes(35), List.of(TimetableEvent.Category.TEACHERS)),
				new TimetableEvent("Biology 3", "Room 303", "Back Door", "Auditorium", LocalDateTime.now().plusMinutes(37).plusDays(4), LocalDateTime.now().plusMinutes(35), List.of(TimetableEvent.Category.TEACHERS)),

				new TimetableEvent("History", "Room 404", "Main Entrance", "Library", LocalDateTime.now().plusMinutes(60), LocalDateTime.now().plusMinutes(90), List.of(TimetableEvent.Category.STUDENTS)));
		// populate dummyEvents with test data
		SwingUtilities.invokeLater(() -> new TimetableFrame(dummyEvents));
	}
}
