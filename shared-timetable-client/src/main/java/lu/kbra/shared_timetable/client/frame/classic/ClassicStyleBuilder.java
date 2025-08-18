package lu.kbra.shared_timetable.client.frame.classic;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lu.kbra.shared_timetable.client.frame.components.RoundedProgressPanel;
import lu.kbra.shared_timetable.common.DurationUtils;
import lu.kbra.shared_timetable.common.Formats;
import lu.kbra.shared_timetable.common.TimetableEventData;
import lu.kbra.shared_timetable.common.TimetableEventData.TimetableEventCategory;
import lu.pcy113.pclib.swing.JLabelBuilder;

@Component
public class ClassicStyleBuilder {

	public static final int PADDING = 10;
	public static final int MARGIN = 5;
	public static final int CORNER_RADIUS = 20;

	@Autowired
	private ClassicStyleConfig config;

	public RoundedProgressPanel getCompactView(TimetableEventData data) {
		final RoundedProgressPanel panel = new RoundedProgressPanel();

		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel
				.add(new JLabelBuilder(data.getStartTime().format(Formats.TIME_FMT) + " - " + data.getEndTime().format(Formats.TIME_FMT))
						.font(new Font("Arial", Font.PLAIN, 20))
						.build());
		panel.add(new JLabelBuilder(data.getName()).font(new Font("Arial", Font.BOLD, 20)).build());
		panel.setBackground(config.getMain());
		panel.setPreferredSize(new Dimension(500, 150));

		return panel;
	}

	public RoundedProgressPanel getUpcomingView(TimetableEventData data) {
		final RoundedProgressPanel panel = new RoundedProgressPanel();
		panel.setLayout(new BorderLayout());

		panel.setProgressColor(config.getProgress());
		panel.setVertical();
		panel.setProgress(data.getStartDuration() * 100 / TimetableEventData.UPCOMING_MINUTES);

		final JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setOpaque(false);
		topPanel.add(new JLabelBuilder(data.getName()).font(new Font("Arial", Font.BOLD, 30)).build(), BorderLayout.WEST);
		topPanel
				.add(new JLabelBuilder(data.getStartTime().format(Formats.TIME_FMT) + " - " + data.getEndTime().format(Formats.TIME_FMT))
						.font(new Font("Arial", Font.BOLD, 30))
						.build(), BorderLayout.EAST);
		panel.add(topPanel, BorderLayout.NORTH);

		final JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
		centerPanel.setOpaque(false);

		centerPanel.add(Box.createVerticalGlue());
		centerPanel.add(new JLabelBuilder(data.getLocation()).font(new Font("Arial", Font.PLAIN, 24)).build());

		if (data.getCategories() != null && !data.getCategories().isEmpty()) {
			String categoriesText = data.getCategories().stream().map(Enum::name).reduce((a, b) -> a + ", " + b).orElse("");
			centerPanel.add(new JLabelBuilder(categoriesText).font(new Font("Arial", Font.ITALIC, 20)).build());
		}
		centerPanel.add(Box.createVerticalGlue());

		panel.add(centerPanel, BorderLayout.CENTER);

		panel
				.add(new JLabelBuilder(DurationUtils.formatDuration(data.getStartTime()))
						.font(new Font("Arial", Font.BOLD, 38))
						.horizontalAlignment(JLabel.CENTER)
						.build(), BorderLayout.SOUTH);

		return panel;
	}

	// Full detail: name + time range + location + end location + progress bar
	public RoundedProgressPanel getOngoingView(TimetableEventData data) {
		final RoundedProgressPanel panel = getUpcomingView(data);

		panel.setProgressColor(config.getProgress());
		panel.setProgress(data.getElapsedDuration() * 100 / data.getTotalDuration());
		panel.setHorizontal();

		final JPanel bottomPanel = new JPanel(new BorderLayout());

		bottomPanel.setOpaque(false);

		bottomPanel
				.add(new JLabelBuilder(DurationUtils.formatDuration(data.getStartTime())).font(new Font("Arial", Font.BOLD, 38)).build(),
						BorderLayout.WEST);
		bottomPanel
				.add(new JLabelBuilder(DurationUtils.formatDuration(data.getEndTime())).font(new Font("Arial", Font.BOLD, 38)).build(),
						BorderLayout.EAST);

		panel.add(bottomPanel, BorderLayout.SOUTH);

		return panel;
	}

	public JPanel getCellComponent(TimetableEventData data) {
		final RoundedProgressPanel comp = data.isOngoing() ? getOngoingView(data)
				: (data.isUpcoming() ? getUpcomingView(data) : getCompactView(data));

		comp.setBackground(config.getMain());

		if (data.getCategories().contains(TimetableEventCategory.STUDENTS)) {
			comp.setBackground(config.getAccent());
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
