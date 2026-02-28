package wargame;

/*
 * L'interface ISoldat donne la signature des méthodes de Soldat
 */
public interface ISoldat {
	static int nbTypeHeros = 4;
	static int nbTypeMonstre = 3;
   static enum TypesH {
      HUMAIN (40,3,2,10,2), NAIN (80,1,1,20,0), ELF (70,5,3,10,6), HOBBIT (20,3,2,5,2);
      private final int POINTS_DE_VIE, PORTEE_VISUELLE, PORTEE_DEPLACEMENT, PUISSANCE, TIR;
      
      TypesH(int points, int portee_visuelle, int portee_deplacement, int puissance, int tir) {
POINTS_DE_VIE = points; PORTEE_VISUELLE = portee_visuelle; PORTEE_DEPLACEMENT = portee_deplacement;
PUISSANCE = puissance; TIR = tir;
      }
      public int getPoints() { return POINTS_DE_VIE; }
      public int getPortee() { return PORTEE_VISUELLE; }
      public int getPorteeDeplacement() { return PORTEE_DEPLACEMENT; }
      public int getPuissance() { return PUISSANCE; }
      public int getTir() { return TIR; }
      public static TypesH getTypeHAlea() {
         return values()[(int)(Math.random()*values().length)];
      }
   }
   public static enum TypesM {
      TROLL (100,1,1,30,0), ORC (40,2,1,10,3), GOBELIN (20,2,1,5,2);
      private final int POINTS_DE_VIE, PORTEE_VISUELLE, PORTEE_DEPLACEMENT, PUISSANCE, TIR;
      TypesM(int points, int portee_visuelle, int portee_deplacement, int puissance, int tir) {
POINTS_DE_VIE = points; PORTEE_VISUELLE = portee_visuelle; PORTEE_DEPLACEMENT = portee_deplacement;
PUISSANCE = puissance; TIR = tir;
      }
      public int getPoints() { return POINTS_DE_VIE; }
      public int getPortee() { return PORTEE_VISUELLE; }
      public int getPorteeDeplacement() { return PORTEE_DEPLACEMENT; }
      public int getPuissance() { return PUISSANCE; }
      public int getTir() { return TIR; } 
      public static TypesM getTypeMAlea() {
         return values()[(int)(Math.random()*values().length)];
      }
   }
   
   /*
    * Recuperation des points de vies
    */
   int getPoints();
   /*
    * Recuperation de se il peut jouer
    */
   int getTour();
   /*
    * Recuperation de la portee visuelle
    */
   int getPortee();
   /*
    * Recuperation de la portee de deplacement
    */
   int getPorteeDeplacement();

   void joueTour(int tour);
   /*
    * Permet de savoir si l'on peut attaquer a la position pos
    * @param pos
    * @return boolean
    */
   boolean peutAttaquer(Position pos);
   /*
    * Permet de savoir si le soldat est mort
    * @return boolean
    */
   boolean est_mort();
   /*
    * Gere le combat avec un soldat
    * @parm soldat
    */
   void combat(Soldat soldat);
   /*
    * Deplace l'element vers une nouvelle position
    * @param newPos nouvelle position
    */
   void seDeplace(Position newPos);
}