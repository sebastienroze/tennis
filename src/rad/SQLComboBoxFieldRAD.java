package rad;

import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;

public class SQLComboBoxFieldRAD implements DataFieldRAD {
	private HashMap<String, Integer> values;
	private JComboBox<String> cb;

	public SQLComboBoxFieldRAD(JComboBox<String> comboBox) {
		values = new HashMap<String, Integer>();
		cb = comboBox;
		cb.setRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;
			@Override
			public void paint(Graphics g) {
				setForeground(cb.getForeground());
				super.paint(g);
			}
		});
	}
	
	public JComboBox<String> getCombobox() {
		return cb;		
	}

	public void AddValue(String value, int index) {
		values.put(value, index);
	}

	@Override
	public void setValue(String value) {
		if (values.containsKey(value)) {
			cb.setSelectedIndex(values.get(value));
		} else {
			cb.setSelectedIndex(-1);
		}
	}

	@Override
	public String getValue() {
		for (Entry<String, Integer> entry : values.entrySet()) {
			if (cb.getSelectedIndex() == entry.getValue()) {
				return entry.getKey();
			}
		}
		return null;
	}

	@Override
	public void setReadOnly(boolean readonly) {
		cb.setEnabled(!readonly);
	}

}
