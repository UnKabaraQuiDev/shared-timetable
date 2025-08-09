package lu.kbra.shared_timetable.client.frame;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import lu.kbra.shared_timetable.client.data.TimetableEvent;
import lu.kbra.shared_timetable.client.utils.JLabelBuilder;

public class TimetableFrame extends JFrame {

	private JLabel timeLabel, dateLabel;
	private JPanel currentEventList, upcomingEventList;
	private List<TimetableEvent> events;
	private Timer timer;

	public TimetableFrame(List<TimetableEvent> events) {
		this.events = events;
		setTitle("Timetable Display");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		final JPanel northPanel = new JPanel(new BorderLayout());

		final JPanel timeDatePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

		// Time label (left)
		timeLabel = new JLabel();
		timeLabel.setFont(new Font("Arial", Font.BOLD, 50));
		timeDatePanel.add(timeLabel, BorderLayout.WEST);

		timeDatePanel.add(new JLabelBuilder(" | ").font(new Font("Arial", Font.BOLD, 50)).build());

		// Date label (right)
		dateLabel = new JLabel();
		dateLabel.setFont(new Font("Arial", Font.PLAIN, 50));
		timeDatePanel.add(dateLabel, BorderLayout.EAST);

		northPanel.add(timeDatePanel, BorderLayout.NORTH);

		// Events display (center, top-left)
		currentEventList = new JPanel();
		currentEventList.setLayout(new BoxLayout(currentEventList, BoxLayout.X_AXIS));
		northPanel.add(currentEventList, BorderLayout.CENTER);

		add(northPanel, BorderLayout.NORTH);

		// Upcoming events display (center, bottom-left)
		upcomingEventList = new JPanel();
		upcomingEventList.setLayout(new GridLayout(0, 4));
		add(upcomingEventList, BorderLayout.CENTER);

		// Update every second
		timer = new Timer(1000, e -> updateUIContents());
		timer.start();

		setSize(600, 400);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void updateUIContents() {
		// Update clock
		LocalDateTime now = LocalDateTime.now();
		timeLabel.setText(now.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
		dateLabel.setText(now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

		events.removeIf(e -> e.isPast());

		currentEventList.removeAll();
		events.stream().filter(e -> e.isOngoing() || e.isUpcoming()).sorted((e1, e2) -> e1.getStartTime().compareTo(e2.getStartTime())).forEach(e -> currentEventList.add(e.getCellComponent()));
		currentEventList.revalidate();
		currentEventList.repaint();

		upcomingEventList.removeAll();
		events.stream().filter(e -> !(e.isOngoing() || e.isUpcoming())).sorted((e1, e2) -> e1.getStartTime().compareTo(e2.getStartTime())).forEach(e -> upcomingEventList.add(e.getCellComponent()));
		upcomingEventList.revalidate();
		upcomingEventList.repaint();
	}

	public static void main(String[] args) {
		// Example usage with dummy events
		List<TimetableEvent> dummyEvents = Arrays.asList(
				new TimetableEvent("Math", "Room 101", "Main Entrance", "Library", LocalDateTime.now().minusMinutes(10), LocalDateTime.now().plusMinutes(20), List.of(TimetableEvent.Category.STUDENTS)),
				new TimetableEvent("Physics", "Lab 2", "Side Gate", "Cafeteria", LocalDateTime.now().plusMinutes(20), LocalDateTime.now().plusMinutes(50), List.of(TimetableEvent.Category.STUDENTS, TimetableEvent.Category.TEACHERS)),
				new TimetableEvent("Chemistry", "Room 202", "Main Entrance", "Gym", LocalDateTime.now().plusMinutes(40), LocalDateTime.now().plusMinutes(70), List.of(TimetableEvent.Category.STUDENTS)),
				new TimetableEvent("Biology", "Room 303", "Back Door", "Auditorium", LocalDateTime.now().plusMinutes(5), LocalDateTime.now().plusMinutes(35), List.of(TimetableEvent.Category.TEACHERS)),
				new TimetableEvent("Biology 1", "Room 303", "Back Door", "Auditorium", LocalDateTime.now().plusMinutes(35), LocalDateTime.now().plusMinutes(35), List.of(TimetableEvent.Category.TEACHERS)),
				new TimetableEvent("Biology 2 ", "Room 303", "Back Door", "Auditorium", LocalDateTime.now().plusMinutes(40), LocalDateTime.now().plusMinutes(35), List.of(TimetableEvent.Category.TEACHERS)),
				new TimetableEvent("Biology 3", "Room 303", "Back Door", "Auditorium", LocalDateTime.now().plusMinutes(37), LocalDateTime.now().plusMinutes(35), List.of(TimetableEvent.Category.TEACHERS)),
				new TimetableEvent("History", "Room 404", "Main Entrance", "Library", LocalDateTime.now().plusMinutes(60), LocalDateTime.now().plusMinutes(90), List.of(TimetableEvent.Category.STUDENTS)),
				new TimetableEvent("Biology 1", "Room 303", "Back Door", "Auditorium", LocalDateTime.now().plusMinutes(35), LocalDateTime.now().plusMinutes(35), List.of(TimetableEvent.Category.TEACHERS)),
				new TimetableEvent("Biology 2 ", "Room 303", "Back Door", "Auditorium", LocalDateTime.now().plusMinutes(40), LocalDateTime.now().plusMinutes(35), List.of(TimetableEvent.Category.TEACHERS)),
				new TimetableEvent("Biology 3", "Room 303", "Back Door", "Auditorium", LocalDateTime.now().plusMinutes(37), LocalDateTime.now().plusMinutes(35), List.of(TimetableEvent.Category.TEACHERS)),
				new TimetableEvent("History", "Room 404", "Main Entrance", "Library", LocalDateTime.now().plusMinutes(60), LocalDateTime.now().plusMinutes(90), List.of(TimetableEvent.Category.STUDENTS)));
		// populate dummyEvents with test data
		SwingUtilities.invokeLater(() -> new TimetableFrame(dummyEvents));
	}
}
