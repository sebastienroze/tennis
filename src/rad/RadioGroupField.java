package rad;

import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class RadioGroupField extends JPanel implements DataFieldRAD {
	private static final long serialVersionUID = 1L;
	private HashMap<String, JRadioButton> values ; 
	private boolean readOnly;
	private ButtonGroup btG;
	public RadioGroupField() {
		values = new HashMap<String, JRadioButton>();		
		readOnly = false;
		this.btG = new ButtonGroup();
	}

	public void AddRadioButton(String value,JRadioButton rb) {
		values.put(value,rb);
		btG.add(rb);
		add(rb);		
	}

	@Override
	public void setValue(String value) {
		if (values.containsKey(value)) {
			values.get(value).setSelected(true);
		} else {
			btG.clearSelection();
		}
		if (readOnly) setReadOnly(readOnly);
	}
	
	@Override
	public String getValue() {		
		for (Map.Entry<String, JRadioButton>  entry : values.entrySet()) {
			if (entry.getValue().isSelected()) {
				return entry.getKey();
			}
		}
		return "";
	}
	
	@Override
	public void setReadOnly(boolean readonly) {		
		this.readOnly = readonly;
		for (Map.Entry<String, JRadioButton>  entry : values.entrySet()) {
			entry.getValue().setEnabled(!readonly || entry.getValue().isSelected());
		}
	}
}
