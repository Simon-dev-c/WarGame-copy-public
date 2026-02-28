package wargame;

import java.io.Serializable;

/*
 * La classe Element gère les éléments de base à la carte
 */
public abstract class Element implements Serializable{
	private boolean estVisible;
	private String nom;
	private Position pos;
	
	private static final long serialVersionUID = 1L; // contrôle de la compatibilité
	
	/*
	 * Constructeur de l'element de base
	 */
	public Element() {
		this.estVisible = true;
		this.nom = "vide";
		this.pos = new Position(-1,-1);
	}
	
	/*
	 * permet de savoir si il est visible
	 * @return boolean résultat
	 */
	public boolean EstVisible() {
		return estVisible;
	}
	
	/*
	 * definit si il est visible
	 * @param estVisible nouvelle valeur
	 */
	public void setEstVisible(boolean estVisible) {
		this.estVisible = estVisible;
	}
	
	/*
	 * Recuperation du nom
	 * @return String nom
	 */
	public String getNom() {
		return nom;
	}
	
	/*
	 * Définit par un autre nom
	 * @param nom nouveau nom
	 */
	public void setNom(String nom) {
		this.nom = nom;
	}
	
	/*
	 * recuperation de la position
	 * @return Position de l'element
	 */
	public Position getPos() {
		return pos;
	}
	
	/*
	 * Mis a jour de la position
	 * @param pos nouvelle position
	 */
	public void setPos(Position pos) {
		this.pos.setY(pos.getY());
		this.pos.setX(pos.getX());
	}
	
	/*
	 * Mis a jour de la position
	 * @param x coordonnée verticale de la nouvelle position
	 * @parem y coordonnée horizontale de la nouvelle position
	 */
	public void setPos(int x, int y) {
		this.pos.setY(y);
		this.pos.setX(x);
	}
	
	/*
	 * Méthode de transformation automatique en chaine de caractère
	 * @return String chaine de caractère correspondante
	 */
	public String toString() {
		String m = "";
		
		m += "Nom : " + nom;
		
		return m;
	}
}
