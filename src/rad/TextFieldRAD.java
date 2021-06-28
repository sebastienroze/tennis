package rad;

import javax.swing.JTextField;

public class TextFieldRAD implements DataFieldRAD {
	private JTextField txtField ;
	public TextFieldRAD(JTextField txtField) {
		this.txtField =  txtField;
	}

	@Override
	public void setValue(String value) {
		txtField.setText(value);		
	}

	@Override
	public void setReadOnly(boolean readonly) {
		txtField.setEditable(! readonly);		
	}

	@Override
	public String getValue() {		
		return txtField.getText();
	}

}
