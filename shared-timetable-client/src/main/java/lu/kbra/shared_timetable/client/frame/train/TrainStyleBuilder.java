package lu.kbra.shared_timetable.client.frame.train;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lu.kbra.shared_timetable.client.frame.components.ProgressBarJPanel;
import lu.kbra.shared_timetable.common.DurationUtils;
import lu.kbra.shared_timetable.common.Formats;
import lu.kbra.shared_timetable.common.TimetableEventData;
import lu.pcy113.pclib.PCUtils;
import lu.pcy113.pclib.swing.JLabelBuilder;

@Component
public class TrainStyleBuilder {

	public static final int PROGRESS_BAR_HEIGHT = 20;

	@Autowired
	private TrainStyleConfig style;

	private boolean flash = false;

	public void toggleFlash() {
		this.flash = !this.flash;
	}

	public JPanel getUpcomingCell(TimetableEventData event) {
		final JPanel panel = new JPanel();
		panel.setBackground(style.getBackground());
		panel.setForeground(style.getForeground());

		if (flash && event.shouldFlash() && style.isFlashEnabled()) {
			panel.setBackground(style.getFlashColor());
			panel.setForeground(PCUtils.maxContrast(panel.getBackground(), style.getBackground(), style.getForeground()));
		}

		panel.setLayout(new GridBagLayout());
		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(2, 5, 2, 5);

		final JLabel nameLabel = new JLabelBuilder(event.getName())
				.font(Font.MONOSPACED, Font.BOLD, 46)
				.foreground(style.getForeground())
				.build();
		final JLabel locationLabel = new JLabelBuilder(event.getLocation())
				.font(Font.MONOSPACED, Font.BOLD, 32)
				.foreground(style.getForeground())
				.build();
		final JLabel startTimeLabel = new JLabelBuilder(event.getStartTime().format(Formats.SHORT_DATE_TIME_REVERSED_FMT)
				+ (event.isLongUpcoming() ? " " + DurationUtils.formatDurationHHmm(event.getStartTime()) : ""))
						.font(Font.MONOSPACED, Font.BOLD, 46)
						.foreground(style.getForeground())
						.build();
		final JLabel endTimeLabel = new JLabelBuilder(
				event.getEndTime().format(event.isSameDay() ? Formats.SHORT_TIME_FMT : Formats.SHORT_DATE_TIME_REVERSED_FMT))
						.font(Font.MONOSPACED, Font.BOLD, 32)
						.foreground(style.getForeground())
						.build();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel.add(nameLabel, gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.weightx = 0.0;
		gbc.fill = GridBagConstraints.NONE;
		panel.add(startTimeLabel, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel.add(locationLabel, gbc);

		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.weightx = 0.0;
		gbc.fill = GridBagConstraints.NONE;
		panel.add(endTimeLabel, gbc);

		return panel;
	}

	public JPanel getOngoingCell(TimetableEventData event) {
		final JPanel panel = new ProgressBarJPanel(event.getElapsedPercentage(), style.getProgressColor(), PROGRESS_BAR_HEIGHT,
				SwingConstants.BOTTOM, SwingConstants.LEADING);
		panel.setBackground(style.getBackground());
		panel.setForeground(style.getForeground());

		if (flash && event.shouldFlash() && style.isFlashEnabled()) {
			panel.setBackground(style.getFlashColor());
			panel.setForeground(PCUtils.maxContrast(panel.getBackground(), style.getBackground(), style.getForeground()));
		}

		panel.setLayout(new GridBagLayout());
		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(2, 5, 2, 5);

		final JLabel nameLabel = new JLabelBuilder(event.getName())
				.font(Font.MONOSPACED, Font.BOLD, 50)
				.foreground(style.getForeground())
				.build();
		final JLabel locationLabel = new JLabelBuilder(event.getLocation())
				.font(Font.MONOSPACED, Font.BOLD, 32)
				.foreground(style.getForeground())
				.build();
		final JLabel startTimeLabel = new JLabelBuilder(event
				.getStartTime()
				.format(event.isSameDay() || event.isStartToday() ? Formats.SHORT_TIME_FMT : Formats.SHORT_DATE_TIME_REVERSED_FMT) + " "
				+ DurationUtils.formatDurationHHmm(event.getStartTime()))
						.font(Font.MONOSPACED, Font.BOLD, 50)
						.foreground(style.getForeground())
						.build();
		final JLabel endTimeLabel = new JLabelBuilder(event
				.getEndTime()
				.format(event.isSameDay() || event.isEndToday() ? Formats.SHORT_TIME_FMT : Formats.SHORT_DATE_TIME_REVERSED_FMT)
				+ (event.isEndToday() ? " " + DurationUtils.formatDurationHHmm(event.getEndTime()) : ""))
						.font(Font.MONOSPACED, Font.BOLD, 32)
						.foreground(style.getForeground())
						.build();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel.add(nameLabel, gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.weightx = 0.0;
		gbc.fill = GridBagConstraints.NONE;
		panel.add(startTimeLabel, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		panel.add(locationLabel, gbc);

		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.weightx = 0.0;
		gbc.fill = GridBagConstraints.NONE;
		panel.add(endTimeLabel, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.PAGE_END;

		panel.add(Box.createVerticalStrut(PROGRESS_BAR_HEIGHT), gbc);

		return panel;
	}

}
