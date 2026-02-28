package wargame;
import java.awt.Color;
import java.io.Serializable;


/*
 * La classe Obstacle gère les éléments de la carte qui bloquent les unités
 */
public class Obstacle extends Element implements Serializable {
	public static int nbObstacle = 2;
	private static final long serialVersionUID = 1L; // contrôle de la compatibilité
	
	public enum TypeObstacle {
		ROCHER (IConfig.COULEUR_ROCHER), FORET (IConfig.COULEUR_FORET), EAU (IConfig.COULEUR_EAU);
		//private final Color COULEUR;
		TypeObstacle(Color couleur) { /*COULEUR = couleur;*/ }
		public static TypeObstacle getObstacleAlea() {
			return values()[(int)(Math.random()*values().length)];
		}
	}
	private TypeObstacle TYPE;
	Obstacle(TypeObstacle type, Position pos) { TYPE = type; this.setPos(pos);; }
	public String toString() { return ""+TYPE; }
	
	public TypeObstacle getTYPE() {
		return TYPE;
	}
	
}