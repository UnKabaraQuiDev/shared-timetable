package lu.kbra.shared_timetable.client.frame.train;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.Collector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import org.springframework.beans.factory.annotation.Autowired;

import lu.kbra.shared_timetable.client.frame.AbstractTimetableJFrame;
import lu.kbra.shared_timetable.client.network.TimetableList;
import lu.kbra.shared_timetable.common.Formats;
import lu.kbra.shared_timetable.common.TimetableEventData;
import lu.pcy113.pclib.swing.JLabelBuilder;

@org.springframework.stereotype.Component("train")
public class TrainTimetableFrame extends AbstractTimetableJFrame {

	private static final int UPCOMING_EVENT_COLUMNS = 5;
	private static final int DATE_FONT_STYLE = 100;

	private JPanel ongoingPanel, upcomingPanel;
	private JLabel timeDateLabel;

	private Timer timer;

	@Autowired
	private TimetableList timetableList;

	@Autowired
	private TrainStyleConfig style;

	@Autowired
	private TrainStyleBuilder builder;

	@Override
	public void setActive() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		this.timeDateLabel = new JLabelBuilder("time | date")
				.horizontalAlignment(SwingConstants.CENTER)
				.font(Font.MONOSPACED, Font.PLAIN, 80)
				.foreground(Color.WHITE)
				.background(style.getBackground())
				.build();

		final JPanel northPanel = new JPanel(new BorderLayout());

		final JPanel fakeCenterPanel = new JPanel(new BorderLayout());
		fakeCenterPanel.setOpaque(false);

		this.ongoingPanel = new JPanel();
		this.ongoingPanel.setLayout(new BoxLayout(ongoingPanel, BoxLayout.Y_AXIS));
		this.ongoingPanel.setOpaque(false);

		this.upcomingPanel = new JPanel();
		this.upcomingPanel.setLayout(new BoxLayout(upcomingPanel, BoxLayout.Y_AXIS));
		this.upcomingPanel.setOpaque(false);

		fakeCenterPanel.add(ongoingPanel, BorderLayout.NORTH);
		fakeCenterPanel.add(upcomingPanel, BorderLayout.CENTER);

		northPanel.add(timeDateLabel, BorderLayout.NORTH);
		northPanel.add(fakeCenterPanel, BorderLayout.CENTER);

		this.add(northPanel, BorderLayout.NORTH);

		// Update every second
		timer = new Timer(1000, e -> updateUIContents());
		timer.start();

		setSize(600, 400);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void updateUIContents() {
		builder.toggleFlash();

		this.ongoingPanel.removeAll();
		this.upcomingPanel.removeAll();

		this.timeDateLabel.setText(LocalDateTime.now().format(Formats.DATE_TIME_FMT));

		timetableList.sort(TimetableEventData::compareTo);
		timetableList.removeIf(TimetableEventData::isPast);

		ongoing: {
			ongoingPanel.add(separator());
			final JPanel subPanel = new JPanel();
			subPanel
					.add(new JLabelBuilder("----- ONGOING -----")
							.font(Font.MONOSPACED, Font.PLAIN, 50)
							.foreground(style.getBackground())
							.background(super.getBackground())
							.build());
			ongoingPanel.add(subPanel);
			timetableList
					.stream()
					.filter(TimetableEventData::isOngoing)
					.map(builder::getOngoingCell)
					.collect(collectWithSeparator())
					.forEach(ongoingPanel::add);
		}

		upcoming: {
			upcomingPanel.add(separator());
			final JPanel subPanel = new JPanel();
			subPanel
					.add(new JLabelBuilder("----- UPCOMING -----")
							.font(Font.MONOSPACED, Font.PLAIN, 50)
							.foreground(style.getBackground())
							.background(super.getBackground())
							.build());
			upcomingPanel.add(subPanel);
			timetableList
					.stream()
					.filter(e -> (e.isUpcoming() || !e.isOngoing()) && !e.isPast())
					.map(builder::getUpcomingCell)
					.collect(collectWithSeparator())
					.forEach(upcomingPanel::add);
			upcomingPanel.add(Box.createVerticalGlue());
		}

		ongoingPanel.revalidate();
		upcomingPanel.revalidate();
		super.repaint();
	}

	private Collector<JComponent, ArrayList<JComponent>, ArrayList<JComponent>> collectWithSeparator() {
		return Collector.of(ArrayList<JComponent>::new, (list, elem) -> {
			if (!list.isEmpty()) {
				list.add(separator());
			}
			list.add(elem);
		}, (left, right) -> {
			if (!left.isEmpty() && !right.isEmpty()) {
				left.add(separator());
			}
			left.addAll(right);
			return left;
		});
	}

	private JComponent separator() {
		return (JComponent) Box.createVerticalStrut(10);
	}

}
