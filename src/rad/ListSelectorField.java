package rad;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ListSelectorField implements DataFieldRAD {
	String value;
	JButton btSelect;
	Long selectedKey;
	private JDialog ficheListe;
	private JFrame owner;
	private String title;	
	private KeySelectListener onSelect = null;
	private GridBuilderListener gridBuilder = null;
	
	public ListSelectorField(JFrame owner, String title) {
		this.owner = owner;
		this.title = title;
		btSelect = new JButton(title);
		btSelect.addActionListener(e -> {ListSelectorField.this.displayList();});
		btSelect.setForeground(Color.black);
	}
	
	public Long getKeyValue() {
		return Long.parseLong(value);
	}
	public void doSelect() {
		value = selectedKey.toString();
		ListSelectorField.this.ficheListe.dispose();		
		if (onSelect != null) {
			onSelect.KeySelected(getKeyValue());
		}
	}
	
	public void addSelectListener(KeySelectListener ksl) {
		onSelect = ksl;
	}
	public void addGridBuilderListener(GridBuilderListener gbl) {
		gridBuilder = gbl;
	}


	public void displayList() {
		GridRAD grille = gridBuilder.newGrid(0);
		grille.addRowSelectListener(id -> {	ListSelectorField.this.selectedKey = id;});
		grille.addDoubleClicListener(e -> {	ListSelectorField.this.doSelect();});		
		ficheListe = new JDialog(owner, title, true);
		ficheListe.setSize(800, 800);
		ficheListe.setLocationRelativeTo(null);
		JButton btChoix = new JButton("Choisir");
		JPanel content = new JPanel();
		ficheListe.add(content);
		content.setLayout(new BorderLayout());		
		content.add(grille.getComponent(), BorderLayout.CENTER);
		content.add(btChoix, BorderLayout.SOUTH);
		btChoix.addActionListener(e -> {
			doSelect();
		});
		selectedKey = Long.parseLong(value);
		grille.select(selectedKey);
		ficheListe.setVisible(true);
	}
	
	@Override
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public void setReadOnly(boolean readonly) {
		btSelect.setEnabled(!readonly);		
	}

}
