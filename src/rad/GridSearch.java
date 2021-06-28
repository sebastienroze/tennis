package rad;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public abstract class GridSearch {
	public GridRAD grille;

	public GridSearch() {
		init();
	}

	public abstract void init();

	public abstract String getFilter();

	public abstract JPanel getPanel();

	public void search() {
		grille.filter(getFilter());
	}

	public RadioGroupField createRadioGroupField(String[] values, String[] labels) {
		RadioGroupField rgf = new RadioGroupField();
		for (int i = 0; i < values.length; i++) {
			JRadioButton rb = new JRadioButton(labels[i]);
			rb.addActionListener(e -> {
				GridSearch.this.search();
			});
			rgf.AddRadioButton(values[i], rb);
		}
		return rgf;
	}

	public JTextField createJTextField(int size) {
		JTextField jtf = new JTextField(size);
		jtf.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				GridSearch.this.search();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				GridSearch.this.search();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				GridSearch.this.search();
			}
		});
		return jtf;
	}
	
	public SQLComboBoxFieldRAD createSQLComboBoxField(Connection conn, String sql) {
		JComboBox<String> cb = new JComboBox<String>();
		SQLComboBoxFieldRAD cbf = new SQLComboBoxFieldRAD(cb);
		try {
			ResultSet rsql = conn.createStatement().executeQuery(sql);
			int comboIndex = 0;
			while (rsql.next()) {
				cb.addItem(rsql.getString(2));
				cbf.AddValue(rsql.getString(1), comboIndex++);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		cb.addActionListener(e -> {
			GridSearch.this.search();
		});		
		return cbf;
	}
}
