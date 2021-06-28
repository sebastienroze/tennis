package tennis;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import rad.DataFieldRAD;

public class TennisSetField extends JPanel implements DataFieldRAD {
	private static final long serialVersionUID = 1L;
	JTextField scroreVainqueur;
	JTextField scroreFinaliste;
	JLabel titre;
	boolean noChange = false;

	public TennisSetField(String label) {
		super();
		setLayout(new GridLayout(3, 1));
		add(new JLabel(label));
		scroreVainqueur = createJTextFieldVainqueur();
		scroreFinaliste = createJTextFieldeFinaliste();
		add(scroreVainqueur);
		add(scroreFinaliste);
		noChange = false;
	}

	private JTextField createJTextFieldVainqueur() {
		JTextField jtf = new JTextField(2);
		jtf.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				TennisSetField.this.vainqueurChanged();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				TennisSetField.this.vainqueurChanged();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				TennisSetField.this.vainqueurChanged();
			}
		});
		return jtf;
	}

	private JTextField createJTextFieldeFinaliste() {
		JTextField jtf = new JTextField(2);
		jtf.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				TennisSetField.this.finalisteChanged();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				TennisSetField.this.finalisteChanged();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				TennisSetField.this.finalisteChanged();
			}
		});
		return jtf;
	}

	public void vainqueurChanged() {
		if (!noChange) {
			noChange = true;
			if (scroreVainqueur.getText().equals("")) {
				scroreFinaliste.setText("");
				scroreVainqueur.setBackground(Color.LIGHT_GRAY);
				scroreFinaliste.setBackground(Color.LIGHT_GRAY);
			} else {
				scroreVainqueur.setBackground(null);
				try {
					int score = Integer.parseInt(scroreVainqueur.getText());
					int scoreAutre = Math.max(6, score + 2);
					scroreFinaliste.setText(Integer.toString(scoreAutre));
					scroreFinaliste.setBackground(Color.green);
				} catch (NumberFormatException e) {
					scroreFinaliste.setBackground(Color.red);
					scroreFinaliste.setText("");
				}

			}
			noChange = false;
		}
	}

	public void finalisteChanged() {
		if (!noChange) {
			noChange = true;
			if (scroreFinaliste.getText().equals("")) {
				scroreVainqueur.setText("");
				scroreFinaliste.setBackground(Color.LIGHT_GRAY);
				scroreVainqueur.setBackground(Color.LIGHT_GRAY);
			} else {
				scroreFinaliste.setBackground(null);
				try {
					int score = Integer.parseInt(scroreFinaliste.getText());
					int scoreAutre = Math.max(6, score + 2);
					scroreVainqueur.setText(Integer.toString(scoreAutre));
					scroreVainqueur.setBackground(Color.green);
				} catch (NumberFormatException e) {
					scroreVainqueur.setBackground(Color.red);
					scroreVainqueur.setText("");
				}
			}
			noChange = false;
		}
	}

	@Override
	public void setValue(String value) {
		int score = toInt(value);
		int scoreAutre = score;
		noChange = true;
		if (score == 0) {
			scroreVainqueur.setBackground(Color.LIGHT_GRAY);
			scroreFinaliste.setBackground(Color.LIGHT_GRAY);
			scroreVainqueur.setText("");
			scroreFinaliste.setText("");
		} else {
			if (score > 0)
				scoreAutre = Math.max(6, score + 2);
			else
				scoreAutre = Math.max(6, -score + 2);
			if (score > 0) {
				scroreFinaliste.setText(Integer.toString(score));
				scroreFinaliste.setBackground(null);
				scroreVainqueur.setText(Integer.toString(scoreAutre));
				scroreVainqueur.setBackground(Color.green);

			} else {
				scroreVainqueur.setText(Integer.toString(-score));
				scroreVainqueur.setBackground(null);
				scroreFinaliste.setText(Integer.toString(scoreAutre));
				scroreFinaliste.setBackground(Color.green);
			}
		}
		noChange = false;
	}

	@Override
	public void setReadOnly(boolean readonly) {
		scroreVainqueur.setEditable(!readonly);
		scroreFinaliste.setEditable(!readonly);
	}

	@Override
	public String getValue() {
		String value;
		int scoreVainqueur = toInt(scroreVainqueur.getText());
		int scoreFinaliste = toInt(scroreFinaliste.getText());
		int score = 0;
		if (scoreVainqueur > scoreFinaliste) {
			score = scoreFinaliste;
		} else {
			score = -scoreVainqueur;
		}
		if (score == 0)
			value = "";
		else
			value = Integer.toString(score);
		return value;
	}

	private Integer toInt(String s) {
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return 0;
		}

	}

}
