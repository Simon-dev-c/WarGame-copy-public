package wargame;

import java.io.Serializable;

/*
 * La classe Position gère la position d’un élément sur la carte
 */
public class Position implements IConfig,Serializable{
	private int x, y;
	private static final long serialVersionUID = 1L; // contrôle de la compatibilité
	
	/*
	 * Constructeur
	 * @param x
	 * @pparam y 
	 */
	Position(int x, int y) { this.x = x; this.y = y; }
	
	/*
	 * Accesseur à x
	 */
	public int getX() { return x; }
	/*
	 * Accesseur à y
	 */
	public int getY() { return y; }
	/*
	 * Mutateur à x
	 */
	public void setX(int x) { this.x = x; }
	/*
	 * Mutateur à y
	 */
	public void setY(int y) { this.y = y; }
	/*
	 * Position valide selon la taille de la carte
	 * @return boolean 
	 */
	public boolean estValide() {
		if (x<0 || x>=LARGEUR_CARTE || y<0 || y>=HAUTEUR_CARTE) return false; else return true;
	}
	/*
	 * Transformation en chaine de caractère
	 * @return String
	 */
	public String toString() { return "("+x+","+y+")"; }
	/*
	 * Décrit si la position est voisine de celle en paramètre
	 * @param pos
	 * @return boolean
	 */
	public boolean estVoisine(Position pos) {
		return ((Math.abs(x-pos.x)<=1) && (Math.abs(y-pos.y)<=1));
	}
}