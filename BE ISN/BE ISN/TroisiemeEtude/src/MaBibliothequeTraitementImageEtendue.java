
import java.awt.Dimension;


import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfInt4;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
//import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;

//import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;



import org.opencv.imgcodecs.Imgcodecs;

public class MaBibliothequeTraitementImageEtendue {
	//Contient toutes les méthodes necessaires à la transformation des images


	//Methode qui permet de transformer une matrice intialement au  format BGR au format HSV
	public static Mat transformeBGRversHSV(Mat matriceBGR){
		Mat matriceHSV=new Mat(matriceBGR.height(),matriceBGR.cols(),matriceBGR.type());
		Imgproc.cvtColor(matriceBGR,matriceHSV,Imgproc.COLOR_BGR2HSV);
		return matriceHSV;

	}

	//Methode qui convertit une matrice avec 3 canaux en un vecteur de 3 matrices monocanal (un canal par couleur)
	public static Vector<Mat> splitHSVChannels(Mat input) {
		Vector<Mat> channels = new Vector<Mat>(); 
		Core.split(input, channels);
		return channels;
	}

	//Methode qui permet d'afficher une image sur un panel
	public static void afficheImage(String title, Mat img){
		MatOfByte matOfByte=new MatOfByte();
		Imgcodecs.imencode(".png",img,matOfByte);
		byte[] byteArray=matOfByte.toArray();
		BufferedImage bufImage=null;
		try{
			InputStream in=new ByteArrayInputStream(byteArray);
			bufImage=ImageIO.read(in);
			JFrame frame=new JFrame();
			frame.setTitle(title);
			frame.getContentPane().add(new JLabel(new ImageIcon(bufImage)));
			frame.pack();
			frame.setVisible(true);

		}
		catch(Exception e){
			e.printStackTrace();
		}


	}

	//Methode qui permet de saturer les couleurs rouges à partir de 3 seuils
		public static Mat seuillage(Mat input, int seuilRougeOrange, int seuilRougeViolet,int seuilSaturation){
			//à completer
			// Decomposition en 3 cannaux HSV
			Vector<Mat> channels = splitHSVChannels(input);
			//création d'un seuil 
			Scalar rougeorange = new Scalar(seuilRougeOrange);
			Scalar rougeviolet = new Scalar(seuilRougeViolet);
			Scalar rouge_sat = new Scalar(seuilSaturation);
			//Création d'une matrice
			Mat rouges_orange=new Mat();
			Mat rouges_violet=new Mat();
			Mat rouges_sat=new Mat();
			Mat Image_sortie_rouge=new Mat();
			Mat Image_sortie=new Mat();
			
			
			//Comparaison et saturation des pixels dont la composante rouge est plus grande que le seuil rougeViolet
			Core.compare(channels.get(0), rougeorange, rouges_orange, Core.CMP_LT);
			Core.compare(channels.get(0), rougeviolet, rouges_violet, Core.CMP_GT);
			Core.compare(channels.get(1),rouge_sat , rouges_sat, Core.CMP_GT);
			Core.bitwise_or(rouges_orange,rouges_violet,Image_sortie_rouge );
			Core.bitwise_and(Image_sortie_rouge, rouges_sat, Image_sortie);
			
			

			//image saturée à retourner
			return  Image_sortie;



		}
	

	//Methode qui permet d'extraire les contours d'une image donnee
	public static List<MatOfPoint> ExtractContours(Mat input) {
		// Detecter les contours des formes trouvées
		int thresh = 100;
		Mat canny_output = new Mat();
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		MatOfInt4 hierarchy = new MatOfInt4();
		Imgproc.Canny( input, canny_output, thresh, thresh*2);


		/// Find extreme outer contours
		Imgproc.findContours( canny_output, contours, hierarchy,Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

		Mat drawing = Mat.zeros( canny_output.size(), CvType.CV_8UC3 );
		Random rand = new Random();
		for( int i = 0; i< contours.size(); i++ )
		{
			Scalar color = new Scalar( rand.nextInt(255 - 0 + 1) , rand.nextInt(255 - 0 + 1),rand.nextInt(255 - 0 + 1) );
			Imgproc.drawContours( drawing, contours, i, color, 1, 8, hierarchy, 0, new Point() );
		}
		//afficheImage("Contours",drawing);

		return contours;
	}

	//Methode qui permet de decouper et identifier les contours carrés, triangulaires ou rectangulaires. 
	//Renvoie null si aucun contour rond n'a été trouvé.	
	//Renvoie une matrice carrée englobant un contour rond si un contour rond a été trouvé
	public static Mat DetectForm(Mat img,MatOfPoint contour) {
		MatOfPoint2f matOfPoint2f = new MatOfPoint2f();
		MatOfPoint2f approxCurve = new MatOfPoint2f();
		float[] radius = new float[1];
		Point center = new Point();
		Rect rect = Imgproc.boundingRect(contour);
		double contourArea = Imgproc.contourArea(contour);


		matOfPoint2f.fromList(contour.toList());
		// Cherche le plus petit cercle entourant le contour
		Imgproc.minEnclosingCircle(matOfPoint2f, center, radius);
		//System.out.println(contourArea+" "+Math.PI*radius[0]*radius[0]);
		//on dit que c'est un cercle si l'aire occupé par le contour est à supérieure à  80% de l'aire occupée par un cercle parfait
		if ((contourArea / (Math.PI*radius[0]*radius[0])) >=0.8) {
			//System.out.println("Cercle");
			Imgproc.circle(img, center, (int)radius[0], new Scalar(127, 0, 0), 2);
			Imgproc.rectangle(img, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height), new Scalar (0, 127, 0), 2);
			Mat tmp = img.submat(rect.y,rect.y+rect.height,rect.x,rect.x+rect.width);
			Mat sign = Mat.zeros(tmp.size(),tmp.type());
			tmp.copyTo(sign);
			return sign;
		}else {

			Imgproc.approxPolyDP(matOfPoint2f, approxCurve, Imgproc.arcLength(matOfPoint2f, true) * 0.02, true);
			long total = approxCurve.total();
			if (total == 3 ) { // is triangle
				//System.out.println("Triangle");
				Point [] pt = approxCurve.toArray();
				Imgproc.line(img, pt[0], pt[1], new Scalar(255,0,0),2);
				Imgproc.line(img, pt[1], pt[2], new Scalar(255,0,0),2);
				Imgproc.line(img, pt[2], pt[0], new Scalar(255,0,0),2);
				Imgproc.rectangle(img, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height), new Scalar (0, 255, 0), 2);
				Mat tmp = img.submat(rect.y,rect.y+rect.height,rect.x,rect.x+rect.width);
				Mat sign = Mat.zeros(tmp.size(),tmp.type());
				tmp.copyTo(sign);
				return null;
			}
			if (total >= 4 && total <= 6) {
				List<Double> cos = new ArrayList<>();
				Point[] points = approxCurve.toArray();
				for (int j = 2; j < total + 1; j++) {
					cos.add(angle(points[(int) (j % total)], points[j - 2], points[j - 1]));
				}
				Collections.sort(cos);
				Double minCos = cos.get(0);
				Double maxCos = cos.get(cos.size() - 1);
				boolean isRect = total == 4 && minCos >= -0.1 && maxCos <= 0.3;
				boolean isPolygon = (total == 5 && minCos >= -0.34 && maxCos <= -0.27) || (total == 6 && minCos >= -0.55 && maxCos <= -0.45);
				if (isRect) {
					double ratio = Math.abs(1 - (double) rect.width / rect.height);
					//drawText(rect.tl(), ratio <= 0.02 ? "SQU" : "RECT");
					//System.out.println("Rectangle");
					Imgproc.rectangle(img, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height), new Scalar (0, 255, 0), 2);
					Mat tmp = img.submat(rect.y,rect.y+rect.height,rect.x,rect.x+rect.width);
					Mat sign = Mat.zeros(tmp.size(),tmp.type());
					tmp.copyTo(sign);
					return null;
				}
				if (isPolygon) {
					//System.out.println("Polygon");
					//drawText(rect.tl(), "Polygon");
				}
			}			
		}
		return null;

	}

	

	public static double angle(Point a, Point b, Point c) {
		Point ab = new Point( b.x - a.x, b.y - a.y );
		Point cb = new Point( b.x - c.x, b.y - c.y );
		double dot = (ab.x * cb.x + ab.y * cb.y); // dot product
		double cross = (ab.x * cb.y - ab.y * cb.x); // cross product
		double alpha = Math.atan2(cross, dot);
		return Math.floor(alpha * 180. / Math.PI + 0.5);
	}

	public static Mat seuillageBW(Mat input, int seuilNoirBlanc){
		//création d'un seuil 
		Scalar noirblanc = new Scalar(seuilNoirBlanc);
		//Création de l'image d'arrivée en noir et blanc
		Mat noir_blanc=new Mat();
		
		
		//Comparaison et saturation des pixels dont la luminosité est plus grande que le seuil noirblanc
		Core.compare(input, noirblanc, noir_blanc, Core.CMP_GT);
		
		
		
		//image saturée à retourner
		return  noir_blanc;



	}
	public static double SimilitudeBWratio(Mat object,String signfile) {
		// Conversion du signe de reference en niveaux de gris et normalisation
		Mat panneauref = Imgcodecs.imread(signfile);	float somme=0;
		//int n=336;
				
		float moyenne=0;;
		Mat graySign = new Mat(panneauref.rows(), panneauref.cols(), panneauref.type());
		Imgproc.cvtColor(panneauref, graySign, Imgproc.COLOR_BGRA2GRAY);
		Mat signeNoirEtBlanc=new Mat();
				


		//normalisation du panneau extrait de l'image et redimmensionnement à la taille du panneau de référence
		Mat sObject=new Mat();
		Imgproc.resize(object, sObject,panneauref.size() );
		Core.normalize(sObject, sObject, 0, 255, Core.NORM_MINMAX);
		Vector<Mat> channels = new Vector<Mat>(); 
		
		//Seuillage du panneau extrait pour supprimer la bordure rouge
		Core.split(sObject, channels);
		Mat tmp1 = channels.get(1);
		channels.remove(1);
		sObject=transformeBGRversHSV(sObject);
		Mat tmp3= seuillage(sObject, 6, 170, 72);
		
		//suppression de toute l'image sauf la zone de texte et préparation aux seuillages de noirs et de blancs
		Mat tmp4 = new Mat();
		Imgproc.threshold(tmp3, tmp4, 90, 255, 0);
		Core.bitwise_not(tmp4,tmp4);
		Mat tmp5 = Mat.zeros(tmp4.size(),tmp4.type());
		Imgproc.circle(tmp5, new Point(tmp5.width()/2, tmp5.height()/2), (int)(tmp5.width()*(8.0/20.0)), new Scalar(255, 255, 255), -1);
		Core.bitwise_and(tmp5,tmp4,tmp4);
		Core.bitwise_not(tmp4,tmp5);
		Imgproc.threshold(tmp5, tmp5, 127, 127, 0);
		Core.bitwise_and(tmp4, tmp1, tmp1);
		Core.add(tmp5, tmp1, tmp1);
		
		Mat grayObject=tmp1;
		
		//Comptage des pixels noirs et des pixels blancs et calcul des ratios
		Vector<Mat> Blackandwhite = new Vector<Mat>();
		Blackandwhite.add(seuillageBW(grayObject,125));
		Blackandwhite.add(seuillageBW(grayObject,127));
		double TotalNumberOfPixels = graySign.rows() * graySign.cols();
		double nbblackref= (TotalNumberOfPixels-Core.countNonZero(seuillageBW(graySign,56)));
		double nbwhiteref=Core.countNonZero(seuillageBW(graySign,170));
		TotalNumberOfPixels = grayObject.rows() * grayObject.cols();
		double nbblack= (TotalNumberOfPixels-Core.countNonZero(Blackandwhite.get(0)));
		double nbwhite=Core.countNonZero(Blackandwhite.get(1));
		double ratioref = nbblackref/nbwhiteref;
		double ratio = nbblack/nbwhite;
		
		//Mat affiche = new Mat();
		//Core.hconcat(Blackandwhite, affiche);
		//afficheImage("détection pixels noirs vs pixels blancs du panneau", affiche);
		
		return Math.abs(ratioref-ratio);
	}
	
	//methode à completer
	public static double Similitude(Mat object,String signfile) {
		

		// Conversion du signe de reference en niveaux de gris et normalisation
		Mat panneauref = Imgcodecs.imread(signfile);	float somme=0;
		//int n=336;
		
		float moyenne=0;;
		Mat graySign = new Mat(panneauref.rows(), panneauref.cols(), panneauref.type());
		Imgproc.cvtColor(panneauref, graySign, Imgproc.COLOR_BGRA2GRAY);
		Core.normalize(graySign, graySign, 0, 255, Core.NORM_MINMAX);
		Mat signeNoirEtBlanc=new Mat();
		


		//Conversion du panneau extrait de l'image en gris et normalisation et redimensionnement à la taille du panneau de réference
		Mat sObject=new Mat();
		Imgproc.resize(object, sObject,panneauref.size() );
		Mat grayObject = new Mat(panneauref.rows(), panneauref.cols(), panneauref.type());
		Imgproc.resize(object, object, graySign.size());
		//afficheImage("Panneau extrait de l'image",object);
		Imgproc.cvtColor(object, grayObject, Imgproc.COLOR_BGRA2GRAY);
		Core.normalize(grayObject, grayObject, 0, 255, Core.NORM_MINMAX);
		//Imgproc.resize(grayObject, grayObject, graySign.size());	
		
		
		
		//à compléter...
		FeatureDetector orbDetector =FeatureDetector.create(FeatureDetector.ORB);
		DescriptorExtractor orbExtractor =DescriptorExtractor.create(DescriptorExtractor.ORB);
		MatOfKeyPoint objectKeypoints =new MatOfKeyPoint();
		orbDetector.detect(grayObject , objectKeypoints);
		
		MatOfKeyPoint signKeypoints =new MatOfKeyPoint();
		orbDetector.detect(graySign , signKeypoints);
		
		Mat objectDescriptor =new Mat (object.rows(),object.cols(),object.type());
		orbExtractor.compute(grayObject, objectKeypoints,  objectDescriptor);
		
		Mat signDescriptor =new Mat (panneauref.rows(),panneauref.cols(),panneauref.type());
		orbExtractor.compute(graySign, signKeypoints,  signDescriptor);
		
		//Matching
		MatOfDMatch matchs =new MatOfDMatch();
		DescriptorMatcher matcher =DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE);
		matcher.match(objectDescriptor, signDescriptor,matchs);
		//System.out.println(matchs.dump());
		Mat matchedImage =new Mat(panneauref.rows(),panneauref.cols()*2,panneauref.type());
		Features2d.drawMatches(sObject, objectKeypoints,panneauref,signKeypoints,matchs,matchedImage); 
		//afficheImage("matched",matchedImage );
		List<org.opencv.core.DMatch> l =matchs.toList();
		
		
		for(int i=0;i<l.size();i++)
		{  org.opencv.core.DMatch dmatch=l.get(i);
		   somme=somme+dmatch.distance;
		
		
		}
		moyenne=somme/l.size();
		//System.out.println(moyenne);
		//System.out.println(contours.size());
	
		return moyenne;}
	
	public static BufferedImage Mat2bufferedImage(Mat image) {
		MatOfByte bytemat = new MatOfByte();
		Imgcodecs.imencode(".jpg", image, bytemat);
		byte[] bytes = bytemat.toArray();
		InputStream in = new ByteArrayInputStream(bytes);
		BufferedImage img = null;
		try {
			img = ImageIO.read(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return img;
	}


	
	public static int identifiepanneau(Mat objetrond, float ratioBWMatching){
		double [] scores=new double [6];
		double [] scoreso=new double [6];
		int indexmax=-1;
		
		//Application de la méthode des ratios de noirs et de blancs et addition des scores avec la méthode de matching	
		double scoremin=Integer.MAX_VALUE;
		//System.out.println("scores Black and white + matching");
		scores[0]=MaBibliothequeTraitementImageEtendue.SimilitudeBWratio(objetrond,"ref30bw.jpg")+MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"ref30.jpg")/(200*ratioBWMatching);;
		//System.out.println(scores[0]);
		scores[1]=MaBibliothequeTraitementImageEtendue.SimilitudeBWratio(objetrond,"ref50bw.jpg")+MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"ref50.jpg")/(200*ratioBWMatching);
		//System.out.println(scores[1]);
		scores[2]=MaBibliothequeTraitementImageEtendue.SimilitudeBWratio(objetrond,"ref70bw.jpg")+MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"ref70.jpg")/(200*ratioBWMatching);
		//System.out.println(scores[2]);
		scores[3]=MaBibliothequeTraitementImageEtendue.SimilitudeBWratio(objetrond,"ref90bw.jpg")+MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"ref90.jpg")/(200*ratioBWMatching);
		//System.out.println(scores[3]);
		scores[4]=MaBibliothequeTraitementImageEtendue.SimilitudeBWratio(objetrond,"ref110bw.jpg")+MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"ref110.jpg")/(200*ratioBWMatching);
		//System.out.println(scores[4]);
		scores[5]=MaBibliothequeTraitementImageEtendue.SimilitudeBWratio(objetrond,"refdoublebw.jpg")+MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"refdouble.jpg")/(200*ratioBWMatching);
		//System.out.println(scores[5]);
		
	

		for(int j=0;j<scores.length;j++){
			if (scores[j]<scoremin){scoremin=scores[j];indexmax=j;
			
			}
			//System.out.println("le score de la case"+j+"="+scores[j]+"\n");
			}
		//System.out.println(scoremin);
		return indexmax;
	}
	public static  int identifiepanneau(Mat objetrond) {
		float defaultratio = (float) 0.5;
		return identifiepanneau(objetrond, defaultratio);
	}
	
	public static ArrayList<String> etu_pan(String fichier) {
		//Ouverture le l'image et saturation des rouges
				System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
				Mat m=Imgcodecs.imread(fichier);
				//MaBibliothequeTraitementImageEtendue.afficheImage("Image testée", m);
				Mat transformee=MaBibliothequeTraitementImageEtendue.transformeBGRversHSV(m);
				//la methode seuillage est ici extraite de l'archivage jar du meme nom 
				Mat saturee=MaBibliothequeTraitementImageEtendue.seuillage(transformee, 6, 170, 110);
				Mat objetrond = null;

				//Création d'une liste des contours à partir de l'image saturée
				List<MatOfPoint> ListeContours= MaBibliothequeTraitementImageEtendue .ExtractContours(saturee);
				int i=0;
				int k=0;
				double [] scores=new double [6];
				//Pour tous les contours de la liste
				ArrayList<String> panneaux = new ArrayList<String>();
				
				for (MatOfPoint contour: ListeContours  ){
					i++;
					objetrond=MaBibliothequeTraitementImageEtendue.DetectForm(m,contour);
					
					if (objetrond!=null){
						
						k++;
						if (k==2)
						MaBibliothequeTraitementImageEtendue.afficheImage("Objet rond detécté", objetrond);
						scores[0]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"ref30.jpg");
						scores[1]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"ref50.jpg");
						scores[2]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"ref70.jpg");
						scores[3]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"ref90.jpg");
						scores[4]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"ref110.jpg");
						scores[5]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"refdouble.jpg");
						/*scores[6]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"ref10.jpg");
						scores[7]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"ref20.jpg");
						scores[8]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"ref40.jpg");
						scores[9]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"ref80.jpg");
						
						scores[10]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"ref130.jpg");
					*/	//recherche de l'index du maximum et affichage du panneau detecté
						double scoremax=Integer.MAX_VALUE;
						int indexmax=-1;
						for(int j=0;j<scores.length;j++){
							if (scores[j]<scoremax){scoremax=scores[j];indexmax=j;}}	
						System.out.println(scoremax);
						if(scoremax<0){System.out.println("Aucun Panneau détécté");}
						else{switch(indexmax){
						case -1:;break;
						case 0:System.out.println("Panneau 30 détécté");
					
						panneaux.add("30");
						break;
						case 1:System.out.println("Panneau 50 détécté");
						panneaux.add("50");
						break;
						case 2:System.out.println("Panneau 70 détécté");
						panneaux.add("70");
						break;
						case 3:System.out.println("Panneau 90 détécté");
						panneaux.add("90");
						break;
						case 4:System.out.println("Panneau 110 détécté");
						panneaux.add("110");
						break;
						case 5:System.out.println("Panneau interdiction de dépasser détécté");
						panneaux.add("intdep");
						break;
						/*case 6:System.out.println("Panneau 10 détécté");
						panneaux.add("10");
						break;
						case 7:System.out.println("Panneau 20 détécté");
						panneaux.add("20");
						break;
						case 8:System.out.println("Panneau 40 détécté");
						panneaux.add("40");
						break;
						case 9:System.out.println("Panneau 80 détécté");
						panneaux.add("80");
						break;
					
						case 10:System.out.println("Panneau 130 détécté");
						panneaux.add("130");
						break;*/
						}}
						System.out.println("object rond n "+k+"\n");

					}
				}
				

		return panneaux;
		}


}
