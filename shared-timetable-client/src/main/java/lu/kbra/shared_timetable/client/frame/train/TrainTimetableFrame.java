package lu.kbra.shared_timetable.client.frame.train;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import org.springframework.beans.factory.annotation.Autowired;

import lu.kbra.shared_timetable.client.frame.AbstractTimetableJFrame;
import lu.kbra.shared_timetable.client.network.TimetableList;
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
	private TrainStyleConfig config;

	@Override
	public void setActive() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		this.timeDateLabel = new JLabelBuilder("time | date")
				.horizontalAlignment(SwingConstants.CENTER)
				.font(Font.MONOSPACED, Font.PLAIN, 60)
				.foreground(Color.WHITE)
				.background(config.getBackground())
				.build();

		this.ongoingPanel = new JPanel();
		this.ongoingPanel.setLayout(new BoxLayout(ongoingPanel, BoxLayout.Y_AXIS));

		this.upcomingPanel = new JPanel();
		this.upcomingPanel.setLayout(new BoxLayout(ongoingPanel, BoxLayout.Y_AXIS));

		final JPanel northPanel = new JPanel(new BorderLayout());

		northPanel.add(timeDateLabel, BorderLayout.NORTH);
		northPanel.add(ongoingPanel, BorderLayout.CENTER);

		this.add(northPanel, BorderLayout.NORTH);
		this.add(upcomingPanel, BorderLayout.CENTER);

		// Update every second
		timer = new Timer(1000, e -> updateUIContents());
		timer.start();

		setSize(600, 400);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void updateUIContents() {
		this.ongoingPanel.removeAll();
		this.upcomingPanel.removeAll();
		
		for(final TimetableEventData event : timetableList) {
			if(event.isPast())
				continue;
			
			if(event.isOngoing()) {
				ongoingPanel.add(upcomingPanel);
				ongoingPanel.add(Box.createRigidArea(new Dimension(0, 20));
			}
		}
	}

}
