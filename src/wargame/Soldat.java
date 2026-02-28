package wargame;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/*
 * La classe Soldat gère l'ensemble des unités
 */
public abstract class Soldat extends Element implements ISoldat,Serializable{
	private final int POINTS_DE_VIE_MAX, PUISSANCE, TIR, PORTEE_VISUELLE, PORTEE_DEPLACEMENT;
	private int pointsDeVie;
	private Carte carte;
	private static final long serialVersionUID = 1L; // contrôle de la compatibilité
	
	/*
	 * Constructeur
	 */
	Soldat(Carte carte, int pts, int portee_visuelle, int portee_deplacement, int puiss, int tir, Position pos) {
		POINTS_DE_VIE_MAX = pointsDeVie = pts;
		PORTEE_VISUELLE = portee_visuelle; PORTEE_DEPLACEMENT = portee_deplacement;PUISSANCE = puiss; TIR = tir;
		this.carte = carte; setPos(pos);
	}
	
	public int getPoints() {
		return pointsDeVie; 
	}
	
	public void setPoints(int n) {
		pointsDeVie = n; 
	}
	
	public int getPointsMAX() {
		return POINTS_DE_VIE_MAX; 
	}
	
	
	public int getTour() {
		return 0; /* A remplacer */
	}
	
	public int getPortee() {
		return PORTEE_VISUELLE; 
	}
	
	public int getPorteeDeplacement() {
		return PORTEE_DEPLACEMENT; 
	}
	
	public void joueTour(int tour) {
		return ;
	}
	
	public boolean peutAttaquer(Position pos) { // Pos : position de l'adversaire
		// Calcul si l'on peut ou non attaquer l'ennemie
		Position p = getPos();
		if (p.estVoisine(pos)) {
			return true;
		}
		int portee = this.getPortee();
		if ((pos.getY() <= p.getY()+portee) && (pos.getY() >= p.getY()-portee) && (pos.getX() <= p.getX()+portee) && (pos.getX() >= p.getX()-portee)){
			if (this.TIR > 0) {
				return true;
			}
		}
		return false;
	}
	
	
	public void combat_bis(Soldat soldat){
		int puissance_coup;
		InputStream is;
		if(getPos().estVoisine(soldat.getPos())){//corps à corps 
			puissance_coup = (int)(Math.random() * (this.PUISSANCE + 1)); 
			soldat.pointsDeVie -= puissance_coup;
			System.out.println("Attaque : " + puissance_coup + ", Il reste :" + soldat.pointsDeVie);
			try {
                Clip clip = AudioSystem.getClip();
                
                is = getClass().getResourceAsStream("/wargame/sons/epee.wav");
                clip.open(AudioSystem.getAudioInputStream(new BufferedInputStream(is)));
                
                clip.start();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
		}else{ // combat à distance
			puissance_coup = (int)(Math.random() * (this.TIR + 1)); 
			soldat.pointsDeVie -= puissance_coup;
			System.out.println("Attaque : " + puissance_coup + ", Il reste :" + soldat.pointsDeVie);
			try {
                Clip clip = AudioSystem.getClip();
                
                is = getClass().getResourceAsStream("/wargame/sons/tir_arc.wav");
                clip.open(AudioSystem.getAudioInputStream(new BufferedInputStream(is)));
                
                clip.start();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
		}
	}
	
	public boolean est_mort(){	
		return (this.pointsDeVie <= 0);
	}
	
	public void combat(Soldat soldat) {
		combat_bis(soldat);
		
		if(soldat.est_mort()){
			soldat.carte.mort(soldat);
		}else {
			if (soldat.peutAttaquer(this.getPos())) {
				soldat.combat_bis(this);
				
				if (est_mort()) {
					this.carte.mort(this);
				}
			}
			
		}
	}

	public void seDeplace(Position newPos) {
		Position pos = getPos();
		System.out.println("Ancienne pos: " + pos.getY() + "," + pos.getX());
		System.out.println("Nouvelle pos: " + newPos.getY() + "," + newPos.getX());
		 
		 // mettre à jour la position d'un héros 
		 setPos(newPos);
	}
	
}