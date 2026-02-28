package wargame;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

/*
 * La classe PanneauJeu gère l’affichage de la carte.
 */
public class PanneauJeu extends JPanel implements IConfig{
	private static final long serialVersionUID = -5386655355068292584L;
	private Carte map;
    
	/*
	 * Constructeur
	 * @param map carte du jeu
	 */
	public PanneauJeu(Carte map) {
		this.map = map;
		
		setPreferredSize(new java.awt.Dimension((LARGEUR_CARTE * NB_PIX_CASE) , (HAUTEUR_CARTE * NB_PIX_CASE) ));
	}

	/*
	 * Affichage
	 * @param g outil graphique
	 */
	protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Dessin du tableau
        map.toutDessiner(g);
        
        if( FenetreJeu.isDragging() && FenetreJeu.getDraggedElement()!= null ){
        	drawDraggedElement(g);
        }
        
    }
	
	
	
	private void drawDraggedElement(Graphics g){
		//Element draggedElement = FenetreJeu.getDraggedElement();
		int mouseX = FenetreJeu.getCurrentMouseX();
		int mouseY= FenetreJeu.getCurrentMouseY();
		
		g.setColor(new Color(255,255,255,128));
		g.fillRect(mouseX-NB_PIX_CASE/2, mouseY-NB_PIX_CASE/2, NB_PIX_CASE, NB_PIX_CASE);
		
		/*
		if(draggedElement instanceof Heros){
			Heros h = (Heros) draggedElement;
			switch(h.getTYPE()){
			//case  HUMAIN:
			//	g.drawImage(HUMAIN.getImage(), mouseX-NB_PIX_CASE/2, mouseY-NB_PIX_CASE/2, NB_PIX_CASE, NB_PIX_CASE,null);
			//break;
			}
		}
		*/
		
	}
	
	
}