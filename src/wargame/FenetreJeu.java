package wargame;

import java.awt.event.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.*;

import java.awt.*;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;



/*
 * La classe FenetreJeu contient le main et affiche la carte et d’autres informations du jeu.
 */
public class FenetreJeu implements IConfig{
    private static boolean running = true;
    private static int lastClickX = -1;
    private static int lastClickY = -1;
    private static JButton boutonFinDeTour;
    private static JButton boutonRedemarrer;
    private static JButton boutonSauveGarde;
    private static JButton boutonRestaurer;
    private static JButton boutonRetourMenu;
    private static Boolean dragging = false;
    
    private static int dragDebutX,dragDebutY;
    private static int currentMouseX,currentMouseY;
    
    private static Element draggedElement =  null; 
    
    /*
     * Méthode principale pour créer la barre de menu
     * @param menuBar barre de menu
     * @param panelJeu panneau du jeu
     * @param jeu JFrame regroupant tout
     * @param map carte du jeu en cours
     */
    private static void creationBarreMenu(JMenuBar menuBar, JPanel panelJeu, JFrame jeu, Carte map) {
    	// Créer un menu
        JMenu menu = new JMenu("Menu");

        // Créer des éléments de menu
        JMenuItem itemFinDeTour = new JMenuItem("Fin de tour");
        JMenuItem itemRedemarrer = new JMenuItem("Redemarrer");
        JMenuItem itemSauvegarder = new JMenuItem("Sauvegarder");
        JMenuItem itemRestaurer = new JMenuItem("Restaurer");
        JMenuItem itemRetourMenu = new JMenuItem("Retour Menu");

        // Ajouter les éléments au menu
        menu.add(itemFinDeTour);
        menu.addSeparator(); // ligne de séparation
        menu.add(itemRedemarrer);
        menu.add(itemSauvegarder);
        menu.add(itemRestaurer);
        menu.add(itemRetourMenu);
        
        
        itemFinDeTour.addActionListener(e -> actionFinDeTour(panelJeu, map));
        
        itemRedemarrer.addActionListener(e -> {
        	map.redemarrer(HAUTEUR_CARTE,LARGEUR_CARTE);
        	jeu.repaint();
        });
        
        itemRestaurer.addActionListener( e -> {
        	// recopie du bouto de chargement dans menuDemarrage
			Component parent = SwingUtilities.getWindowAncestor(panelJeu);
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
				File fichier = new File("wargame/save/slot"+slotNumber+".wg");
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
					
					FenetreJeu.initialiserJeu(jeu, mapChargee);
					
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
        
        itemRetourMenu.addActionListener(e -> {
        	running  = false; 
        	MenuDemarrage newmenu = new MenuDemarrage(jeu);
        	newmenu.setOnNouvellePartie(() -> {
        		Carte newMap = new Carte(HAUTEUR_CARTE, LARGEUR_CARTE);
        		FenetreJeu.initialiserJeu(jeu, newMap);
        	});
        	jeu.setContentPane(newmenu);
        	jeu.revalidate();
        	jeu.repaint();
        });
        
        itemSauvegarder.addActionListener(e -> {
        	java.awt.Component parent = SwingUtilities.getWindowAncestor(panelJeu); //Récupère la fenêtre (JFrame) qui contient panelJeu
        	String[] options = {"Slot 1","Slot 2","Slot 3","Annuler"}; 
            
        	//Affichier le menu des slots 
        	int choix  = javax.swing.JOptionPane.showOptionDialog(
        			parent,//
        			"Choisissez un slot de sauvegarde",//
        			"Sauvegarde",
        			JOptionPane.DEFAULT_OPTION,//Type d’options par défaut
        			JOptionPane.QUESTION_MESSAGE,//Icône question affichée dans la boîte
        			null,//icônne personalisée
        			options,//les boutons affichés
        			options[0]); //sélectionné par défaut
        	
        	//Traitement du choix 
        	if(choix == 0 || choix == 1 || choix == 2){
        		int SlotNumber = choix + 1;
        		File dir = new File("wargame/save");
        		
        		if(!dir.exists()){
        			dir.mkdirs();
        		}
        		
        		//Chemin du fichier
        		File fichier = new File(dir,"slot" + SlotNumber + ".wg");
        		
        		//Dans le cas si le chimin vers le fichier exists : demander de la confiramtions d'écraisement ce slot
        		if(fichier.exists()){
        			int confirm = JOptionPane.showConfirmDialog(
        					parent,
        					"Le slot" + SlotNumber + "contient déjâ une sauvegarde.\n Voulez-vous l'écraser ?", 
        					"Confirmation d'écrasement", 
        					JOptionPane.YES_NO_OPTION,
        					JOptionPane.WARNING_MESSAGE
        			);
        			if(confirm != JOptionPane.YES_OPTION){
        				return ;
        			}
        		}
        	
        	
        	try{
        		SauveCharge.sauvegarder(map,fichier);
        		JOptionPane.showMessageDialog(parent,
        				"Sauvegarde efectuée dans le slot " + SlotNumber + " ("+ fichier.getPath() + ")",
        				"Sauvegarde reussie", 
        				JOptionPane.INFORMATION_MESSAGE
        				);
        	}catch (Exception ex){
        		JOptionPane.showMessageDialog(
        		parent,
        		"Erreur de la sauvegarde: "+ ex.getMessage(),
        		"Erreur",
        		JOptionPane.INFORMATION_MESSAGE
        		);
        		ex.printStackTrace();
        	}
          }
        }); 
        
        
        menuBar.getParent().revalidate();
        menuBar.getParent().repaint();
        

        // Ajouter le menu à la barre
        menuBar.add(menu);
    }
    
    /*
     * Affichage d'un bouton personnalisé
     * @param bouton bouton qu'on modifie
     */
    private static void affichageBouton(JButton bouton) {
        // Mettre le texte au centre de l'image
        bouton.setHorizontalTextPosition(SwingConstants.CENTER);
        bouton.setVerticalTextPosition(SwingConstants.CENTER);
        
        // Optionnel : enlever bordure et fond pour que l’image soit visible
        bouton.setBorderPainted(false);
        bouton.setContentAreaFilled(false);
        bouton.setFocusPainted(false);
    }
    
    /*
     * Méthode principale pour créer les boutons
     * @param panelBoutons barre de menu
     * @param panelJeu panneau du jeu
     * @param jeu JFrame regroupant tout
     * @param map carte du jeu en cours
     */
    private static void creationBoutons(JMenuBar panelBoutons, JPanel panelJeu, JFrame jeu, Carte map){
        boutonFinDeTour = new JButton("Fin Tour",BOUTON);
        boutonRedemarrer = new JButton("Redémarrer",BOUTON);
        boutonSauveGarde = new JButton("Sauvegarder",BOUTON);
        boutonRestaurer = new JButton("Restaurer",BOUTON);
        boutonRetourMenu = new JButton("Retour menu",BOUTON); 
        
        // Ajout au panel
        panelBoutons.add(boutonFinDeTour);
        panelBoutons.add(boutonRedemarrer);
        panelBoutons.add(boutonSauveGarde);
        panelBoutons.add(boutonRestaurer);
        panelBoutons.add(boutonRetourMenu);
        
        // Affichage des boutons(images)
        affichageBouton(boutonFinDeTour);
        affichageBouton(boutonRedemarrer);
        affichageBouton(boutonSauveGarde);
        affichageBouton(boutonRestaurer);
        affichageBouton(boutonRetourMenu);
        
        
        boutonFinDeTour.addActionListener(e -> actionFinDeTour(panelJeu, map));
        
        boutonRedemarrer.addActionListener(e -> {
        	map.redemarrer(HAUTEUR_CARTE,LARGEUR_CARTE);
        	jeu.repaint();
        });
        
        boutonRestaurer.addActionListener( e -> {
        	// recopie du bouto de chargement dans menuDemarrage
			Component parent = SwingUtilities.getWindowAncestor(panelJeu);
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
				File fichier = new File("wargame/save/slot"+slotNumber+".wg");
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
					
					FenetreJeu.initialiserJeu(jeu, mapChargee);
					
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
        
        boutonRetourMenu.addActionListener(e -> {
        	running  = false;
        	MenuDemarrage menu = new MenuDemarrage(jeu);
        	jeu.setJMenuBar(null);
        	menu.setOnNouvellePartie( () -> {
        		Carte NewMap =  new Carte(HAUTEUR_CARTE,LARGEUR_CARTE);
        		FenetreJeu.initialiserJeu(jeu, NewMap);
        	});
        	jeu.setContentPane(menu);
        	jeu.revalidate();
        	jeu.repaint();
        	
        });
        
        boutonSauveGarde.addActionListener(e -> {
        	java.awt.Component parent = SwingUtilities.getWindowAncestor(panelJeu); //Récupère la fenêtre (JFrame) qui contient panelJeu
        	String[] options = {"Slot 1","Slot 2","Slot 3","Annuler"}; 
            
        	//Affichier le menu des slots 
        	int choix  = javax.swing.JOptionPane.showOptionDialog(
        			parent,//
        			"Choisissez un slot de sauvegarde",//
        			"Sauvegarde",
        			JOptionPane.DEFAULT_OPTION,//Type d’options par défaut
        			JOptionPane.QUESTION_MESSAGE,//Icône question affichée dans la boîte
        			null,//icônne personalisée
        			options,//les boutons affichés
        			options[0]); //sélectionné par défaut
        	
        	//Traitement du choix 
        	if(choix == 0 || choix == 1 || choix == 2){
        		int SlotNumber = choix + 1;
        		File dir = new File("wargame/save");
        		
        		if(!dir.exists()){
        			dir.mkdirs();
        		}
        		
        		//Chemin du fichier
        		File fichier = new File(dir,"slot" + SlotNumber + ".wg");
        		
        		//Dans le cas si le chimin vers le fichier exists : demander de la confiramtions d'écraisement ce slot
        		if(fichier.exists()){
        			int confirm = JOptionPane.showConfirmDialog(
        					parent,
        					"Le slot" + SlotNumber + "contient déjâ une sauvegarde.\n Voulez-vous l'écraser ?", 
        					"Confirmation d'écrasement", 
        					JOptionPane.YES_NO_OPTION,
        					JOptionPane.WARNING_MESSAGE
        			);
        			if(confirm != JOptionPane.YES_OPTION){
        				return ;
        			}
        		}
        	
        	
        	try{
        		SauveCharge.sauvegarder(map,fichier);
        		JOptionPane.showMessageDialog(parent,
        				"Sauvegarde efectuée dans le slot " + SlotNumber + " ("+ fichier.getPath() + ")",
        				"Sauvegarde reussie", 
        				JOptionPane.INFORMATION_MESSAGE
        				);
        	}catch (Exception ex){
        		JOptionPane.showMessageDialog(
        		parent,
        		"Erreur de la sauvegarde: "+ ex.getMessage(),
        		"Erreur",
        		JOptionPane.INFORMATION_MESSAGE
        		);
        		ex.printStackTrace();
        	}
          }
        }); 
        
        
        panelBoutons.getParent().revalidate();
        panelBoutons.getParent().repaint();
    }
    
    /*
     * Action du fin de tour et actualisation
     * @param panelBoutons
     * @param map carte du jeu en cours
     */
    private static void actionFinDeTour(JPanel panelBoutons, Carte map) {
    	map.finDeTour();
    	
    	panelBoutons.getParent().revalidate();
        panelBoutons.getParent().repaint();
    }
    
    /*
     * Gestion des actions possibles via le clavier
     * @param menuBar barre de menu
     * @param panelJeu panneau du jeu
     * @param jeu JFrame regroupant tout
     * @param map carte du jeu en cours
     */
    private static void actionToucheClavier(JMenuBar menuBar, JPanel panel, JFrame jeu, Carte map) {
    	JRootPane root;
    	
    	// F : fin de tour
    	// F : fin de tour
        AbstractAction actionF = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	actionFinDeTour(panel, map);
            	//System.out.println("touche f détecté");
            }
        };

        root = jeu.getRootPane();

        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_F, 0), "finTour");

        root.getActionMap().put("finTour", actionF);
        
        // Permet de sélectionner les héros avec les touches 0,1,2,...
        for (int i = 0; i <= 9; i++) {
            final int indice = i;
            String actionName = "selectHeros" + i;

            root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_0 + i, 0), actionName);

            root.getActionMap().put(actionName, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                	Heros h = map.getHeros(indice);
                	System.out.println("touche " + indice + " détecté");
                	if (h != null) {
	                	map.setSelect(h.getPos());
	                    panel.repaint();
                	}
                }
            });
        }
    }
    
    /*
     * Méthode permettant d'initialiser une autre partie
     * @param jeu JFrame regroupant tout
     * @param map carte du jeu en cours
     */
    public static void initialiserJeu(JFrame jeu,Carte map){
    	JPanel main = new JPanel();
    	main.setPreferredSize(new Dimension((LARGEUR_CARTE*NB_PIX_CASE), (HAUTEUR_CARTE * NB_PIX_CASE) ));
    	
    	JMenuBar menuBar = new JMenuBar();
    	
    	menuBar.setOpaque(true);
    	menuBar.setBackground(Color.gray);
    	menuBar.setPreferredSize(new Dimension(LARGEUR_CARTE*NB_PIX_CASE, HAUTEUR_BARRE_MENU));
    	
    	jeu.setJMenuBar(menuBar);
    	
    	JPanel panel = new PanneauJeu(map);
    	
    	jeu.setContentPane(main);
    	main.add(panel);
    	creationBarreMenu(menuBar, panel, jeu, map);
    	creationBoutons(menuBar, panel, jeu, map);
    	actionToucheClavier(menuBar, panel, jeu, map);
    	jeu.pack();
    	jeu.setLocationRelativeTo(null);
    	jeu.revalidate();
    	jeu.repaint();
    	
    	// Configuration des listeners
    	configureMouseListeners(jeu, panel, map);
    	
    	// Démarrage de la boucle de jeu
    	demarrerBoucleJeu(jeu);
    	
    	jeu.setVisible(true);
    }
    
    /*
     * Configuration de la souris
     * @param jeu JFrame regroupant tout
     * @param panel panneau du jeu
     * @param map carte du jeu en cours
     */
    private static void configureMouseListeners(JFrame jeu, JPanel panel,Carte map){
    	//Listener des clics
        panel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                lastClickX = e.getX() / NB_PIX_CASE;
                lastClickY = e.getY() / NB_PIX_CASE;
                System.out.println("Clic détecté: " + lastClickY + ", " + lastClickX);
                
                
                if ( map != null && lastClickY>=0 && lastClickY<HAUTEUR_CARTE && lastClickX>=0 && lastClickX<LARGEUR_CARTE ) {
	                map.marquerCase(lastClickY, lastClickX);
	               	Element element = (Element) map.getElement(lastClickX,lastClickY);
	               	if(element instanceof Heros){
		               	dragging = true;
		               	dragDebutX = lastClickX;
		                dragDebutY = lastClickY;
		                draggedElement = element;
		                System.out.println("Debut X: "+ dragDebutX + " Debut Y"+ dragDebutY );
	                }
                	panel.repaint();
                }
                
                try {
                    Clip clip = AudioSystem.getClip();
                    
                    InputStream is = getClass().getResourceAsStream("/wargame/sons/clic.wav");
                    clip.open(AudioSystem.getAudioInputStream(new BufferedInputStream(is)));
                    
                    clip.start();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                
            }
            
            public void mouseReleased(MouseEvent e){
            	if(dragging){
            		int dropX = e.getX() / NB_PIX_CASE;    
            		int dropY = e.getY() / NB_PIX_CASE;
            		
            		map.setSelect(dragDebutX, dragDebutY);
            		if(dropX>=0 && dropX<LARGEUR_CARTE && dropY>=0 && dropY<HAUTEUR_CARTE) {
            			map.marquerCase(dropY, dropX);
            			System.out.println("Drop sur: " + dropY + ", " + dropX);
            		}
            		panel.repaint();
            	}
            	dragging = false;
            	draggedElement = null;
            }
            
        });
        
        panel.addMouseMotionListener(new MouseMotionListener() {
        	public void mouseDragged(MouseEvent e) {
        		if(dragging) {
	        		currentMouseX = e.getX();
	        		currentMouseY = e.getY();
	        		
	        		System.out.println("Drag en cours vers: " + currentMouseY + ", " + currentMouseX);
	        		panel.repaint(); 
        		}
        	}

			
			public void mouseMoved(MouseEvent e){
				int currentCaseX = e.getX() / NB_PIX_CASE;    
        		int currentCaseY = e.getY() / NB_PIX_CASE;
				
				if (map != null && (currentCaseX >= 0 && currentCaseY >= 0 && currentCaseX < LARGEUR_CARTE && currentCaseY < HAUTEUR_CARTE)) {
					if (map.getElement(currentCaseX, currentCaseY).EstVisible()) {
						panel.setToolTipText("Case : " + currentCaseY + "," + currentCaseX + " | " + map.getElement(currentCaseX, currentCaseY));
					}else {
						panel.setToolTipText("Case : " + currentCaseY + "," + currentCaseX);
					}
		        } else {
		            panel.setToolTipText(null);
		        }
				
				
				if(dragging){
					panel.repaint();
				}
			}
        	
        });
    }
    
    /*
     * Gestion de la boucle de jeu
     * @param jeu JFrame globale
     */
    private static void demarrerBoucleJeu(JFrame jeu){
    	 // Thread du jeu (boucle infinie tant que la fenêtre est ouverte)
        Thread gameLoop = new Thread(() -> {
            while (running) {

                // Exemple : si un clic a eu lieu
                if (lastClickX != -1) {
                    System.out.println("Traitement du clic...");
                    lastClickX = -1;
                }
                
                // Ton code de mise à jour du jeu ici
                // ...
                
                try { Thread.sleep(16); } catch (InterruptedException ignored) {}
            }
            System.out.println("Boucle de jeu arrêtée.");
        });

        gameLoop.start();

        // Quand la fenêtre se ferme → arrêter la boucle
        jeu.addWindowListener(new WindowAdapter() {
        	public void windowClosing(WindowEvent e) {
                running = false;   // ARRÊTE LA BOUCLE
                try {
                    gameLoop.join();  // attend que le thread s'arrête proprement
                } catch (InterruptedException ex) {}
            }
        });
    	
    }
    
    /*
     * Recuperation de la coordonnée verticale de la position courante de la souris
     * @return int coordonnée correspondante
     */
    public static int getCurrentMouseX(){
    	 return currentMouseX;  
    }
    
    /*
     * Recuperation de la coordonnée horizontale de la position courante de la souris
     * @return int coordonnée correspondante
     */
    public static int getCurrentMouseY(){
    	return currentMouseY;
    }
    
    /*
     * Main du programme
     * @param args
     */
	public static void main(String[] args) {
		
        JFrame jeu = new JFrame("Jeu");
        jeu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jeu.setPreferredSize(new java.awt.Dimension(LARGEUR_JEU, HAUTEUR_JEU));
        
        //jeu.setUndecorated(true); //Si l'on veut une fenêtre sans contour
        
        MenuDemarrage MenDem = new MenuDemarrage(jeu);
       
        //Nouvelle partie 
        MenDem.setOnNouvellePartie(() -> {
        Carte map = new Carte(HAUTEUR_CARTE,LARGEUR_CARTE);
        JPanel main = new JPanel();	
        main.setPreferredSize(new java.awt.Dimension(LARGEUR_CARTE * NB_PIX_CASE, HAUTEUR_CARTE * NB_PIX_CASE));
       
        JMenuBar menuBar = new JMenuBar();
        menuBar.setOpaque(true);
        menuBar.setBackground(Color.gray); // ne fonctionne pas mais c'est pas grave
        menuBar.setPreferredSize(new Dimension(LARGEUR_CARTE*NB_PIX_CASE,HAUTEUR_BARRE_MENU));
        
        
        jeu.setJMenuBar(menuBar);
        
        JPanel panel = new PanneauJeu(map);
        
        // Pour accepter les info-bulles
        ToolTipManager.sharedInstance().registerComponent(panel);
        ToolTipManager.sharedInstance().setInitialDelay(100); // 0 ou 100 ms
        ToolTipManager.sharedInstance().setDismissDelay(5000); // 5 sec
        
        jeu.setContentPane(main);
        main.add(panel, BorderLayout.CENTER);
        creationBarreMenu(menuBar, panel, jeu, map);
        creationBoutons(menuBar,panel, jeu, map);
        jeu.pack();
        jeu.setLocationRelativeTo(null);
        jeu.revalidate();
        jeu.repaint();
        
        // Configuration des listeners
    	configureMouseListeners(jeu, panel, map);
      
    	// Créer les actions qui seront déclenchés par l'appuie sur le clavier : f : fin de tour
    	actionToucheClavier(menuBar, panel, jeu, map);
    	
        jeu.setVisible(true);
        
        
        Clip clip;

        try {
        	InputStream is = FenetreJeu.class.getResourceAsStream("/wargame/sons/music_small_loop1.wav");
            if (is == null) throw new RuntimeException("Fichier music_small_loop1.wav introuvable dans le JAR");
            
            AudioInputStream audio = AudioSystem.getAudioInputStream(new BufferedInputStream(is));

            clip = AudioSystem.getClip();
            clip.open(audio);
            
            FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            volume.setValue(-12.0f);
            
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        

        // Thread du jeu (boucle infinie tant que la fenêtre est ouverte)
        Thread gameLoop = new Thread(() -> {
            while (running) {

                // Exemple : si un clic a eu lieu
                if (lastClickX != -1) {
                    System.out.println("Traitement du clic...");
                    lastClickX = -1;
                }
                
                try { Thread.sleep(16); }catch (InterruptedException ignored) {}
            }
            System.out.println("Boucle de jeu arrêtée.");
        });

        gameLoop.start();

        // Quand la fenêtre se ferme → arrêter la boucle
        jeu.addWindowListener(new WindowAdapter() {
        	public void windowClosing(WindowEvent e) {
                running = false;   // ARRÊTE LA BOUCLE
                try {
                    gameLoop.join();  // attend que le thread s'arrête proprement
                } catch (InterruptedException ex) {}
            }
        });
	    });
        
        //Afficher le menu en premier 
        jeu.setContentPane(MenDem);
        jeu.pack();
        jeu.setLocationRelativeTo(null);
        jeu.setVisible(true);
	}
	
	//FIN DU MAIN
	
	/*
	 * Recuperation de l'element pris par la souris
	 * @return Element correpondant
	 */
	public static Element getDraggedElement() {
		return draggedElement;
	}
	
	/*
	 * Permet de savoir si il y a dragging
	 * @return boolean correspondant
	 */
	public static Boolean isDragging() {
		return dragging;
	}
}
