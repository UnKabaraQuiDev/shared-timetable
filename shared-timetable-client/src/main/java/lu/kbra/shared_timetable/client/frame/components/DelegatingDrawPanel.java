package lu.kbra.shared_timetable.client.frame.components;

import java.awt.Graphics;
import java.awt.LayoutManager;
import java.util.function.BiConsumer;

import javax.swing.JPanel;

public class DelegatingDrawPanel extends JPanel {

	private BiConsumer<Graphics, JPanel> drawFunction;

	public DelegatingDrawPanel() {
	}

	public DelegatingDrawPanel(BiConsumer<Graphics, JPanel> drawFunction) {
		this.drawFunction = drawFunction;
	}

	public DelegatingDrawPanel(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
	}

	public DelegatingDrawPanel(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
	}

	public DelegatingDrawPanel(LayoutManager layout) {
		super(layout);
	}

	@Override
	protected void paintComponent(Graphics g) {
		if (drawFunction != null) {
			g.setColor(getParent().getBackground());
			g.fillRect(0, 0, getWidth(), getHeight());
			
			drawFunction.accept(g, this);
			
			super.paintChildren(g);
		} else {
			super.paintComponent(g);
		}
	}

	public void setDrawFunction(BiConsumer<Graphics, JPanel> drawFunction) {
		this.drawFunction = drawFunction;
	}

	public BiConsumer<Graphics, JPanel> getDrawFunction() {
		return drawFunction;
	}
}
