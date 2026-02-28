package wargame;
import java.awt.Graphics;

/*
 * L'interface ICarte donne la signature des méthodes de Carte
 */
public interface ICarte {
	/**
	 * Permet de recuperer un element du tableau
	 * @param pos position de l'element
	 * @return element du plateau correspondant
	 */
	Element getElement(Position pos);
	/**
	 * Trouve aléatoirement une position vide sur la carte
	 * @return Position de l'element trouvé
	 */
	Position trouvePositionVide();
	/**
	 * Trouve une position adjacente vide (les 8 cases autours)
	 * @param pos position autour de laquelle on veut trouver une case vide
	 * @return Position trouvé
	 */
	Position trouvePositionVide(Position pos);
	/**
	 * Trouve aléatoirement un héros sur la carte
	 * @return Heros trouvé
	 */
	Heros trouveHeros();
	/**
	 * Trouve aléatoirement un héros sur la carte autour de la positon
	 * @param pos position autour de laquelle on cherche
	 * @return Heros trouvé
	 */
	Heros trouveHeros(Position pos);
	/**
	 * Déplace un soldat à une position
	 * @param pos position
	 * @param soldat soldat
	 * @return boolean qui décrit si le deplacement a été effectué correctement
	 */
	boolean deplaceSoldat(Position pos, Soldat soldat);
	/**
	 * Gere le cas où un soldat meures
	 * @param perso soldat qui est mort
	 */
	void mort(Soldat perso);
	/*
	 * Action du héros à la position pos sur l'element à la position pos2
	 * @param pos position du héros
	 * @param pos2 position du dernier click
	 * @return boolean qui décrit si il y a eu action ou non
	 */
	boolean actionHeros(Position pos, Position pos2);
	//void jouerSoldats(PanneauJeu pj);  // Non utilisée
	
	/*
	 * Affichage graphique de la carte et de ses éléments
	 * @param g outil graphique
	 */
	void toutDessiner(Graphics g);
}