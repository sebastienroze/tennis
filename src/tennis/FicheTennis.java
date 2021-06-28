package tennis;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu.Separator;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import rad.DataRAD;
import rad.GridRAD;


public class FicheTennis {
	protected DataRAD data;
	protected JPanel fiche;
	protected JPanel bouttonsFiche;
	protected GridRAD grille;
	private GridBagConstraints gbc;	
	private JButton btCreate;
	private JButton btCopy;
	private JButton btUpdate;
	private JButton btDelete;
	private JButton btOk;
	private JButton btCancel;
	private int modeFiche;
	protected static Connection conn;
	protected static Tennis jFrame;	
	private static final int modeConsultation = 0;
	private static final int modeCreation = 1;
	private static final int modeModification = 2;

	public FicheTennis(DataRAD data) {
		super();
		this.data = data;
		gbc = new GridBagConstraints();
		gbc.gridy = 0;	
		fiche = new JPanel();
		fiche.setBorder(new LineBorder(Color.BLACK));
		
		fiche.setLayout(new GridBagLayout());
		createBouttonsFiche();
	}
	
	public void setGrille(GridRAD grille) {
		this.grille = grille;
	}

	public JPanel getPageOnglet() {
		JPanel page = new JPanel();
		JPanel card = new JPanel();		
		card.setLayout(new GridLayout(1, 2));
		card.add(grille.getScrollPane());
		card.add(fiche);
		page.setLayout(new BorderLayout());		
		if (grille.getSearchPane() != null) {
		page.add(grille.getSearchPane(), BorderLayout.NORTH);
		}
		page.add(card, BorderLayout.CENTER);
		return page;		
	}

	public void read(long keyValue) {
		if (CanClose()) {
			setVisible(true);
			setReadOnly(true);
			if (!data.read(keyValue)) {
				JOptionPane.showMessageDialog(fiche, "Enregistement inexistant !");
				this.data.doAfterChange();
			}
		}
		FicheTennis.this.grille.select(data.getKeyValue());
	}

	public boolean CanClose() {
		if (modeFiche != modeConsultation) {
			((JTabbedPane) fiche.getParent().getParent().getParent()).setSelectedComponent(fiche.getParent().getParent());
			int clickedButton = JOptionPane.showConfirmDialog(fiche, "Enregistrer les modifications ?", "Tennis",
					JOptionPane.YES_NO_CANCEL_OPTION);
			if (clickedButton == JOptionPane.CANCEL_OPTION)
				return false;
			if (clickedButton == JOptionPane.YES_OPTION) {
				btOk.doClick();
				if (modeFiche != modeConsultation)
					return false;
			}
		}
		return true;
	}

	public void setVisible(boolean visible) {
		for (int i = 0; i < fiche.getComponentCount(); i++)
			if (fiche.getComponent(i) != bouttonsFiche)
				fiche.getComponent(i).setVisible(visible);
		btCopy.setVisible(visible);
		btUpdate.setVisible(visible);
		btDelete.setVisible(visible);
	}

	private void setReadOnly(boolean readOnly) {
		btCreate.setVisible(readOnly);
		btCopy.setVisible(readOnly);
		btUpdate.setVisible(readOnly);
		btDelete.setVisible(readOnly);
		btOk.setVisible(!readOnly);
		btCancel.setVisible(!readOnly);
		data.setReadOnly(readOnly);
		if (readOnly)
			modeFiche = modeConsultation;
	}

	private void createBouttonsFiche() {
		bouttonsFiche = new JPanel();
		bouttonsFiche.setLayout(new FlowLayout());
		btCreate = new JButton("Nouveau");
		btCopy = new JButton("Dupliquer");
		btUpdate = new JButton("Modifier");
		btDelete = new JButton("Supprimer");
		btOk = new JButton("OK");
		btCancel = new JButton("Annuler");
		btOk.setVisible(false);
		btCancel.setVisible(false);
		bouttonsFiche.add(btCreate);
		bouttonsFiche.add(btCopy);
		bouttonsFiche.add(btUpdate);
		bouttonsFiche.add(btDelete);
		bouttonsFiche.add(btOk);
		bouttonsFiche.add(btCancel);

		btCreate.addActionListener(e -> {
			setVisible(true);
			FicheTennis.this.data.clear();
			FicheTennis.this.modeFiche = modeCreation;
			FicheTennis.this.setReadOnly(false);
			FicheTennis.this.grille.select(-1);
		});
		btCopy.addActionListener(e -> {
			FicheTennis.this.modeFiche = modeCreation;
			FicheTennis.this.setReadOnly(false);
		});
		btUpdate.addActionListener(e -> {
			FicheTennis.this.modeFiche = modeModification;
			FicheTennis.this.setReadOnly(false);
		});
		btDelete.addActionListener(e -> {
			try {
				FicheTennis.this.data.delete();
				FicheTennis.this.setVisible(false);
			} catch (SQLException eSql) {
				JOptionPane.showMessageDialog(fiche, "Erreur de suppression !\n" +eSql.getLocalizedMessage());
			}
		});
		btOk.addActionListener(e -> {
			try {
				boolean erreur = false;
				if (FicheTennis.this.modeFiche == modeCreation) {
					FicheTennis.this.data.create();
				} else {
					if (!FicheTennis.this.data.update()) {
						erreur = true;
						int clickedButton = JOptionPane.showConfirmDialog(fiche, "Passer en mode création ?",
								"Enregistement inexistant !", JOptionPane.YES_NO_OPTION);
						if (clickedButton == JOptionPane.YES_OPTION)
							FicheTennis.this.modeFiche = modeCreation;
					}
				}
				if (!erreur) {
					FicheTennis.this.setReadOnly(true);
					modeFiche = modeConsultation;
				}
			} catch (SQLException eSql) {
				//eSql.printStackTrace();
				JOptionPane.showMessageDialog(fiche, "Erreur dans les données !\n" +eSql.getLocalizedMessage());
			}
		});
		btCancel.addActionListener(e -> {
			if (FicheTennis.this.modeFiche == modeCreation) {
				FicheTennis.this.setReadOnly(true);
				FicheTennis.this.setVisible(false);
			} else {
				FicheTennis.this.data.read();
				FicheTennis.this.setReadOnly(true);
			}
		});
	}
	
	protected void ajoutTitre(JPanel fiche, String titre) {
		JLabel lTitre;
		lTitre = new JLabel(titre);
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.ipady = 5;
		Font labelFont = lTitre.getFont();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		lTitre.setHorizontalAlignment(SwingConstants.CENTER);
		lTitre.setBorder(new LineBorder(Color.BLACK));
		lTitre.setFont(new Font(labelFont.getFontName(), Font.BOLD, (int) (labelFont.getSize() * 1.2)));
		fiche.add(lTitre, gbc);
		gbc.gridy = gbc.gridy + 1;
		gbc.ipady = 0;
		fiche.add(new JLabel(" "), gbc);
		gbc.gridy = gbc.gridy + 1;
		gbc.anchor = GridBagConstraints.LINE_START;
	}

	protected void ajoutSousTitre(JPanel fiche, String titre) {
		JLabel lTitre;
		lTitre = new JLabel(titre);
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		Font labelFont = lTitre.getFont();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		Font font = new Font(labelFont.getFontName(), Font.BOLD, (int) (labelFont.getSize() * 1.0));
		lTitre.setFont(font);
		fiche.add(lTitre, gbc);
		gbc.gridy = gbc.gridy + 1;
		fiche.add(new Separator(), gbc);
		gbc.gridy = gbc.gridy + 1;
		gbc.ipady = 0;
	}
	
	protected void ajoutSaisie(JPanel fiche, String label, Component comp) {
		gbc.gridx = 0;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		fiche.add(new JLabel(label), gbc);
		gbc.gridx = 1;
		gbc.fill = GridBagConstraints.REMAINDER;
		fiche.add(comp, gbc);
		gbc.gridy = gbc.gridy + 1;
	}

	protected void ajoutTailleDouble(JPanel fiche, Component comp) {
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		fiche.add(comp, gbc);
		gbc.gridy = gbc.gridy + 1;
	}	

}
