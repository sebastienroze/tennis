package tennis;

import java.awt.FlowLayout;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JPanel;

import rad.DataRAD;
import rad.GridCalculatedField;
import rad.GridRAD;
import rad.GridSearch;
import rad.RadioGroupField;
import rad.SQLComboBoxFieldRAD;

public class FicheEpreuve extends FicheTennis {

	public FicheEpreuve() {
		super(new DataRAD(conn, "epreuve", "ID"));
		ajoutTitre(fiche, "Epreuve");
		ajoutSaisie(fiche, "Année :", data.createTextField("ANNEE"));
		ajoutSaisie(fiche, "Type épreuve :", data.createRadioGroupField("TYPE_EPREUVE",
				new String[] { "H", "F" }, new String[] { "Homme", "Femme" }));
		ajoutSaisie(fiche, "Tournoi :",
				data.createSQLComboBoxField("ID_TOURNOI", "SELECT ID,NOM FROM tournoi"));
		ajoutTailleDouble(fiche, bouttonsFiche);
		setVisible(false);
		grille = createGrille();
		grille.addRowSelectListener(id -> {
			FicheEpreuve.this.read(id);
		});
		data.addAfterChangeListener(id -> {
			FicheEpreuve.this.grille.refreshKey(id);
		});		
	}
	
	public static GridRAD createGrille() {
		GridCalculatedField cf = new GridCalculatedField(2, "Sexe") {
			@Override
			public String Calculate(ResultSet rs) throws SQLException {
				if (rs.getString("TYPE_EPREUVE").equals("H"))
					return "Homme";
				if (rs.getString("TYPE_EPREUVE").equals("F"))
					return "Femme";
				return "";
			}
		};
		GridRAD grille =  new GridRAD(conn, "epreuve.ID",
				"SELECT epreuve.ID,TYPE_EPREUVE,ANNEE as Année,tournoi.NOM as Tournoi FROM epreuve LEFT JOIN tournoi ON tournoi.ID=epreuve.ID_TOURNOI",
				2, new GridCalculatedField[] { cf });
		grille.setGridSearch(createSearch());
		return grille;
	}	
	
	public static GridSearch createSearch() {
		return new GridSearch() {
			public RadioGroupField rgfSexe;
			public SQLComboBoxFieldRAD scfTournoi;
			public SQLComboBoxFieldRAD scfAnnee;

			@Override
			public void init() {
				rgfSexe = createRadioGroupField(new String[] { "*", "H", "F" },
						new String[] { "Tous", "Homme", "Femme" });
				rgfSexe.setValue("*");
				scfTournoi = createSQLComboBoxField(conn,"SELECT -1 AS ID,'Tous' AS NOM UNION SELECT ID,NOM FROM tournoi" );				
				scfAnnee = createSQLComboBoxField(conn,"SELECT -1 AS VAL,'Toutes' AS ANNEE UNION SELECT DISTINCT ANNEE AS VAL,ANNEE FROM epreuve order by VAL" );				
			}

			@Override
			public JPanel getPanel() {
				JPanel pn = new JPanel();
				pn.setLayout(new FlowLayout(FlowLayout.LEFT));
				pn.add(new JLabel("Tournoi :"));
				pn.add(scfTournoi.getCombobox());
				pn.add(new JLabel("Année :"));
				pn.add(scfAnnee.getCombobox());
				pn.add(new JLabel("Sexe :"));
				pn.add(rgfSexe);
				return pn;
			}

			@Override
			public String getFilter() {
				StringBuilder sql = new StringBuilder();
				String sexe = rgfSexe.getValue();
				if (!sexe.equals("*")) {
					sql.append("TYPE_EPREUVE=\"" + sexe + "\"");
				}
				String tournoi = scfTournoi.getValue();
				if (!tournoi.equals("-1")) {
					if (sql.length() >0) sql.append(" AND ");					
					sql.append("ID_TOURNOI=\"" + tournoi + "\"");
				}
				String annee = scfAnnee.getValue();
				if (!annee.equals("-1")) {
					if (sql.length() >0) sql.append(" AND ");					
					sql.append("ANNEE=\"" + annee + "\"");
				}
				return sql.toString();
			}
		};		
	}
}

