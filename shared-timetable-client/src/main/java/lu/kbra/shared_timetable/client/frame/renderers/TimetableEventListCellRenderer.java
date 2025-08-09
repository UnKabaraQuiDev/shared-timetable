package lu.kbra.shared_timetable.client.frame.renderers;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.time.Duration;
import java.time.LocalDateTime;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

import lu.kbra.shared_timetable.client.data.TimetableEvent;
import lu.kbra.shared_timetable.client.frame.components.DelegatingDrawPanel;

public class TimetableEventListCellRenderer implements ListCellRenderer<TimetableEvent> {

	public static final int CORNER_RADIUS = 20;

	@Override
	public Component getListCellRendererComponent(JList<? extends TimetableEvent> list, TimetableEvent value, int index, boolean isSelected, boolean cellHasFocus) {
		final LocalDateTime now = LocalDateTime.now();
		final long minsToStart = Duration.between(now, value.getStartTime()).toMinutes();

		JComponent comp;

		if (!now.isBefore(value.getStartTime()) && !now.isAfter(value.getEndTime())) { // currently ongoing
			comp = value.getOngoingView();
		} else if (minsToStart <= 30) { // starting soon (within 30 minutes)
			comp = value.getUpcomingView();
		} else {
			comp = value.getCompactView();
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
		comp.setBorder(new EmptyBorder(CORNER_RADIUS, CORNER_RADIUS, CORNER_RADIUS, CORNER_RADIUS));
		
		final JPanel ret = new JPanel(new BorderLayout());
		ret.add(comp, BorderLayout.CENTER);
		ret.setBorder(new EmptyBorder(CORNER_RADIUS, CORNER_RADIUS, CORNER_RADIUS, CORNER_RADIUS));
		
		return ret;
	}

}
