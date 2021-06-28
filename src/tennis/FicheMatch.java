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
import rad.SQLComboBoxFieldRAD;

public class FicheMatch extends FicheTennis {

	public FicheMatch() {
		super(new DataRAD(conn, "match_tennis", "ID"));
		DataRAD dataMatchVaiqueur = new DataRAD(conn, "joueur", "ID");
		data.createLink("ID_VAINQUEUR", dataMatchVaiqueur);
		DataRAD dataMatchFinaliste = new DataRAD(conn, "joueur", "ID");
		data.createLink("ID_FINALISTE", dataMatchFinaliste);
		DataRAD dataMatchEpreuve = new DataRAD(conn, "epreuve", "ID");
		data.createLink("ID_EPREUVE", dataMatchEpreuve);
		DataRAD dataMatchEpreuveTournois = new DataRAD(conn, "tournoi", "ID");
		dataMatchEpreuve.createLink("ID_TOURNOI", dataMatchEpreuveTournois);

		ajoutTitre(fiche, "Match");
		ajoutTailleDouble(fiche, data.createListSelectorField("ID_EPREUVE", FicheTennis.jFrame ,"Epreuve",
				tag -> {return FicheEpreuve.createGrille();}));				
		ajoutSaisie(fiche, "Année :", dataMatchEpreuve.createTextField("ANNEE"));
		ajoutSaisie(fiche, "Type épreuve :", dataMatchEpreuve.createRadioGroupField("TYPE_EPREUVE",
				new String[] { "H", "F" }, new String[] { "Homme", "Femme" }));
		dataMatchEpreuve.setReadOnly(true);
		ajoutSaisie(fiche, "Nom :", dataMatchEpreuveTournois.createTextField("NOM"));
		ajoutSaisie(fiche, "Code :", dataMatchEpreuveTournois.createTextField("CODE"));
		dataMatchEpreuveTournois.setReadOnly(true);
		ajoutTailleDouble(fiche, data.createListSelectorField("ID_VAINQUEUR", FicheTennis.jFrame,"Vainqueur",
				tag -> {return FicheJoueur.createGrille();}));		
		ajoutSaisie(fiche, "Nom :", dataMatchVaiqueur.createTextField("NOM"));
		ajoutSaisie(fiche, "Prénom :", dataMatchVaiqueur.createTextField("PRENOM"));
		ajoutSaisie(fiche, "Sexe :", dataMatchVaiqueur.createRadioGroupField("SEXE",
				new String[] { "H", "F" }, new String[] { "Homme", "Femme" }));
		dataMatchVaiqueur.setReadOnly(true);
		
		ajoutTailleDouble(fiche, data.createListSelectorField("ID_FINALISTE", FicheTennis.jFrame,"Finaliste",
		tag -> {return FicheJoueur.createGrille();}));		
		
		ajoutSaisie(fiche, "Nom :", dataMatchFinaliste.createTextField("NOM"));
		ajoutSaisie(fiche, "Prénom :", dataMatchFinaliste.createTextField("PRENOM"));
		ajoutSaisie(fiche, "Sexe :", dataMatchFinaliste.createRadioGroupField("SEXE",
				new String[] { "H", "F" }, new String[] { "Homme", "Femme" }));
		dataMatchFinaliste.setReadOnly(true);
		ajoutTailleDouble(fiche, bouttonsFiche);
		setVisible(false);
		grille = createGrille();
		grille.addRowSelectListener(id -> {
			FicheMatch.this.read(id);
		});
		data.addAfterChangeListener(id -> {
			FicheMatch.this.grille.refreshKey(id);
		});		
	}
	
	public static GridRAD createGrille() {
		GridCalculatedField cfSexe = new GridCalculatedField(2, "Sexe") {
			@Override
			public String Calculate(ResultSet rs) throws SQLException {
				if (rs.getString("TYPE_EPREUVE").equals("H"))
					return "Homme";
				if (rs.getString("TYPE_EPREUVE").equals("F"))
					return "Femme";
				return "";
			}
		};
		GridCalculatedField cfVaiqueur = new GridCalculatedField(3, "Vaiqueur") {
			@Override
			public String Calculate(ResultSet rs) throws SQLException {
				return rs.getString("PRENOM_VAINQUEUR") + " " + rs.getString("NOM_VAINQUEUR");
			}
		};
		GridCalculatedField cfFinaliste = new GridCalculatedField(4, "Finaliste") {
			@Override
			public String Calculate(ResultSet rs) throws SQLException {
				return rs.getString("PRENOM_FINLALISTE") + " " + rs.getString("NOM_FINLALISTE");
			}
		};
		GridRAD grille = new GridRAD(conn, "match_tennis.ID",
				"SELECT match_tennis.ID,TYPE_EPREUVE," + "jva.NOM as NOM_VAINQUEUR,jva.PRENOM as PRENOM_VAINQUEUR,"
						+ "jfi.NOM as NOM_FINLALISTE,jfi.PRENOM as PRENOM_FINLALISTE,"
						+ "tournoi.NOM as Tournoi,ANNEE as Année" + " FROM match_tennis "
						+ "LEFT JOIN joueur jfi ON jfi.ID = match_tennis.ID_FINALISTE "
						+ "LEFT JOIN joueur jva ON jva.ID = match_tennis.ID_VAINQUEUR "
						+ "LEFT JOIN epreuve ON epreuve.ID = match_tennis.ID_EPREUVE "
						+ "LEFT JOIN tournoi ON tournoi.ID = epreuve.ID_TOURNOI",
				6, new GridCalculatedField[] { cfSexe, cfVaiqueur, cfFinaliste });
		
		grille.setGridSearch(createSearch());
		return grille;
	}	
	
	public static GridSearch createSearch() {
		return new GridSearch() {
			public JTextField jtfRecheche;
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
				jtfRecheche = createJTextField(10);
			}

			@Override
			public JPanel getPanel() {
				JPanel pn = new JPanel();
				pn.setLayout(new FlowLayout(FlowLayout.LEFT));
				pn.add(new JLabel("Recheche"));
				pn.add(jtfRecheche);
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
				String recherche = jtfRecheche.getText();
				if (!recherche.equals("")) {
					if (sql.length() >0) sql.append(" AND ");
					sql.append("(jva.NOM LIKE \"%" + recherche + "%\"");					
					sql.append("OR jva.PRENOM LIKE \"%" + recherche + "%\"");
					sql.append("OR jfi.NOM LIKE \"%" + recherche + "%\"");
					sql.append("OR jfi.PRENOM LIKE \"%" + recherche + "%\")");
				}
				return sql.toString();
			}
		};		
	}
}
