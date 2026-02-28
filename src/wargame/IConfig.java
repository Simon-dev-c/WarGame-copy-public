package wargame;
import java.awt.Color;

import javax.swing.ImageIcon;

/*
 * L’interface IConfig rassemble les principaux paramètres du jeu.
 */
public interface IConfig {
	int LARGEUR_CARTE = 25; int HAUTEUR_CARTE = 15; // en nombre de cases
	int NB_PIX_CASE = 50;
	int POSITION_X = 100; int POSITION_Y = 50; // Position de la fen�tre
	int NB_HEROS = 6; int NB_MONSTRES = 15; int NB_OBSTACLES = 80;
	int NB_NOMS = 20; // 20 noms différents pour chaque espèces
	
	int HAUTEUR_BARRE_MENU = 30;
	
	int LARGEUR_JEU = LARGEUR_CARTE * NB_PIX_CASE + (28 * 2);
	int HAUTEUR_JEU = (HAUTEUR_CARTE * NB_PIX_CASE) + HAUTEUR_BARRE_MENU + (28 * 2) ; // 28 sur mac
	
	int LARGEUR_TITRE = 550; 
	int HAUTEUR_TITRE = 250;
	
	int LARGEUR_BOUTON = 400;
	int HAUTEUR_BOUTON = 100;
	
	int STARTY = 250;
	int ESPACEMENT = 150;
	
	
	Color COULEUR_VIDE = Color.white, COULEUR_INCONNU = Color.lightGray;
	Color COULEUR_TEXTE = Color.black, COULEUR_MONSTRES = Color.black;
	Color COULEUR_HEROS = Color.red, COULEUR_HEROS_DEJA_JOUE = Color.pink;
	Color COULEUR_EAU = Color.blue, COULEUR_FORET = Color.getHSBColor((float) 0.35,(float) 0.95,(float) 0.55), COULEUR_ROCHER = Color.gray;
	Color COULEUR_PLAINE = Color.green; Color COULEUR_SELECTION = Color.red;
	
	Color COULEUR_CHAMP_ACTION_VISION = Color.pink; Color COULEUR_CHAMP_ACTION_DEPLACEMENT = Color.red;
	
	
	
	ImageIcon ECLAIR = new ImageIcon(IConfig.class.getResource("/wargame/images/units/eclair.png"));
	ImageIcon COEUR = new ImageIcon(IConfig.class.getResource("/wargame/images/units/coeur.png"));
	ImageIcon ORC = new ImageIcon(IConfig.class.getResource("/wargame/images/units/Orc.png"));
	ImageIcon TROLL = new ImageIcon(IConfig.class.getResource("/wargame/images/units/Troll.png"));
	ImageIcon GOBELIN = new ImageIcon(IConfig.class.getResource("/wargame/images/units/Gobelin.png"));
	ImageIcon ELF = new ImageIcon(IConfig.class.getResource("/wargame/images/units/Elf.png"));
	ImageIcon NAIN = new ImageIcon(IConfig.class.getResource("/wargame/images/units/Nain.png"));
	ImageIcon HUMAIN = new ImageIcon(IConfig.class.getResource("/wargame/images/units/Humain.png"));
	ImageIcon HOBBIT = new ImageIcon(IConfig.class.getResource("/wargame/images/units/Hobbit.png"));
	ImageIcon PLAINE = new ImageIcon(IConfig.class.getResource("/wargame/images/terrain/Plaine.png"));
	ImageIcon PLAINE2 = new ImageIcon(IConfig.class.getResource("/wargame/images/terrain/Plaine2.png"));
	ImageIcon EAU = new ImageIcon(IConfig.class.getResource("/wargame/images/terrain/Eau.png"));
	ImageIcon LAVA = new ImageIcon(IConfig.class.getResource("/wargame/images/terrain/Lava.png"));
	ImageIcon FORET = new ImageIcon(IConfig.class.getResource("/wargame/images/terrain/Foret.png"));
	ImageIcon FORET2 = new ImageIcon(IConfig.class.getResource("/wargame/images/terrain/Foret2.png"));
	ImageIcon ROCHER = new ImageIcon(IConfig.class.getResource("/wargame/images/terrain/Rocher.png"));
	ImageIcon ROCHER2 = new ImageIcon(IConfig.class.getResource("/wargame/images/terrain/Rocher2.png"));
	ImageIcon BROUILLARD = new ImageIcon(IConfig.class.getResource("/wargame/images/terrain/Brouillard.png"));
	ImageIcon BROUILLARD2 = new ImageIcon(IConfig.class.getResource("/wargame/images/terrain/Brouillard2.png"));
	
	ImageIcon FOND = new ImageIcon(IConfig.class.getResource("/wargame/images/menu/fond.png"));
	
	ImageIcon TITRE = new ImageIcon(IConfig.class.getResource("/wargame/images/menu/titre4.png"));
	ImageIcon BOUTON = new ImageIcon(IConfig.class.getResource("/wargame/images/menu/bouton.png"));
	
	ImageIcon VICTOIRE = new ImageIcon(IConfig.class.getResource("/wargame/images/menu/Victoire.png"));
	ImageIcon MATCH_NUL = new ImageIcon(IConfig.class.getResource("/wargame/images/menu/Match_nul.png"));
	ImageIcon DEFAITE = new ImageIcon(IConfig.class.getResource("/wargame/images/menu/Défaite.png"));
	
}