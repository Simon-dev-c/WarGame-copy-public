package wargame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;


/**
 * La classe Carte gère la carte et les éléments qui y figurent.
 */
public class Carte implements ICarte, IConfig, Serializable{
	private int[][] brouillard; // 0 : pas de brouillard, 1 : brouillard
	private Element[][] tab;
	private Heros[] armeeHeros;
	private Monstre[] armeeMonstre;
	private int nbHerosVivant;
	private int nbMonstreVivant;
	private int hauteur;
	private int largeur;
	private int compteur_tour;
	private Position select;
	
	
	
	private enum Etat{
		EN_COURS,
		VICTOIRE,
		MATCH_NUL,
		DEFAITE
	};
	private Etat etat;
	
	private static final long serialVersionUID = 1L; // contrôle de la compatibilité
	
	public static String case_selectionne ; 
	public static String str_action_Hero = "Aucune Action réalisée";
	
	private int deplacementPossible[][];
	private int vision[][];
	
	
	/**
	 * Constructeur de la Carte
	 * @param hauteur hauteur de la carte
	 * @param largeur largeur de la carte
	 */
	public Carte(int hauteur, int largeur) {
		select = new Position(-1,-1);
		int i,j;
		this.hauteur = hauteur;
		this.largeur = largeur;
		brouillard = new int[hauteur][largeur];
		nbHerosVivant = NB_HEROS;
		nbMonstreVivant = NB_MONSTRES;
		tab = new Element[hauteur][largeur];
		armeeHeros = new Heros[NB_HEROS];  // Vide
		armeeMonstre = new Monstre[NB_MONSTRES];   // Vide
		
		compteur_tour = 0;
		etat = Etat.EN_COURS;
		
		deplacementPossible = null;
		vision = null;
		
		for (i=0;i<hauteur;i++) {
			for (j=0;j<largeur;j++) {
				tab[i][j] = new Plaine();
				tab[i][j].setPos(j,i);
			}
		}
		//tab[5][5] = new Heros(this,ISoldat.TypesH.HUMAIN,"BLOUP BLOUP",new Position(5,5));
		//initBrouillard(); -> Pour mettre toutes les cases en brouillard
		initObstacleAlea();
		for (int x = 0; x < 10; x++) {
			initRiviereAlea(3);
		}
		
		initArmeeHeros();    // Il faut initialiser les unités après les obstacles pour pouvoir verifier les chemins
		initArmeeMonstre();
		
		// Générer trop de rivières peut empêcher la page de s'ouvrir.
		actuBrouillard(); //-> Pour actualiser le brouillard
	}
	
	/**
	 * Vide les deux tableaux representant les armées
	 */
	public void redemarrer_armees() {
		for (int i = 0; i< NB_HEROS;i++) {
			armeeHeros[i] = null;
		}
		for (int i = 0; i< NB_MONSTRES;i++) {
			armeeMonstre[i] = null;
		}
	}
	
	/**
	 * Permet de relancer une autre partie
	 * @param hauteur hauteur de la carte
	 * @param largeur largeur de la carte
	 */
	public void redemarrer(int hauteur, int largeur) {
		// copie du constructeur
		
		select = new Position(-1,-1);
		int i,j;
		this.hauteur = hauteur;
		this.largeur = largeur;
		nbHerosVivant = NB_HEROS;
		nbMonstreVivant = NB_MONSTRES;
		compteur_tour = 0;
		etat = Etat.EN_COURS;
		for (i=0;i<hauteur;i++) {
			for (j=0;j<largeur;j++) {
				tab[i][j] = new Plaine();
				tab[i][j].setPos(j,i);
			}
		}
		redemarrer_armees();
		
		initObstacleAlea();
		for (int x = 0; x < 10; x++) {
			initRiviereAlea(3);
		}
		
		initArmeeHeros();   // Il faut initialiser les unités après les obstacles pour pouvoir verifier les chemins
		initArmeeMonstre();
		
		actuBrouillard();
	}
	

	/**
	 * Permet de récuper le tableau de jeu
	 * @return tableau du jeu
	 */
	public Element[][] getJeu() {
		return tab;
	}
	
	/**
	 * Permet de changer le plateau de jeu
	 * @param jeu plateau du jeu
	 */
	public void setJeu(Element[][] jeu) {
		tab = jeu;
	}
	
	/**
	 * Permet d'afficher la carte sur la console
	 */
	public void afficherCarteConsole() {
		int i,j;
		System.out.println("Affichage de la carte");
		for (j=0;j<largeur;j++) {
			System.out.print("-------");
		}
		System.out.println();
		for (i=0;i<hauteur;i++) {
			System.out.print("|");
			for (j=0;j<largeur;j++) {
				if (tab[i][j].EstVisible()) {
					System.out.print(" "+ tab[i][j].getClass().getSimpleName() +" ");
				}else {
					System.out.print(" Vide ");
				}
				System.out.print("|");
			}
			System.out.println("");
		}
		for (j=0;j<largeur;j++) {
			System.out.print("-------");
		}
	}
	
	/**
	 * Permet de recuperer un element du tableau
	 * @param pos position de l'element
	 * @return element du plateau correspondant
	 */
	public Element getElement(Position pos) {
		if (pos.estValide()) {
			return tab[pos.getY()][pos.getX()];
		}else {
			return null;
		}
	}
	
	/**
	 * Permet de recuperer un element du tableau
	 * @param x Coordonnée verticale
	 * @param y Coordonnée horizontale
	 * @return element du plateau correspondant
	 */
	public Element getElement(int x,int y) {
		Position p = new Position(x,y);
		if (p.estValide()) {
			return tab[y][x];
		}else {
			return null;
		}
	}
	
	/**
	 * Permet de recuperer un heros de l'armée
	 * @param i indice de l'element
	 * @return Heros correspondant dans la liste
	 */
	public Heros getHeros(int i) {
		if (nbHerosVivant > i) {
			return armeeHeros[i];
		}else {
			return null;
		}
	}
	
	/**
	 * Permet de poser un element dans une case du tableau
	 * @param e element
	 * @param pos position
	 */
	public void setElement(Element e,Position p) {
		tab[p.getY()][p.getX()] = e;
	}
	
	/**
	 * Permet de poser un element dans une case du tableau
	 * @param e element
	 * @param y coordonnée horizontale
	 * @param x coordonnée vericale
	 */
	public void setElement(Element e,int y,int x) {
		tab[y][x] = e;
	}
	
	/**
	 * Trouve aléatoirement une position vide sur la carte
	 * @return Position de l'element trouvé
	 */
	public Position trouvePositionVide() {
		// Trouve aléatoirement une position vide sur la carte
		boolean b = false;
		int x = -1;
		int y = -1;
		
		while (!b) {
			x = (int) (Math.random() * largeur);
			y = (int) (Math.random() * hauteur);
			
			if (tab[y][x] instanceof Plaine) {
				b = true;
			}
		}
		Position p = new Position(x,y);
		return p;
	}
	
	/**
	 * Trouve aléatoirement une position vide sur la carte pour les héros
	 * @return Position de l'element trouvé
	 */
	public Position trouvePositionVideHeros() {
		// Trouve aléatoirement une position vide sur la carte
		boolean b = false;
		int x = -1;
		int y = -1;
		
		while (!b) {
			x = (int) (Math.random() * (largeur/2));
			y = (int) (Math.random() * hauteur);
			
			if (tab[y][x] instanceof Plaine) {
				b = true;
			}
		}
		Position p = new Position(x,y);
		return p;
	}
	
	/**
	 * Trouve aléatoirement une position vide sur la carte pour les monstres
	 * @return Position de l'element trouvé
	 */
	public Position trouvePositionVideMonstre() {
		// Trouve aléatoirement une position vide sur la carte
		boolean b = false;
		int x = -1;
		int y = -1;
		
		while (!b) {
			x = (int) ((Math.random() * (largeur/2))+(largeur/2));
			y = (int) (Math.random() * hauteur);
			
			if (tab[y][x] instanceof Plaine) {
				b = true;
			}
		}
		Position p = new Position(x,y);
		return p;
	}
	
	/**
	 * Trouve une position adjacente vide (les 8 cases autours)
	 * @param pos position autour de laquelle on veut trouver une case vide
	 * @return Position trouvé
	 */
	public Position trouvePositionVide(Position pos) {
		// Trouve une position vide choisie
		// al�atoirement parmi les 8 positions adjacentes de pos
		Position p;
		boolean b = false;
		int x = -1;
		int y = -1;
		int compteur = 40;
		
		while (!b  && compteur > 0) { // rajout du compteur pour sortir dans tout les cas, au bout de 10 essais on sort
			compteur --;
			
			x = ((int) (Math.random() * 4  - 2)) + pos.getX();  // entre -1 et 1
			y = ((int) (Math.random() * 4  - 2)) + pos.getY();   // Très etrange mais ça fonctionne mieux ????
				
			if ((x >= 0 && x < largeur) && (y >= 0 && y < hauteur) )  {
				if (tab[y][x] instanceof Plaine) {
					b = true;
				}
			}
		}
			
		if (compteur > 0)
			p = new Position(x,y);
		else
			p = pos;
			
		return p;
	}
	
	/**
	 * Trouve aléatoirement un héros sur la carte
	 * @return Heros trouvé
	 */
	public Heros trouveHeros() {
		// Trouve al�atoirement un h�ros sur la carte
		Heros h;
		int x;
		
		x = (int) (Math.random() * nbHerosVivant);  // de 0 à nbHerosVivant-1 pour indice
		
		h = armeeHeros[x];
		
		return h;
	}
	
	/**
	 * Trouve aléatoirement un héros sur la carte autour de la positon
	 * @param pos position autour de laquelle on cherche
	 * @return Heros trouvé
	 */
	public Heros trouveHeros(Position pos) {
		// Trouve un h�ros choisi al�atoirement
		// parmi les 8 positions adjacentes de pos
		Heros h = null;
		boolean b = true;
		int x = -1;
		int y = -1;
		
		// Verification qu'il y en a au moins 1
		for (int i=-1;i<=1;i++) {
			for (int j=-1;j<=1;j++) {
				if((pos.getY()+i)>=0 && (pos.getY()+i)<hauteur && (pos.getX()+j)>=0 && (pos.getX()+j)<largeur) {
					if (tab[pos.getY()+i][pos.getX()+j] instanceof Heros) {
						b = false;
						i = 1;
						j=1;
					}
				}
			}
		}
		
		while (!b) {
			x = (int) (Math.random() * 3 - 1);  // entre -1 et 1
			y = (int) (Math.random() * 3 - 1);
			
			if((pos.getY()+y)>=0 && (pos.getY()+y)<hauteur && (pos.getX()+x)>=0 && (pos.getX()+x)<largeur) {
				if (tab[pos.getY()+y][pos.getX()+x] instanceof Heros) {
					b = true;
					h = (Heros) tab[pos.getY()+y][pos.getX()+x];
				}
			}
		}
		
		
		return h;
	}
	
	/**
	 * Déplace un soldat à une position
	 * @param pos position
	 * @param soldat soldat
	 * @return boolean qui décrit si le deplacement a été effectué correctement
	 */
	public boolean deplaceSoldat(Position pos, Soldat soldat) {
		setElement(soldat,pos);
		setElement(new Plaine(), soldat.getPos());
		return true;
	}
	
	/**
	 * Gere le cas où le joueur 1 gagnes
	 */
	public void victoire() {
		etat = Etat.VICTOIRE;
	}
	
	/**
	 * Gere le cas où éggalité entre les joueurs
	 */
	public void matchNull() {
		etat = Etat.MATCH_NUL;
	}
	
	/**
	 * Gere le cas où le joueur 2 gagnes
	 */
	public void defaite() {
		etat = Etat.DEFAITE;
	}
	
	/**
	 * Gere le cas où un soldat meures
	 * @param perso soldat qui est mort
	 */
	public void mort(Soldat perso) {
		
		if (perso instanceof Heros) {
		
			for (int i=0;i<nbHerosVivant;i++) {
				if (armeeHeros[i].equals(perso)) {
					
					// Si on le trouve on le déplace jusqu'a la fin en décalant les autres de une pos pour garder une liste d'armée héros vivante de 0 à nbHerosVivant
					
					for (int j=i;j<(nbHerosVivant-1);j++) {
						armeeHeros[j] = armeeHeros[j+1];
					}
					armeeHeros[nbHerosVivant-1] = (Heros) perso; // On le garde au cas où mais plus accessible
					
					i = nbHerosVivant;	// Sortir de la boucle
				}
			}
			
			setElement(new Plaine(), perso.getPos()); // Joueur mort donc il n'est plus là
			nbHerosVivant --;
			
			if (nbHerosVivant == 0) {
				defaite();
			}
			
		}else {
		
			if (perso instanceof Monstre) {
				
				for (int i=0;i<nbMonstreVivant;i++) {
					if (armeeMonstre[i].equals(perso)) {
						
						// Si on le trouve on le déplace jusqu'a la fin en décalant les autres de une pos pour garder une liste d'armée monstres vivants de 0 à nbMonstreVivant
						
						for (int j=i;j<(nbMonstreVivant-1);j++) {
							armeeMonstre[j] = armeeMonstre[j+1];
						}
						armeeMonstre[nbMonstreVivant-1] = (Monstre) perso; // On le garde au cas où mais plus accessible
						
						i = nbMonstreVivant;	// Sortir de la boucle
					}
				}
				
				setElement(new Plaine(), perso.getPos()); // Joueur mort donc il n'est plus là
				nbMonstreVivant --;
				
				if (nbMonstreVivant == 0) {
					victoire();
				}
			}
			
		}
	}
	
	/**
	 * Permet de trouver si il y a une case vide autour (4 directions haut,bas,gauche,droite) vide
	 * @param p position autour de laquelle on cherche
	 * @return boolean qui décrit la réussite ou l'échec de la recherche
	 */
	public boolean caseVideAutourNonDiagonale(Position p) {
		int x = p.getX();
		int y = p.getY();
		Position p2;
		
		// On suppose p valide
		
		p2 = new Position(x,y-1);
		if (p2.estValide() && (getElement(p2) instanceof Plaine)) {
			System.out.println("" + p2 + " valide ");
			return true;
		}
		p2 = new Position(x,y+1);
		if (p2.estValide() && (getElement(p2) instanceof Plaine)) {
			System.out.println("" + p2 + " valide ");
			return true;
		}
		p2 = new Position(x-1,y);
		if (p2.estValide() && (getElement(p2) instanceof Plaine)) {
			System.out.println("" + p2 + " valide ");
			return true;
		}
		p2 = new Position(x+1,y);
		if (p2.estValide() && (getElement(p2) instanceof Plaine)) {
			System.out.println("" + p2 + " valide ");
			return true;
		}
		
		return false;
	}
	
	/**
	 * Permet d'initialiser l'armée du joueur 1
	 */
	public void initArmeeHeros() {
		
		int type;
		int nom;
		
		String ligne = null;
        BufferedReader br;
        InputStream is;
		
		for (int i = 0; i< NB_HEROS;i++) {
			Position p = trouvePositionVideHeros();
			Position p2 = new Position(p.getX() + 1 ,p.getY()); // On est un héros donc il y a forcement de la place à droite
			if (!caseVideAutourNonDiagonale(p)) { // Juste pour s'assurer qu'il y a au moins une position vide dans les cases adjacentes
				
				if (getElement(p2) instanceof Obstacle) {
					setElement(new Plaine(), p2); 
				}
				// Si ce n'est pas un obstacle alors c'est un soldat donc possibilité de déplacement future
				
			}
			type = (int) (Math.random() * ISoldat.nbTypeHeros);
			nom = (int) (Math.random() * NB_NOMS);
			ISoldat.TypesH th = ISoldat.TypesH.ELF;
			switch (type) {
			case(0):
				th = ISoldat.TypesH.ELF;
			
				try {
					is = Carte.class.getResourceAsStream("/wargame/name/Elfs");
					if (is == null) {
					    throw new RuntimeException("Fichier Elfs introuvable dans le JAR");
					}
					br = new BufferedReader(new InputStreamReader(is));
					
	
			        for (int j = 0; j <= nom; j++) {
			            ligne = br.readLine();
			            if (ligne == null) break; // fin du fichier avant d'arriver à x
			        }
			        
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
				break;
			case(1):
				th = ISoldat.TypesH.HOBBIT;
			
				try {
					is = Carte.class.getResourceAsStream("/wargame/name/Hobbits");
					if (is == null) {
					    throw new RuntimeException("Fichier Hobbits introuvable dans le JAR");
					}
					br = new BufferedReader(new InputStreamReader(is));
	
			        for (int j = 0; j <= nom; j++) {
			            ligne = br.readLine();
			            if (ligne == null) break; // fin du fichier avant d'arriver à x
			        }
			        
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
				break;
			case(2):
				th = ISoldat.TypesH.HUMAIN;
			
				try {
					is = Carte.class.getResourceAsStream("/wargame/name/Humains");
					if (is == null) {
					    throw new RuntimeException("Fichier Humains introuvable dans le JAR");
					}
					br = new BufferedReader(new InputStreamReader(is));
	
			        for (int j = 0; j <= nom; j++) {
			            ligne = br.readLine();
			            if (ligne == null) break; // fin du fichier avant d'arriver à x
			        }
			        
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
				break;
			case(3):
				th = ISoldat.TypesH.NAIN;
			
				try {
					is = Carte.class.getResourceAsStream("/wargame/name/Nains");
					if (is == null) {
					    throw new RuntimeException("Fichier Nains introuvable dans le JAR");
					}
					br = new BufferedReader(new InputStreamReader(is));
	
			        for (int j = 0; j <= nom; j++) {
			            ligne = br.readLine();
			            if (ligne == null) break; // fin du fichier avant d'arriver à x
			        }
			        
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
				break;
			default:
				;
			}
			
			Heros h = new Heros(this, th, ligne, p);
			armeeHeros[i] = h;
			setElement(h,p);
		}
	}
	
	/**
	 * Permet d'initialiser l'armée du joueur 2
	 */
	public void initArmeeMonstre() {
		
		int type;
		int nom;
		
		String ligne = null;
        BufferedReader br;
        InputStream is;
		
		for (int i = 0; i< NB_MONSTRES;i++) {
			Position p = trouvePositionVideMonstre();
			Position p2 = new Position(p.getX() - 1,p.getY()); // On est un monstre donc il y a forcement de la place à gauche
			if (!verifPositionVideAutour(p)) { // Juste pour s'assurer qu'il y a au moins une position vide dans les cases adjacentes
				
				if (getElement(p2) instanceof Obstacle) {
					setElement(new Plaine(), p2); 
				}
				// Si ce n'est pas un obstacle alors c'est un soldat donc possibilité de déplacement future
				
			}
			type = (int) (Math.random() * ISoldat.nbTypeMonstre);
			nom = (int) (Math.random() * NB_NOMS);
			ISoldat.TypesM th = ISoldat.TypesM.GOBELIN;
			switch (type) {
			case(0):
				th = ISoldat.TypesM.GOBELIN;
			
				try {
					is = Carte.class.getResourceAsStream("/wargame/name/Gobelins");
					if (is == null) {
					    throw new RuntimeException("Fichier Gobelins introuvable dans le JAR");
					}
					br = new BufferedReader(new InputStreamReader(is));

			        for (int j = 0; j <= nom; j++) {
			            ligne = br.readLine();
			            if (ligne == null) break; // fin du fichier avant d'arriver à x
			        }
			        
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        
				break;
			case(1):
				th = ISoldat.TypesM.ORC;
			
				try {
					is = Carte.class.getResourceAsStream("/wargame/name/Orcs");
					if (is == null) {
					    throw new RuntimeException("Fichier Orcs introuvable dans le JAR");
					}
					br = new BufferedReader(new InputStreamReader(is));
	
			        for (int j = 0; j <= nom; j++) {
			            ligne = br.readLine();
			            if (ligne == null) break; // fin du fichier avant d'arriver à x
			        }
			        
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
				break;
			case(2):
				th = ISoldat.TypesM.TROLL;
			
				try {
					is = Carte.class.getResourceAsStream("/wargame/name/Trolls");
					if (is == null) {
					    throw new RuntimeException("Fichier Trolls introuvable dans le JAR");
					}
					br = new BufferedReader(new InputStreamReader(is));
	
			        for (int j = 0; j <= nom; j++) {
			            ligne = br.readLine();
			            if (ligne == null) break; // fin du fichier avant d'arriver à x
			        }
			        
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
				break;
			default:
				;
			}
			
			Monstre m = new Monstre(this, th, ligne, p);
			armeeMonstre[i] = m;
			setElement(m,p);
		}
	}
	
	/*
	 * Permet d'initialiser les obstacles sur la carte aléatoirement
	 */
	public void initObstacleAlea() {
		for (int i = 0; i< NB_OBSTACLES;i++) {
			Position p = trouvePositionVide();
			
			int type = (int) (Math.random() * Obstacle.nbObstacle);
			Obstacle.TypeObstacle th = Obstacle.TypeObstacle.ROCHER;
			switch (type) {
			case(0):
				th = Obstacle.TypeObstacle.ROCHER;
				break;
			case(1):
				th = Obstacle.TypeObstacle.FORET;
				break;
			case(2):
				th = Obstacle.TypeObstacle.EAU;
				break;
			default:
				;
			}
			
			setElement(new Obstacle(th, p),p);
		}
	}
	
	/*
	 * Verifie si il y a une case vide autours (8 directions)
	 * @param p position autour de laquelle on cherche
	 * @return boolean qui décrit si il y a ou non une case disponible
	 */
	private boolean verifPositionVideAutour(Position p) {
		int y,x;
		for (int i = -1;i<=1;i++) {
			y = p.getY() + i;
			for (int j = -1;j<=1;j++) {
				x = p.getX() + j;
				if (((x >= 0 && x < largeur) && (y >= 0 && y < hauteur)) && ((i!=0) || (j!=0)) )  {
					if (tab[y][x] instanceof Plaine) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/*
	 * Initialise une rivière sur la carte d'une certaine taille
	 * @param tailleRiviere taille de la rivière (nombre de case occupés)
	 */
	public void initRiviereAlea(int tailleRiviere) {
		int nb_riv;
		if (tailleRiviere > 0) {
			nb_riv = tailleRiviere;
			
			Position p = trouvePositionVide();
			setElement(new Obstacle(Obstacle.TypeObstacle.EAU, p),p);
			nb_riv --;
			
			
			while (nb_riv > 0 && verifPositionVideAutour(p)) {
				p = trouvePositionVide(p);
				setElement(new Obstacle(Obstacle.TypeObstacle.EAU, p),p);
				nb_riv --;
			}
			
		}
	}
	
	/*
	 * Initialise la carte du brouillard (plein)
	 */
	public void initBrouillard() {
		int i,j;
		for (i=0;i<hauteur;i++) {
			for (j=0;j<largeur;j++) {
				brouillard[i][j] = 1;
			}
		}
	}
	
	/*
	 * Met à jour la carte du brouillard
	 */
	public void actuBrouillard() {
		int k,i,j;
		
		for (i = 0; i < hauteur; i++) {
			for (j = 0; j < largeur; j++) {
				getElement(j,i).setEstVisible(false);
			}
		}
		initBrouillard(); // On remet tout le brouillard
		
		for (k=0;k<nbHerosVivant;k++) {
			Heros h = armeeHeros[k];
			int portee_visuel = h.getPortee();
			Position pos = h.getPos();
			
			
			if (pos != null) {
				int y = pos.getY();
				int x = pos.getX();
				
				for (i=(y - portee_visuel);i<=(y + portee_visuel);i++) {
					for (j=(x - portee_visuel);j<=(x + portee_visuel);j++) {
						if ((i>=0 && j>=0) && (i<hauteur && j<largeur)) {
							
							if (distance(x,y,j,i) <= portee_visuel) {
								brouillard[i][j] = 0;
								getElement(j, i).setEstVisible(true);
							}
						}
					}
				}
			}
			
		}
	}
	
	/*
	 * Affichage graphique de la carte et de ses éléments
	 * @param g outil graphique
	 */
	public void toutDessiner(Graphics g) {
		// TODO Stub de la méthode généré automatiquement
		
		actuBrouillard();
		String affichage_pv;
		
		
		for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
            	
            	
            	Image plaine, eau, rocher, foret, brouillard_img;
            	if ( x + (compteur_tour/2) < largeur - largeur/3) {
            		plaine = PLAINE.getImage();
            		eau = EAU.getImage();
            		rocher = ROCHER.getImage();
            		foret = FORET.getImage();
            		brouillard_img = BROUILLARD.getImage();
            	}else {
            		plaine = PLAINE2.getImage();
            		eau = LAVA.getImage();
            		rocher = ROCHER2.getImage();
            		foret = FORET2.getImage();
            		brouillard_img = BROUILLARD2.getImage();
            	}
            	
            	
            	if (brouillard[y][x] == 0) {
	            	switch (getElement(x,y).getClass().getSimpleName()) {
	            	case ("Plaine"):
	                    //g.setColor(COULEUR_PLAINE);
	            		g.drawImage(plaine, x * NB_PIX_CASE, y * NB_PIX_CASE, NB_PIX_CASE, NB_PIX_CASE, null);
	            
	                    //g.fillRect(y * NB_PIX_CASE, x * NB_PIX_CASE, NB_PIX_CASE, NB_PIX_CASE);
	                    break;
	            	case ("Obstacle"): // Obstacle: prendre en compte si c'est de l'eau, un rocher, une forêt (dans Obstacle.java)
	            		Obstacle o = (Obstacle) getElement(x,y);
	            		Obstacle.TypeObstacle to = o.getTYPE();
	            		switch (to) {
						case EAU:
							//g.setColor(COULEUR_EAU);
							//g.fillRect(y * NB_PIX_CASE, x * NB_PIX_CASE, NB_PIX_CASE, NB_PIX_CASE);
							g.drawImage(eau, x * NB_PIX_CASE, y * NB_PIX_CASE, NB_PIX_CASE, NB_PIX_CASE, null);
							
							break;
					
						case FORET:
							//g.setColor(COULEUR_FORET);
							//g.fillRect(y * NB_PIX_CASE, x * NB_PIX_CASE, NB_PIX_CASE, NB_PIX_CASE);
							g.drawImage(foret, x * NB_PIX_CASE, y * NB_PIX_CASE, NB_PIX_CASE, NB_PIX_CASE, null);
							break;
						case ROCHER:
							//g.setColor(COULEUR_ROCHER);
							//g.fillRect(y * NB_PIX_CASE, x * NB_PIX_CASE, NB_PIX_CASE, NB_PIX_CASE);
							g.drawImage(rocher, x * NB_PIX_CASE, y * NB_PIX_CASE, NB_PIX_CASE, NB_PIX_CASE, null);
							break;
	            		}
	                    break;
	            	case ("Heros"):    // Prendre en compte les héros déjà joués également
	                    //g.setColor(COULEUR_HEROS);
	                    //g.fillRect(y * NB_PIX_CASE, x * NB_PIX_CASE, NB_PIX_CASE, NB_PIX_CASE);
	            		
	            		// On affiche le fond (pour l'instant ça ne peut être que la plaine)
	            		g.drawImage(plaine, x * NB_PIX_CASE, y * NB_PIX_CASE, NB_PIX_CASE, NB_PIX_CASE, null); 
	            		g.drawImage(COEUR.getImage(), x * NB_PIX_CASE, y * NB_PIX_CASE, NB_PIX_CASE/2, NB_PIX_CASE/2, null);
	            		Heros h = (Heros) getElement(x,y);
	            		affichage_pv = "" + h.getPoints();
	    	            g.drawString(affichage_pv, x * NB_PIX_CASE + NB_PIX_CASE/10, y * NB_PIX_CASE + g.getFont().getSize() + NB_PIX_CASE/10);
	    	            if (h.peutJouer()) {
	    	            	g.drawImage(ECLAIR.getImage(), x * NB_PIX_CASE + NB_PIX_CASE/2, y * NB_PIX_CASE + NB_PIX_CASE/2, NB_PIX_CASE/2, NB_PIX_CASE/2, null);
	    	            }
	            		ISoldat.TypesH th = h.getTYPE();
	            		switch (th) {
						case ELF:
							g.drawImage(ELF.getImage(), x * NB_PIX_CASE, y * NB_PIX_CASE, NB_PIX_CASE, NB_PIX_CASE, null);
							break;
						case NAIN:
							g.drawImage(NAIN.getImage(), x * NB_PIX_CASE, y * NB_PIX_CASE, NB_PIX_CASE, NB_PIX_CASE, null);
							break;
						case HUMAIN:
							g.drawImage(HUMAIN.getImage(), x * NB_PIX_CASE, y * NB_PIX_CASE, NB_PIX_CASE, NB_PIX_CASE, null);
							break;
						case HOBBIT:
							g.drawImage(HOBBIT.getImage(), x * NB_PIX_CASE, y * NB_PIX_CASE, NB_PIX_CASE, NB_PIX_CASE, null);
							break;
	            		}
	                    break;
	            	case ("Monstre"):
	                    //g.setColor(COULEUR_MONSTRES);
	                    //g.fillRect(y * NB_PIX_CASE, x * NB_PIX_CASE, NB_PIX_CASE, NB_PIX_CASE);
	            		
	            		// On affiche le fond (pour l'instant ça ne peut être que la plaine)
	            		g.drawImage(plaine, x * NB_PIX_CASE, y * NB_PIX_CASE, NB_PIX_CASE, NB_PIX_CASE, null); 
	            		g.drawImage(COEUR.getImage(), x * NB_PIX_CASE, y * NB_PIX_CASE, NB_PIX_CASE/2, NB_PIX_CASE/2, null);
	            		Monstre m = (Monstre) getElement(x,y);
	            		affichage_pv = "" + m.getPoints();
	            		g.drawString(affichage_pv, x * NB_PIX_CASE + NB_PIX_CASE/10, y * NB_PIX_CASE + g.getFont().getSize() + NB_PIX_CASE/10);
	            		
	            		ISoldat.TypesM tm = m.getTYPE();
		        		switch (tm) {
						case TROLL:
							g.drawImage(TROLL.getImage(), x * NB_PIX_CASE, y * NB_PIX_CASE, NB_PIX_CASE, NB_PIX_CASE, null);
							break;
						case ORC:
							g.drawImage(ORC.getImage(), x * NB_PIX_CASE, y * NB_PIX_CASE, NB_PIX_CASE, NB_PIX_CASE, null);
							break;
						case GOBELIN:
							g.drawImage(GOBELIN.getImage(), x * NB_PIX_CASE, y * NB_PIX_CASE, NB_PIX_CASE, NB_PIX_CASE, null);
							break;
		        		}
	                    break;
	            	}
            	}else {
            		g.drawImage(brouillard_img, x * NB_PIX_CASE, y * NB_PIX_CASE, NB_PIX_CASE, NB_PIX_CASE, null);
            	}
            	
                g.setColor(COULEUR_TEXTE); // contour
                g.drawRect(x * NB_PIX_CASE, y * NB_PIX_CASE, NB_PIX_CASE, NB_PIX_CASE);
            }
        }
		g.setColor(COULEUR_SELECTION);
		if (select.getY() != -1) { // Si pas de selection x = -1 et y = -1
			if (select.getY() >= 0 && select.getY() < hauteur && select.getX() >= 0 && select.getX() < largeur ) {
				g.drawRect(select.getX() * NB_PIX_CASE, select.getY() * NB_PIX_CASE, NB_PIX_CASE, NB_PIX_CASE); // Draw : Y puis X
				
				System.out.println(" Case : " + getElement(select).getClass().getSimpleName());
				
				if (getElement(select) instanceof Heros) {  // Position getX getY
					Heros h = (Heros) getElement(select);
					case_selectionne = " Espèce : " + h.getTYPE() + ", nom :" + h.getNom();
					System.out.println(case_selectionne);
					 
					if (h.peutJouer()) {
						int portee_visuelle = h.getPortee();
						int portee_deplacement = h.getPorteeDeplacement();
						
						int y = select.getY();
						int x = select.getX();
						
						
						// Fonction qui affiche le champ d'action
						// D'abord le champ d'action vision puis celui de deplacement pour pas avoir de gêne dans l'affichage
						
						calculerChampAction(x, y, portee_visuelle, portee_deplacement);
						
						afficherChampAction(x,y,g);
						
						/*
						for (i=(y - portee);i<=(y + portee);i++) {
							for (j=(x - portee);j<=(x + portee);j++) {
								if ((i>=0 && j>=0) && (i<hauteur && j<largeur)) {
									g.setColor(COULEUR_CHAMP_ACTION);
									g.drawRect(j * NB_PIX_CASE, i * NB_PIX_CASE, NB_PIX_CASE, NB_PIX_CASE);
								}
							}
						}
						*/
						
					}
					
					
					
				}
				
				if( brouillard[select.getY()][select.getX()] != 1){
					if (getElement(select) instanceof Monstre) {  // Position getX getY
						Monstre m = (Monstre) getElement(select);
						case_selectionne = " Espèce : " + m.getTYPE() + ", nom :" + m.getNom();
						System.out.println(case_selectionne);
					}
					
					if (getElement(select) instanceof Obstacle) {  // Position getX getY
						Obstacle o = (Obstacle) getElement(select);
						System.out.println(" Type : " + o.getTYPE());
						Obstacle.TypeObstacle to = o.getTYPE();
						case_selectionne = to.toString();
					}
					
					if(getElement(select) instanceof Plaine) {
						Plaine p = (Plaine) getElement(select);
						case_selectionne = (p.getTYPE()).toString();
					}
				}else{
					case_selectionne = "BROUILLARD";
				}
				
				
				
				g.setColor(Color.BLACK);
				g.drawString(case_selectionne, 10, hauteur * NB_PIX_CASE + 20);
				g.drawString(str_action_Hero, 500, hauteur * NB_PIX_CASE + 20);
				
				
			}
		}
		
		if (etat != Etat.EN_COURS) {
			if (etat == Etat.VICTOIRE) {
				g.drawImage(VICTOIRE.getImage(), (int) (LARGEUR_JEU/2 - LARGEUR_TITRE/2), (int) (HAUTEUR_JEU/5 - HAUTEUR_TITRE/2), LARGEUR_TITRE, HAUTEUR_TITRE, null);
			}
			if (etat == Etat.MATCH_NUL) {
				g.drawImage(MATCH_NUL.getImage(), (int) (LARGEUR_JEU/2 - LARGEUR_TITRE/2), (int) (HAUTEUR_JEU/5 - HAUTEUR_TITRE/2), LARGEUR_TITRE, HAUTEUR_TITRE, null);
			}
			if (etat == Etat.DEFAITE) {
				g.drawImage(DEFAITE.getImage(), (int) (LARGEUR_JEU/2 - LARGEUR_TITRE/2), (int) (HAUTEUR_JEU/5 - HAUTEUR_TITRE/2), LARGEUR_TITRE, HAUTEUR_TITRE, null);
			}
		}
	}
	
	/*
	 * Permet de calculer la distance entre deux points
	 * @param x0 Coordonnée verticale du premier point
	 * @param y0 Coordonnée horizontale du premier point
	 * @param x1 Coordonnée verticale du deuxieme point
	 * @param y1 Coordonnée horizontale du deuxieme point
	 * @return double representant le resultat
	 */
	public double distance(int x0, int y0, int x1, int y1) {
	    return Math.sqrt(
	        (x1 - x0)*(x1 - x0) +
	        (y1 - y0)*(y1 - y0)
	    );
	}
	
	/*
	 * Permet de savoir si l'element de la carte est un element bloquant les déplacements/attaques
	 * @param x coordonnée verticale
	 * @param y coordonnée horizontale
	 * @return boolean de la réponse
	 */
	public boolean elementBloquant(int x, int y) {
		Element e = this.getElement(x, y);
		
		if (e instanceof Obstacle) {
			Obstacle o = (Obstacle) e;
			if ((o.getTYPE() == Obstacle.TypeObstacle.ROCHER) || (o.getTYPE() == Obstacle.TypeObstacle.FORET)) {
				return true;
			}
		}
		
		
		return false;
	}
	
	/*
	 * Algorithme inspiré de la méthode de Bresenham, permettant de savoir si l'element est accessible
	 * @param x0 coordonnée verticale du premier point
	 * @param y0 coordonnée horizontale du premier point
	 * @param x1 coordonnée verticale du deuxieme point
	 * @param y1 coordonnée horizontale du deuxieme point
	 * @return boolean pour savoir si accessible ou non
	 */
	public boolean ligneDeVue(int x0, int y0, int x1, int y1) {
	    int dx = Math.abs(x1 - x0);
	    int dy = Math.abs(y1 - y0);

	    int sx = x0 < x1 ? 1 : -1;
	    int sy = y0 < y1 ? 1 : -1;

	    int err = dx - dy;

	    int x = x0;
	    int y = y0;

	    while (x != x1 || y != y1) {

	        if (!(x == x0 && y == y0)) {
	            if (elementBloquant(x,y)) return false;
	        }

	        int e2 = 2 * err;
	        if (e2 > -dy) { err -= dy; x += sx; }
	        if (e2 < dx)  { err += dx; y += sy; }
	    }

	    return true;
	}
	
	/*
	 * Permet de calculer le champ de vision et le champ de deplacement de l'element selecetionné
	 * @param x0 coordonnée verticale
	 * @param y0 coordonnée horizontale
	 * @param portee_visuelle nombre décrivant la protée visuelle de l'element selectionné
	 * @param portee_deplacement nombre décrivant la protée de deplacement de l'element selectionné
	 */
	public void calculerChampAction(int x0, int y0, int portee_visuelle, int portee_deplacement) {
		
		vision = new int[portee_visuelle*2+1][portee_visuelle*2+1];
		deplacementPossible = new int[portee_deplacement*2+1][portee_deplacement*2+1];
		
		int y_deb = y0 - portee_visuelle;
		int x_deb = x0 - portee_visuelle;
		int y_fin = y0 + portee_visuelle;
		int x_fin = x0 + portee_visuelle;
		
	    for (int y = y_deb; y <= y_fin; y++) {
	        for (int x = x_deb; x <= x_fin; x++) {
	        	
	        	Position p = new Position(x,y);
	        	if (p.estValide()) {
	        		
		            if ((distance(x0, y0, x, y) <= portee_visuelle) && ligneDeVue(x0,y0,x,y)) {
		            	
		            	vision[y - y_deb][x - x_deb] = 1;
		            	
		            }else {
		            	
		            	vision[y - y_deb][x - x_deb] = -1;
		            	
		            }
		            
	        	}else {
	        		vision[y - y_deb][x - x_deb] = -1;
	        	}
	        }
	    }
	    
	    y_deb = y0 - portee_deplacement;
		x_deb = x0 - portee_deplacement;
		y_fin = y0 + portee_deplacement;
		x_fin = x0 + portee_deplacement;
	    
	    // On est obligé de le faire 2 fois pour que l'affichage soit correctement représenté
	    
	    for (int y = y_deb; y <= y_fin; y++) {
	        for (int x = x_deb; x <= x_fin; x++) {
	        	
	        	Position p = new Position(x,y);
	        	if (p.estValide()) {
	        		
		            if ((distance(x0, y0, x, y) <= portee_deplacement) && ligneDeVue(x0,y0,x,y)) {
		            	
		            	deplacementPossible[y - y_deb][x - x_deb] = 1;
		            	
		            }else {
		            	
		            	deplacementPossible[y - y_deb][x - x_deb] = -1;
		            	
		            }
	        	}else {
	        		vision[y - y_deb][x - x_deb] = -1;
	        	}
	        }
	    }
	    
	}
	
	/*
	 * Permet d'afficher les deux champs d'action de l'element selectionné
	 * @param x0 coordonnée verticale
	 * @param y0 coordonnée horizontale
	 * @param g outil graphique
	 */
	public void afficherChampAction(int x0, int y0, Graphics g) {
		
		if (vision == null || deplacementPossible == null) {
			return;
		}
		
		int portee_visuelle = vision.length / 2; // Nombre impaire : 11 / 2 = 5 car le perso au milieu
		int portee_deplacement = deplacementPossible.length / 2;
		
		int y_deb = y0 - portee_visuelle;
		int x_deb = x0 - portee_visuelle;
		int y_fin = y0 + portee_visuelle;
		int x_fin = x0 + portee_visuelle;
		
		for (int y = y_deb; y <= y_fin; y++) {
	        for (int x = x_deb; x <= x_fin; x++) {
	        	
	        	if ( vision[y - y_deb][x - x_deb] == 1) { // 1 veut dire vision, -1 non
		            	
		            g.setColor(COULEUR_CHAMP_ACTION_VISION);
					g.drawRect(x * NB_PIX_CASE, y * NB_PIX_CASE, NB_PIX_CASE, NB_PIX_CASE);
		            
	        	}
	        }
	    }
		
		y_deb = y0 - portee_deplacement;
		x_deb = x0 - portee_deplacement;
		y_fin = y0 + portee_deplacement;
		x_fin = x0 + portee_deplacement;
	    
	    // On est obligé de le faire 2 fois pour que l'affichage soit correctement représenté
	    
	    for (int y = y_deb; y <= y_fin; y++) {
	        for (int x = x_deb; x <= x_fin; x++) {
	        	
	        	if ( deplacementPossible[y - y_deb][x - x_deb] == 1) { // 1 veut dire vision, -1 non
		            	
	        		g.setColor(COULEUR_CHAMP_ACTION_DEPLACEMENT);
					g.drawRect(x * NB_PIX_CASE, y * NB_PIX_CASE, NB_PIX_CASE, NB_PIX_CASE);
					
	        	}
	        }
	    }
	}
	
	/*
	 * permet de recuperer la positionde l'element sélectionné
	 * @return Position correspondante
	 */
	public Position getSelect() {
		Position p = new Position(select.getX(),select.getY());
		return p;
	}
	
	/*
	 * Permet de changer la position décrivant l'element sélectionné
	 * @param p postion du nouvelle element selectionné
	 */
	public void setSelect(Position p) {
		select = new Position(p.getX(),p.getY());
	}
	
	/*
	 * Permet de changer la position décrivant l'element sélectionné
	 * @param x coordonnée verticale de la postion du nouvel element selectionné
	 * @param y coordonnée horizontale de la postion du nouvel element selectionné
	 */
	public void setSelect(int x, int y) {
		select = new Position(x,y);
	}
	
	/*
	 * fonction principale pour changer l'element selectionné
	 * @param y coordonné horizontale
	 * @param x coordonnée verticale
	 */
	public void marquerCase(int y, int x) {
		if (select.getY() < 0 && select.getX() < 0) {
			select = new Position(x,y);
			return;
		}
		// Coordonnées valides
		
		Position pos = new Position(x,y);
		if (getElement(select) instanceof Heros) {      
			// Le click précédent était sur un héros
			Heros h = (Heros) getElement(select);
			if (h.peutJouer()) {
				if ((getElement(pos) instanceof Obstacle) || (getElement(pos) instanceof Heros)) {
					// Si on selectionne un obstacle ou un héros, on n'utilise pas le héros.
					select = pos;
				}else {
					// Si on clique sur une plaine ou un monstre alors on va faire une action
					if (!actionHeros(select,pos)) {
						select = pos; // ActionHeros n'a pas conduit à un déplacement ou attaque 
					}else {
						h.aJouer();
						select.setX(-1);
						select.setY(-1);
					}
				}
			}else {
				select = pos;
			}
			
		}else {
			select = pos;
		}
		
		
		// Si l'on a sélectionner un héros alors on calcul son champ d'action
		
		// On le fait ici au cas où une action peut conduire à un changement
		if (select.getX() != -1 && select.getY() != -1) {
			
			if (getElement(select) instanceof Heros){
				Heros h2 = (Heros) getElement(select);
				
				int portee_visuelle = h2.getPortee();
				int portee_deplacement = h2.getPorteeDeplacement();
				
				int x2 = select.getX();
				int y2 = select.getY();
				
				calculerChampAction(x2,y2,portee_visuelle,portee_deplacement);
			}
		}
	}
	
	/*
	 * Action du héros à la position pos sur l'element à la position pos2
	 * @param pos position du héros
	 * @param pos2 position du dernier click
	 * @return boolean qui décrit si il y a eu action ou non
	 */
	public boolean actionHeros(Position pos, Position pos2) {
		// On a la pos du héros dans pos et la pos du click d'après dans pos2
		Heros h = (Heros) getElement(pos);
		int portee_visuelle = h.getPortee();
		int portee_deplacement = h.getPorteeDeplacement();
		
		int x = pos.getX();
		int y = pos.getY();
		
		int x2 = pos2.getX();
		int y2 = pos2.getY();
		
		int y_deb;
		int x_deb;
		
		System.out.println("" + pos + " | " + pos2);
		
		// Permet d'empêcher le cas où l'on est trop loin
		if (((y2 <= y+portee_visuelle) && (y2 >= y-portee_visuelle) && (x2 <= x+portee_visuelle) && (x2 >= x-portee_visuelle)) && vision != null){
			System.out.println("Dans portee visuelle");
			
			y_deb = y - portee_visuelle;
			x_deb = x - portee_visuelle;
			
			System.out.println("Centre vision = " + y + " | " + x + " | " + vision[y - y_deb][x - x_deb]);
			System.out.println("Centre vision = " + y2 + " | " + x2 + " | " + vision[y2 - y_deb][x2 - x_deb]);
			
			
			// Il faut aussi vérifier qu'on puisse regarder dans ce tableau également
			if (((y2 <= y+portee_deplacement) && (y2 >= y-portee_deplacement) && (x2 <= x+portee_deplacement) && (x2 >= x-portee_deplacement)) && deplacementPossible != null ) {
				System.out.println("Dans portee deplacement");
				
				
				y_deb = y - portee_deplacement;
				x_deb = x - portee_deplacement;
				 
				if((y2 - y_deb)>=0 && (y2 - y_deb)<deplacementPossible.length && (x2 - x_deb)>=0 && (x2 - x_deb)<deplacementPossible[0].length) {
				
					if (deplacementPossible[y2 - y_deb][x2 - x_deb] == 1) { // Si un déplacement est possible
						
						System.out.println("Deplacement possible");
						
						if (getElement(pos2) instanceof Plaine) {
							System.out.println("Déplacement");
							str_action_Hero = "Dernière Action : Déplacement en (" + x2 + "," + y2 +")";
							
							deplaceSoldat(pos2,h);
							h.seDeplace(pos2);
							
							return true;
						}
						if (getElement(pos2) instanceof Monstre) {
							System.out.println("Attaque");
							// Il faudra calculer ici si l'on peut ou non toucher le monstre ( méthode peutAttaquer(Soldat s) dans Soldat par exemple) 
							Monstre m = (Monstre) getElement(pos2);
							str_action_Hero = "Dernière Action : Attaque en " + x2 + " " + y2;
							
							//h.peutAttaquer(pos2);
							h.combat(m);
							
							
							return true;
						}
					 }
				}
				
			}
					
			// Si l'on peut pas se déplacer on peut peut être voir -> attaque distante possible
				
			y_deb = y - portee_visuelle;
			x_deb = x - portee_visuelle;
			
			if((y2 - y_deb)>=0 && (y2 - y_deb)<vision.length && (x2 - x_deb)>=0 && (x2 - x_deb)<vision[0].length) {
			
				if (vision[y2 - y_deb][x2 - x_deb] == 1) { // Si dans le champ de vision
						
					System.out.println("Vision");
						
					if (getElement(pos2) instanceof Monstre) {
						System.out.println("Attaque");
						// Il faudra calculer ici si l'on peut ou non toucher le monstre ( méthode peutAttaquer(Soldat s) dans Soldat par exemple) 
						Monstre m = (Monstre) getElement(pos2);
						str_action_Hero = "Dernière Action : Attaque en " + x2 + " " + y2;
							
						//h.peutAttaquer(pos2);
						h.combat(m);
						
						
						return true;
					}
						
				}
			}
		}
		
		// Hors de portée
		str_action_Hero = "Action impossible";
		
		// Si on a ni attaquer ni deplacer, on a pas cliqué dans le champ d'action
		//panneauJeu.repaint();
		return false;
	}
	
	/*
	 * Permet de savoir si il y a un heros a coté de la position
	 * @param pos postion autour de laquelle on cherche
	 * @return boolean réponse
	 */
	public boolean herosACote(Position pos) {
		for (int i=-1; i<=1;i++) {
			for (int j=-1; j<=1;j++) {
				Position p2 = new Position(pos.getX()+j,pos.getY()+i);
				//System.out.println(p2);
				if (p2.estValide() && getElement(p2) instanceof Heros) {
					Monstre m = (Monstre) getElement(pos);
					Heros h = (Heros) getElement(p2);
					m.combat(h);
					
					
					return true;
				}
			}
		}
		return false;
	}
	
	/*
	 * fonction principale du fin de tour
	 */
	public void finDeTour() {
		compteur_tour++;
		for (int i=0;i<nbHerosVivant;i++) {
			Heros h = armeeHeros[i];
			int pv = h.getPoints();
			
			if (h.peutJouer()) { // si il peut jouer mais qu'il ne joue pas le personage regagne un peu de vie.
				if (pv < h.getPointsMAX()) {
					h.setPoints(pv + 1);
				}
			}
			
			h.peutRejouer();
		}
		
		for (int i=0;i<nbMonstreVivant;i++) {
			Monstre m = armeeMonstre[i];
			Position p = m.getPos();
			int pv = m.getPoints();
			if (pv < m.getPointsMAX()) {
				m.setPoints(pv + 1);
			}
			if (!herosACote(p)) {
				Position p2 = trouvePositionVide(p);
				setElement(new Plaine(), p);
				armeeMonstre[i].setPos(p2);
				setElement(armeeMonstre[i], p2);
			}
		}
		// IA :  Jouer le tour des monstres
		/*Ordinateur ia = new Ordinateur(hauteur, largeur, this);
		ia.jouerTour();*/
	}
	
	/*
	public void jouerSoldats(PanneauJeu pj) {
		
	}
	*/
	
	public Monstre[] getArmeeMonstre(){
		return armeeMonstre; 
	}
	public Heros[] getArmeeHeros(){
		return armeeHeros; 
	}
	
	public int getNbHerosVivant() {
		return nbHerosVivant;
	}

	public int getNbMonstreVivant() {
		return nbMonstreVivant;
	}
	
}