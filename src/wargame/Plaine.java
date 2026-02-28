package wargame;

import java.io.Serializable;

/*
 * La classe Plaine gère l'element de base de la carte
 */
public class Plaine extends Element implements Serializable{
	private TypePlaine TYPE = TypePlaine.PLAINE;  // Ajout du champ TYPE
	private static final long serialVersionUID = 1L; // contrôle de la compatibilité
	
	/*
	 * Constructeur
	 */
	public Plaine() {
		super();
	}
	public enum TypePlaine{
		PLAINE;
	}
	
	/*
	 * Recuperation du type
	 */
	public TypePlaine getTYPE() {
		return TYPE;
	}
	
	/*
	 * Transformation chaine de caractere
	 */
	public String toString() {
		String m = "";
		
		m += "Plaine";
		
		return m;
	}
}
