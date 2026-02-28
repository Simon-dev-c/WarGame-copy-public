package wargame;

import java.awt.Color;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import java.awt.Component;
import javax.swing.JOptionPane;
import java.io.File;
import java.io.IOException;

/*
 * La classe MenuDemarrage gère le JPanel qui permet d'afficher le menu et ses éléments
 */
public class MenuDemarrage extends JPanel implements IConfig{
	private static final long serialVersionUID = 1L;
	private JButton btnNouvellePartie;
	private JButton btnChargerPartie;
	private JButton btnQuitter;
	private JFrame fenetreJeu;
	private Runnable onNouvellePartie;
	
	/*
	 * Constructeur
	 * @param fenetre JFrame qui contient l'element
	 */
	public MenuDemarrage(JFrame fenetre){
		this.fenetreJeu = fenetre;
		setPreferredSize(new Dimension(LARGEUR_CARTE*NB_PIX_CASE,HAUTEUR_CARTE * NB_PIX_CASE ));
		//setBackground(new Color(34,34,34)); //Fond sombre
		setLayout(null); // Layout absolu pour positionner les boutons
		repaint();
		creerComposants();
	}
	
	/*
	 * Auto-affichage de l'element
	 * @param g outil graphique
	 */
	protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        g.drawImage(FOND.getImage(), 0, 0, getWidth(), getHeight(), null);
        
        g.drawImage(TITRE.getImage(), (int) (LARGEUR_JEU/2 - LARGEUR_TITRE/2), (int) (HAUTEUR_JEU/5 - HAUTEUR_TITRE/2), LARGEUR_TITRE, HAUTEUR_TITRE, null);
        
        int centre_x = (int) (LARGEUR_JEU/2 - LARGEUR_BOUTON/2);
		int start_y = (int) (HAUTEUR_JEU/2.2 - HAUTEUR_BOUTON/2);
        
        g.drawImage(BOUTON.getImage(), centre_x, start_y, LARGEUR_BOUTON, HAUTEUR_BOUTON, null);
        g.drawImage(BOUTON.getImage(), centre_x, start_y + ESPACEMENT, LARGEUR_BOUTON, HAUTEUR_BOUTON, null);
        g.drawImage(BOUTON.getImage(), centre_x, start_y + ESPACEMENT * 2, LARGEUR_BOUTON, HAUTEUR_BOUTON, null);
    }

	public void setOnNouvellePartie(Runnable r){ this.onNouvellePartie = r;}

	/*
	 * Crée les composants de la barre de menu
	 */
	private void creerComposants() {
		/* Version sans images
		 * 
		 * JLabel  titre = new JLabel("⚔ WAR GAME ⚔", SwingConstants.CENTER);
		titre.setFont(new Font("Arial", Font.BOLD,48 ));
		titre.setForeground(Color.WHITE);
		titre.setBounds(0,100,LARGEUR_CARTE *NB_PIX_CASE,60);
		add(titre);*/
		
		//Bouton Nouvelle Partie
		int centre_x = (int) (LARGEUR_JEU/2 - LARGEUR_BOUTON/2);
		int start_y = (int) (HAUTEUR_JEU/2.2 - HAUTEUR_BOUTON/2);
		
		btnNouvellePartie = creerBouton("Nouvelle partie", centre_x,start_y, LARGEUR_BOUTON, HAUTEUR_BOUTON);
		btnNouvellePartie.addActionListener(e -> {
			if(onNouvellePartie != null)
				onNouvellePartie.run();
			});
		add(btnNouvellePartie);

		//Bouton Nouvelle Partie 
		btnChargerPartie = creerBouton("Charger une partie", centre_x,start_y+ESPACEMENT, LARGEUR_BOUTON, HAUTEUR_BOUTON);
		add(btnChargerPartie);
		btnChargerPartie.addActionListener( e -> {
			Component parent = SwingUtilities.getWindowAncestor(this);
			String[] options = {"Slot 1", "Slot 2", "Slot 3", "Annuler"};
			
			int choix = javax.swing.JOptionPane.showOptionDialog(
				parent,
				"Choisissez un slot à charger", 
				"Charger une partie", 
				JOptionPane.DEFAULT_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				options,
				options[0]
			); 
			
			if(choix == 0 || choix == 1 || choix == 2 ){
				
				int slotNumber = choix + 1;
				File fichier = new File("src/wargame/save/slot"+slotNumber+".wg");
				if(!fichier.exists()){
					JOptionPane.showMessageDialog(
						parent,
						"Aucun sauvegarde trouvée pout slot"+slotNumber+" .",
						"Slot vide",
						JOptionPane.WARNING_MESSAGE
					);
					return ;
				}
				
				try{
					JPanel tempPanel = new JPanel();
					
					Carte mapChargee = SauveCharge.charger(fichier,tempPanel);
					
					FenetreJeu.initialiserJeu(fenetreJeu, mapChargee);
					
				}catch(IOException ex){
					JOptionPane.showMessageDialog(
							parent, 
							"Erreur lors du chargement : " + ex.getMessage(),
							"Erreur de chargement",
							JOptionPane.ERROR_MESSAGE
							); 
					 ex.printStackTrace();
				}catch (ClassNotFoundException ex) {
					JOptionPane.showMessageDialog(
							parent, 
							"Fichier de sauvegadre corrompu ou incompatible.",
							"Erreur de chargement", 
							JOptionPane.ERROR_MESSAGE
						); 
					 ex.printStackTrace();
				}
				
				
			}			
		});
		
		
		//Bouton Quitter
		btnQuitter = creerBouton("Quitter", centre_x,start_y+ESPACEMENT*2, LARGEUR_BOUTON, HAUTEUR_BOUTON); 
		btnQuitter.addActionListener(e -> {
			System.exit(0);
		});
		add(btnQuitter); 
	}
	
	/*
	 * Création d'un bouton
	 * @param texte 
	 * @param x 
	 * @param y 
	 * @param largeur
	 * @param hauteur
	 * @JButton du bouton créé
	 */
	private JButton creerBouton(String texte, int x,int y, int largeur, int hauteur){
		JButton bouton = new JButton(texte);
		bouton.setBounds(x,y, largeur, hauteur);
		bouton.setFont(new Font("Arial",Font.BOLD,20));
		bouton.setFocusPainted(false);
		bouton.setBackground(new Color(70,130,180)); // Bleu acier
		bouton.setForeground(Color.WHITE);
		bouton.setBorder(BorderFactory.createRaisedBevelBorder());// bouton qui ressort
		
		bouton.addMouseListener( new MouseAdapter() {
			public void mouseEntered(MouseEvent e){
				bouton.setBackground(new Color(100,149,237));//Bleu plus clair 
			}
			public void mouseExited(MouseEvent e){
				bouton.setBackground(new Color(70,130,180)); // Bleu acier
			}
		});
		
		return bouton;
	}
	
	
	
	
}
