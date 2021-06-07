import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;



public class testVideo {
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
			}

	static Mat imag = null;

	public static void main(String[] args) {
		
		
		JFrame jframe = new JFrame("Detection de panneaux sur un flux vidéo");
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JLabel vidpanel = new JLabel();
		jframe.setContentPane(vidpanel);
		jframe.setSize(720, 480);
		jframe.setVisible(true);
		int nbr30=0;
		int nbr50=0;
		int nbr70=0;
		int nbr90=0;
		int nbr110=0;
		int nbrdep=0;
		int valeurprec=0;
		//Mat frame =Highgui.imread("p8.jpg",Highgui.CV_LOAD_IMAGE_COLOR);
		
		VideoCapture camera = new VideoCapture("video1.mp4");
		
		 /*if (!camera.isOpened()) {
	            System.out.println("Error! Camera can't be opened!");
	            return;
	        }*/
		Mat PanneauAAnalyser = null;
		Mat frame=new Mat();
			while (camera.read(frame)) {
			//A completer
		//MaBibliothequeTraitementImageEtendue.afficheImage("Image testée", frame);
		Mat transformee=MaBibliothequeTraitementImageEtendue.transformeBGRversHSV(frame);
		//la methode seuillage est ici extraite de l'archivage jar du meme nom 
		Mat saturee=MaBibliothequeTraitementImage.seuillage(transformee, 6, 170, 110);
		Mat objetrond = null;

		//Création d'une liste des contours à partir de l'image saturée
		List<MatOfPoint> ListeContours= MaBibliothequeTraitementImageEtendue .ExtractContours(saturee);
		//System.out.println("nbr contour"+ListeContours.size()+"\n");
		int i=0;
		int k=0;
		int indice;
	
		double [] scores=new double [6];
		//Pour tous les contours de la liste
		for (MatOfPoint contour: ListeContours  ){
			i++;
			objetrond=MaBibliothequeTraitementImageEtendue.DetectForm(frame,contour);
			
			if (objetrond!=null && objetrond.cols()>6 && objetrond.rows()>6){
				//MaBibliothequeTraitementImageEtendue.afficheImage("Objet rond detécté", objetrond);
				indice=Interface.identifiepanneau(objetrond);
				//System.out.println("indice max"+indice+"\n");
				switch(indice){
				case -1:System.out.println("Aucun panneau détécté");break;
				case 0:{
						if (valeurprec!=30)
						  nbr30=0;
					      nbr30++;
					//System.out.println("nbr 30= "+nbr30+"\n");
					if (nbr30==5)
					{  nbr30=0;
						System.out.println("Panneau 30 détécté");}
					valeurprec=30;
					break;
					}
				
				case 1:
					{if (valeurprec!=50)
					nbr50=0;
						nbr50++;
					//System.out.println("nbr 50= "+nbr50+"\n");
					if (nbr50==5)
					{  nbr50=0;
						System.out.println("Panneau 50 détécté");}
					valeurprec=50;
					break;}
				case 2:
				{   if (valeurprec!=70)
					nbr70=0;
					nbr70++;
				//System.out.println("nbr 70= "+nbr70+"\n");
				if (nbr70==5)
				{  nbr70=0;
					System.out.println("Panneau 70 détécté");}
				valeurprec=70;
				break;}
				case 3:
					{if (valeurprec!=90)
						nbr90=0;
						nbr90++;
				//System.out.println("nbr 90= "+nbr90+"\n");
				if (nbr90==5)
				{  nbr90=0;
					System.out.println("Panneau 90 détécté");}
				valeurprec=90;
				break;}
				case 4:
				{ if (valeurprec!=110)
					nbr110=0;
					nbr110++;
				//System.out.println("nbr 110= "+nbr110+"\n");
				if (nbr110==5)
				{  nbr110=0;
					System.out.println("Panneau 110 détécté");}
				valeurprec=110;
				break;}
				case 5:
					{if (valeurprec!=1)
						nbrdep=0;
						nbrdep++;
				if (nbrdep==5)
				{  nbrdep=0;
					System.out.println("Panneau interdiction de dépasser détécté");}
				valeurprec=1;
				break;}
					
					
					
				}
				}	
				
				//System.out.println("object rond n "+k+"\n");
			}


			ImageIcon image = new ImageIcon(Interface.Mat2bufferedImage(frame));
			vidpanel.setIcon(image);
			vidpanel.repaint();
		}
	}





}