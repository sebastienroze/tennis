package tennis;

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import rad.DataRAD;
import rad.GridRAD;
import rad.GridSearch;

public class FicheTournois extends FicheTennis {

	public FicheTournois() {
		super(new DataRAD(conn, "tournoi", "ID"));
		ajoutTitre(fiche, "Tournoi");
		ajoutSaisie(fiche, "Nom :", data.createTextField("NOM"));
		ajoutSaisie(fiche, "Code :", data.createTextField("CODE"));
		ajoutTailleDouble(fiche, bouttonsFiche);
		setVisible(false);
		grille = createGrille();
		grille.addRowSelectListener(id -> {
			FicheTournois.this.read(id);
		});
		data.addAfterChangeListener(id -> {
			FicheTournois.this.grille.refreshKey(id);
		});		
	}
	
	public static GridRAD createGrille() {
		GridRAD grille = new GridRAD(conn, "ID", "SELECT ID,NOM as Nom,CODE as Code FROM TOURNOI", 1, null);
		grille.setGridSearch(createSearch());
		return grille;
	}	
	
	public static GridSearch createSearch() {
		return new GridSearch() {
			public JTextField jtfRecheche;

			@Override
			public void init() {
				jtfRecheche = createJTextField(10);
			}

			@Override
			public JPanel getPanel() {
				JPanel pn = new JPanel();
				pn.setLayout(new FlowLayout(FlowLayout.LEFT));
				pn.add(new JLabel("Recheche"));
				pn.add(jtfRecheche);
				return pn;
			}

			@Override
			public String getFilter() {
				StringBuilder sql = new StringBuilder();
				String recherche = jtfRecheche.getText();
				if (!recherche.equals("")) {
					sql.append("NOM LIKE \"%" + recherche + "%\"");					
				}
				return sql.toString();
			}
		};		
	}
}
