package wargame;

import java.io.Serializable;

/*
 * La classe Monstre gère les unités du joueur 2 (joueur ordinateur)
 */
public class Monstre extends Soldat implements Serializable{
	private final TypesM TYPE;
	private static final long serialVersionUID = 1L; // contrôle de la compatibilité

	/*
	 * Constructeur
	 */
	public Monstre(Carte carte, TypesM type, String nom, Position pos) {
		super(carte, type.getPoints(), type.getPortee(), type.getPorteeDeplacement(),
		type.getPuissance(), type.getTir(), pos);
		this.setNom(nom); TYPE = type;
	}
	
	public TypesM getTYPE() {
		return TYPE;
	}

	/*
	 * Transformation en chaine de caractere
	 */
	public String toString() {
		String m = "";
		TypesM type = this.getTYPE();
		
		m += "Type : " + type + " | ";
		m += "Nom : " + this.getNom() + " | \n";
		m += "PV : " + this.getPoints() + "/" + this.getPointsMAX() + " | \n";
		m += "Portee : " + type.getPortee() + " | ";
		m += "Puissance : " + type.getPuissance() + " | ";
		m += "Tir : " + type.getTir() + "\n";
		
		return m;
	}
}