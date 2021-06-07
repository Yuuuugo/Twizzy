package test;

import java.util.Arrays;
import java.util.List;
import org.opencv.core.*;
import org.opencv.highgui.*;


public class Principale {

	public static void main(String[] args)
	{
		//Ouverture le l'image et saturation des rouges
		System.loadLibrary("opencv_java2413");
		Mat m=Highgui.imread("p3.jpg",Highgui.CV_LOAD_IMAGE_COLOR);
		MaBibliothequeTraitementImageEtendue.afficheImage("Image testée", m);
		Mat transformee=MaBibliothequeTraitementImageEtendue.transformeBGRversHSV(m);
		//la methode seuillage est ici extraite de l'archivage jar du meme nom 
		Mat saturee=MaBibliothequeTraitementImage.seuillage(transformee, 6, 170, 110);
		Mat objetrond = null;

		//Création d'une liste des contours à partir de l'image saturée
		List<MatOfPoint> ListeContours= MaBibliothequeTraitementImageEtendue.ExtractContours(saturee);
		int i=0;
		double [] scores=new double [6];
		//Pour tous les contours de la liste
		for (MatOfPoint contour: ListeContours  ){
			i++;
			objetrond=MaBibliothequeTraitementImageEtendue.DetectForm(m,contour);

			if (objetrond!=null){
				MaBibliothequeTraitementImage.afficheImage("Objet rond detécté", objetrond);
				scores[0]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"ref30.jpg");
				scores[1]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"ref50.jpg");
				scores[2]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"ref70.jpg");
				scores[3]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"ref90.jpg");
				scores[4]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"ref110.jpg");
				scores[5]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"refdouble.jpg");


				//recherche de l'index du maximum et affichage du panneau detecté
				double scoremax=Double.POSITIVE_INFINITY;
				int indexmax=0;
				for(int j=0;j<scores.length;j++){
					if (scores[j]<scoremax){
						scoremax=scores[j];
						indexmax=j;}}	
				if(scoremax==Double.POSITIVE_INFINITY){
					System.out.println("Aucun Panneau détécté");}
				else{switch(indexmax){
				case -1:;break;
				case 0:System.out.println("Panneau 30 détécté");break;
				case 1:System.out.println("Panneau 50 détécté");break;
				case 2:System.out.println("Panneau 70 détécté");break;
				case 3:System.out.println("Panneau 90 détécté");break;
				case 4:System.out.println("Panneau 110 détécté");break;
				case 5:System.out.println("Panneau interdiction de dépasser détécté");break;
				}}

			}
		}	


	}
}