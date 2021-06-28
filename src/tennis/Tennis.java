package tennis;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;


public class Tennis extends JFrame {
	private static final long serialVersionUID = 1L;
	private FicheTennis[] fiches;
	public static void main(String[] args) throws UnsupportedLookAndFeelException {
		try {
			FicheTennis.conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/tennis?allowPublicKeyRetrieval=true&useSSL=false&useLegacyDatetimeCode=false&serverTimezone=Europe/Paris",
					"root", "masterkey");
			UIManager.setLookAndFeel(new NimbusLookAndFeel());
			FicheTennis.jFrame = new Tennis();
			FicheTennis.jFrame.createFiches ();
			FicheTennis.jFrame.setVisible(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Tennis() {
		super("Tennis");
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				
				boolean canclose = true;
				for (FicheTennis ficheTennis : fiches) {
					if (canclose)
						canclose = ficheTennis.CanClose();
				}
				if (canclose) {
					Tennis.this.dispose();
				}
			}
		});
		this.setSize(1000, 800);
		this.setLocationRelativeTo(null);
	}

	private void createFiches() {
		FicheTennis ficheJoueur;
		FicheTennis ficheTournois;
		FicheTennis ficheEpreuve;
		FicheTennis ficheMatch;
		FicheTennis ficheScore;
		
		ficheJoueur = new FicheJoueur();
		ficheTournois = new FicheTournois();
		ficheEpreuve = new FicheEpreuve();
   	    ficheMatch = new FicheMatch();
   	    ficheScore = new FicheScore();
		JTabbedPane tabbedpan = new JTabbedPane();
		this.setContentPane(tabbedpan);
		tabbedpan.add("Joueurs", ficheJoueur.getPageOnglet());
		tabbedpan.add("Tournois", ficheTournois.getPageOnglet());
		tabbedpan.add("Epreuves", ficheEpreuve.getPageOnglet());
		tabbedpan.add("Matchs", ficheMatch.getPageOnglet());
		tabbedpan.add("Résultats", ficheScore.getPageOnglet());
		fiches = new  FicheTennis[] { ficheJoueur, ficheTournois, ficheEpreuve,ficheMatch,ficheScore };		
	}	
}
