package wargame;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JPanel;

/*
 * La classe SauveCharge gère la sauvegarde et le chargement des données
 */
public class SauveCharge{
	
	/*
	 * Méthode pour sauvegarder la carte dans un fichier
	 * @param carte
	 * @param fichier
	 */
	public static void sauvegarder(Carte carte,File fichier) throws IOException{
		if(carte == null) throw new IllegalArgumentException("Carte null");
		if( fichier == null) throw new IllegalArgumentException("Sauvegarde: fichier null"); 
		
		File parent = fichier.getParentFile();
		if(parent != null && !parent.exists()){
			parent.mkdirs();
		}
		
		try (ObjectOutputStream oos = new ObjectOutputStream (new FileOutputStream(fichier) )){ //Ouvre un flux d'écriture vers un fichier 
			oos.writeObject(carte); // Sérialise l'objet carte et l'écrit dans le fichier
		}
		
		
	}
	
	/*
	 * Méthode pour charger le contenu du fichier dans le jeu
	 * @param fichier
	 * @param panneauJeu
	 * @return Carte Carte obtenu par le chargement des données
	 */
	public static Carte charger(File fichier,JPanel panneauJeu) throws IOException,ClassNotFoundException{
		if( fichier == null) throw new IllegalArgumentException("Chargement: fichier null"); 
		
		if(!fichier.exists()){
			throw new FileNotFoundException("Le fichier de sauvegarde n'existe.");
		}
		
		try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fichier)) ){
			Object obj = ois.readObject();
			if(!(obj instanceof Carte)){
				throw new IOException("Fichier de sauvegarde invalide n'est pas le type (Carte)");
			}
			Carte carte = (Carte) obj;
			carte.actuBrouillard();
			return carte;
		}
	}
	
	/*
	 * Permet de savoir si une sauvegarde existe dans le fichier
	 * @param fichier
	 * @return boolean
	 */
	public static boolean sauvegardeExiste(File fichier){
		if( fichier != null && fichier.exists() && fichier.canRead() ){
			return true;
		}else{
			return false; 
		}
	} 
	
}