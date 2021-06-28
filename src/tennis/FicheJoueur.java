package tennis;

import java.awt.FlowLayout;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import rad.DataRAD;
import rad.GridCalculatedField;
import rad.GridRAD;
import rad.GridSearch;
import rad.RadioGroupField;

public class FicheJoueur extends FicheTennis {

	public FicheJoueur() {
		super(new DataRAD(conn, "joueur", "ID"));
		ajoutTitre(fiche, "Joueur");
		ajoutSaisie(fiche, "Nom :", data.createTextField("NOM"));
		ajoutSaisie(fiche, "Prénom :", data.createTextField("PRENOM"));
		ajoutSaisie(fiche, "Sexe :", data.createRadioGroupField("SEXE",
				new String[] { "H", "F" }, new String[] { "Homme", "Femme" }));
		ajoutTailleDouble(fiche, bouttonsFiche);
		setVisible(false);
		grille = createGrille();
		grille.addRowSelectListener(id -> {
			FicheJoueur.this.read(id);
		});
		data.addAfterChangeListener(id -> {
			FicheJoueur.this.grille.refreshKey(id);
		});
	}
	
	public static GridRAD createGrille() {
		GridCalculatedField cf = new GridCalculatedField(2, "Sexe") {
			@Override
			public String Calculate(ResultSet rs) throws SQLException {
				if (rs.getString("SEXE").equals("H"))
					return "Homme";
				if (rs.getString("SEXE").equals("F"))
					return "Femme";
				return "";
			}
		};
		GridRAD grille = new GridRAD(conn, "ID", "SELECT ID,SEXE,NOM as Nom,PRENOM as Prénom FROM JOUEUR", 2,
				new GridCalculatedField[] { cf });
		grille.setGridSearch(createSearch());
		return grille;
	}

	public static GridSearch createSearch() {
		return new GridSearch() {
			public RadioGroupField rgfSexe;
			public JTextField jtfRecheche;

			@Override
			public void init() {
				rgfSexe = createRadioGroupField(new String[] { "*", "H", "F" },
						new String[] { "Tous", "Homme", "Femme" });
				rgfSexe.setValue("*");
				jtfRecheche = createJTextField(10);
			}

			@Override
			public JPanel getPanel() {
				JPanel pn = new JPanel();
				pn.setLayout(new FlowLayout(FlowLayout.LEFT));
				pn.add(new JLabel("Recheche"));
				pn.add(jtfRecheche);
				pn.add(new JLabel("Sexe :"));
				pn.add(rgfSexe);
				return pn;
			}

			@Override
			public String getFilter() {
				String sexe = rgfSexe.getValue();
				StringBuilder sql = new StringBuilder();
				if (!sexe.equals("*")) {
					sql.append("SEXE=\"" + sexe + "\"");
				}
				String recherche = jtfRecheche.getText();
				if (!recherche.equals("")) {
					if (sql.length() >0) sql.append(" AND ");
					sql.append("(NOM LIKE \"%" + recherche + "%\"");					
					sql.append("OR PRENOM LIKE \"%" + recherche + "%\")");					
				}
				return sql.toString();
			}
		};
	}
}