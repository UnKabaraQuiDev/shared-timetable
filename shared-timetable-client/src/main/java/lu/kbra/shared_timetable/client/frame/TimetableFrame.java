package lu.kbra.shared_timetable.client.frame;

import java.awt.BorderLayout;
import java.awt.Font;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import lu.kbra.shared_timetable.client.data.TimetableEvent;
import lu.kbra.shared_timetable.client.frame.renderers.TimetableEventListCellRenderer;

public class TimetableFrame extends JFrame {

	private JLabel timeLabel, dateLabel;
	private JList<TimetableEvent> eventList;
	private List<TimetableEvent> events;
	private Timer timer;

	public TimetableFrame(List<TimetableEvent> events) {
		this.events = events;
		setTitle("Timetable Display");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		JPanel timeDatePanel = new JPanel(new BorderLayout());

		// Time label (left)
		timeLabel = new JLabel();
		timeLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 18));
		timeDatePanel.add(timeLabel, BorderLayout.WEST);

		// Date label (right)
		dateLabel = new JLabel();
		dateLabel.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
		timeDatePanel.add(dateLabel, BorderLayout.EAST);

		add(timeDatePanel, BorderLayout.NORTH);

		// Events display (center, top-left)
		eventList = new JList<>(new DefaultListModel<>());
		eventList.setCellRenderer(new TimetableEventListCellRenderer());
		eventList.setFixedCellHeight(-1);
		add(eventList, BorderLayout.CENTER);

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

		List<TimetableEvent> activeEvents = events.stream().filter(ev -> !ev.getStartTime().isAfter(now) && !ev.getEndTime().isBefore(now)).sorted(Comparator.comparing(TimetableEvent::getStartTime)).limit(3).collect(Collectors.toList());

		int activeCount = activeEvents.size();

		// Fill with upcoming events if less than 3 active
		List<TimetableEvent> eventsToShow = new ArrayList<>(activeEvents);

		if (activeCount < 3) {
			List<TimetableEvent> upcomingEvents = events.stream().filter(ev -> ev.getStartTime().isAfter(now)).sorted(Comparator.comparing(TimetableEvent::getStartTime)).collect(Collectors.toList());

			int slotsLeft = 3 - activeCount;
			for (TimetableEvent ev : upcomingEvents) {
				eventsToShow.add(ev);
				if (--slotsLeft == 0)
					break;
			}
		}

		// Update list model
		DefaultListModel<TimetableEvent> eventListModel = (DefaultListModel<TimetableEvent>) eventList.getModel();
		eventListModel.clear();
		for (TimetableEvent ev : eventsToShow) {
			eventListModel.addElement(ev);
		}
	}

	public static void main(String[] args) {
		// Example usage with dummy events
		List<TimetableEvent> dummyEvents = Arrays.asList(
				new TimetableEvent("Math", "Room 101", "Main Entrance", "Library", LocalDateTime.now().minusMinutes(10), LocalDateTime.now().plusMinutes(20), List.of(TimetableEvent.Category.STUDENTS)),

				new TimetableEvent("Physics", "Lab 2", "Side Gate", "Cafeteria", LocalDateTime.now().plusMinutes(20), LocalDateTime.now().plusMinutes(50), List.of(TimetableEvent.Category.STUDENTS, TimetableEvent.Category.TEACHERS)),

				new TimetableEvent("Chemistry", "Room 202", "Main Entrance", "Gym", LocalDateTime.now().plusMinutes(40), LocalDateTime.now().plusMinutes(70), List.of(TimetableEvent.Category.STUDENTS)),

				new TimetableEvent("Biology", "Room 303", "Back Door", "Auditorium", LocalDateTime.now().plusMinutes(5), LocalDateTime.now().plusMinutes(35), List.of(TimetableEvent.Category.TEACHERS)),

				new TimetableEvent("History", "Room 404", "Main Entrance", "Library", LocalDateTime.now().plusMinutes(60), LocalDateTime.now().plusMinutes(90), List.of(TimetableEvent.Category.STUDENTS)));
		// populate dummyEvents with test data
		SwingUtilities.invokeLater(() -> new TimetableFrame(dummyEvents));
	}
}
