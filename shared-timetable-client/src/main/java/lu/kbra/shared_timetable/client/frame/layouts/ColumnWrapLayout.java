package lu.kbra.shared_timetable.client.frame.layouts;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;

import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/**
 * From {@link https://gist.github.com/jirkapenzes/4560255}
 */
public class ColumnWrapLayout extends FlowLayout {

	private Dimension preferredLayoutSize;

	public ColumnWrapLayout() {
		super();
	}

	public ColumnWrapLayout(int align) {
		super(align);
	}

	public ColumnWrapLayout(int align, int hgap, int vgap) {
		super(align, hgap, vgap);
	}

	@Override
	public Dimension preferredLayoutSize(Container target) {
		return layoutSize(target, true);
	}

	@Override
	public Dimension minimumLayoutSize(Container target) {
		Dimension minimum = layoutSize(target, false);
		minimum.height -= (getVgap() + 1);
		return minimum;
	}

	private Dimension layoutSize(Container target, boolean preferred) {
		synchronized (target.getTreeLock()) {
			int targetHeight = target.getSize().height;

			if (targetHeight == 0)
				targetHeight = Integer.MAX_VALUE;

			int hgap = getHgap();
			int vgap = getVgap();
			Insets insets = target.getInsets();
			int horizontalInsetsAndGap = insets.top + insets.bottom + (vgap * 2);
			int maxHeight = targetHeight - horizontalInsetsAndGap;

			Dimension dim = new Dimension(0, 0);
			int colHeight = 0;
			int colWidth = 0;

			int nmembers = target.getComponentCount();

			for (int i = 0; i < nmembers; i++) {
				Component m = target.getComponent(i);

				if (m.isVisible()) {
					Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();

					if (colHeight + d.height > maxHeight) {
						addColumn(dim, colHeight, colWidth);
						colHeight = 0;
						colWidth = 0;
					}

					if (colHeight != 0) {
						colHeight += vgap;
					}

					colHeight += d.height;
					colWidth = Math.max(colWidth, d.width);
				}
			}

			addColumn(dim, colWidth, colHeight);

			dim.height += horizontalInsetsAndGap;
			dim.width += insets.left + insets.right + hgap * 2;

			Container scrollPane = SwingUtilities.getAncestorOfClass(JScrollPane.class, target);
			if (scrollPane != null) {
				dim.height -= (vgap + 1);
			}

			return dim;
		}
	}

	private void addColumn(Dimension dim, int rowWidth, int rowHeight) {
		dim.height = Math.max(dim.height, rowHeight);

		if (dim.width > 0) {
			dim.width += getHgap();
		}

		dim.width += rowWidth;
	}

}
