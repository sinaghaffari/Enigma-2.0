package gui;

import java.util.ArrayList;

public class GUIButtonGroup {
	ArrayList<GUIRadioButton> buttonList = new ArrayList<GUIRadioButton>();
	public void add(GUIRadioButton b) {
		b.linkedButtonGroup = this;
		buttonList.add(b);
	}
	public void remove(GUIRadioButton b) {
		b.linkedButtonGroup = null;
		buttonList.remove(b);
	}
	public void clear() {
		for (GUIRadioButton b : buttonList) {
			b.linkedButtonGroup = null;
		}
		buttonList.clear();
	}
	public GUIRadioButton getSelected() {
		for (GUIRadioButton b : buttonList) {
			if (b.isSelected) {
				return b;
			}
		}
		return null;
	}
}
