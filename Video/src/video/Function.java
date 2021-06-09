package video;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;


import org.opencv.core.Core;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.highgui.Highgui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import Utilitaires.MaBibliothequeTraitementImageEtendue;

public class Function {


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

	public static int identifiepanneau(Mat objetrond){
		double [] scores=new double [6];
		int indexmax=-1;
		if (objetrond!=null){
			scores[0]=Similitude(objetrond,"ref30.jpg");
			scores[1]=Similitude(objetrond,"ref50.jpg");
			scores[2]=Similitude(objetrond,"ref70.jpg");
			scores[3]= Similitude(objetrond,"ref90.jpg");
			scores[4]=Similitude(objetrond,"ref110.jpg");
			scores[5]=Similitude(objetrond,"refdouble.jpg");

			double scoremax=Double.POSITIVE_INFINITY;

			for(int j=0;j<scores.length;j++){
				if (scores[j]<scoremax){
					scoremax=scores[j];
					indexmax=j;}}	


		}
		return indexmax;
	}


	public static double angle(Point a, Point b, Point c) {
		Point ab = new Point( b.x - a.x, b.y - a.y );
		Point cb = new Point( b.x - c.x, b.y - c.y );
		double dot = (ab.x * cb.x + ab.y * cb.y); // dot product
		double cross = (ab.x * cb.y - ab.y * cb.x); // cross product
		double alpha = Math.atan2(cross, dot);
		return Math.floor(alpha * 180. / Math.PI + 0.5);
	}

	public  static Mat DetectForm(Mat img,MatOfPoint contour) {
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
			Imgproc.circle(img, center, (int)radius[0], new Scalar(255, 0, 0), 2);
			Imgproc.rectangle(img, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height), new Scalar (0, 255, 0), 2);
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
public static double Similitude(Mat object,String signfile) {
		

		// Conversion du signe de reference en niveaux de gris et normalisation
		//Mat panneauref = Imgcodecs.imread(signfile);
		Mat panneauref =Imgcodecs.imread(signfile);
		float somme=0;
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
		//List<org.opencv.core.DMatch> l =matchs.toList();
		List<DMatch>  l = matchs.toList();
		
		
		for(int i=0;i<l.size();i++)
		{ DMatch dmatch= l.get(i);
		   somme=somme+dmatch.distance;
		
		
		}
		moyenne=somme/l.size();
		//System.out.println(moyenne);
		//System.out.println(contours.size());
	
		return moyenne;}

	

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
}