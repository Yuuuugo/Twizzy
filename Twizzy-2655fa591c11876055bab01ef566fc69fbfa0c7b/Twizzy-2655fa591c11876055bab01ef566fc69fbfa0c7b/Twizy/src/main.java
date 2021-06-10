import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfInt4;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.opencv.core.Point;
import org.opencv.core.Rect;

import java.awt.image.BufferedImage;

public class main {

	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat mat = LectureImage("images/PanneauRoute/panneau_110_2.jpg");
		Vector<Mat> channels = decompHSV(mat);
		ImShow("HSV_1",channels.get(0));
		ImShow("HSV_2",channels.get(1));
		ImShow("HSV_3",channels.get(2));
		Scalar rouge_min = new Scalar(0,100,100);
		Scalar rouge_max =new Scalar(10,255,255);
		Scalar rouge_min_2 =new Scalar(160,100,100);
		Scalar rouge_max_2 =new Scalar(179,255,255);
		
		//Mat threshold_img = Seuils(mat,rouge_min, rouge_max, rouge_min_2,rouge_max_2);
		//ImShow("Cercles rouges",threshold_img);
		
		//List<MatOfPoint> contours = DetecterContours_Seuil(mat,rouge_min, rouge_max, rouge_min_2,rouge_max_2);
		List<Mat> panneaux = DetecterPanneau_Seuil(mat,rouge_min, rouge_max, rouge_min_2,rouge_max_2);
		Mat panneau = panneaux.get(0);
		Mat panneau_test = Highgui.imread("images/EchantillonPanneau/panneau_110.jpg");
		Mat panneau_resize = MiseEchelle(panneau,panneau_test);
		//ImShow("panneau_test",panneau_test);
		//ImShow("resize",panneau_resize);
		match(panneau_resize,panneau_test);
		
	}
	
	public static Vector<Mat> decompHSV (Mat mat) {
	Mat output = Mat.zeros(mat.size(),  mat.type());
	Imgproc.cvtColor(mat,  output,  Imgproc.COLOR_BGR2HSV);
	ImShow("HSV",output);
	Vector<Mat> channels = new Vector<Mat>();
	Core.split(output,  channels);;
	return channels ;
	}

	public static void ImShow(String title, Mat img) {
		/* Affiche une image a partir d'une matrice la representant */

		MatOfByte matOfByte = new MatOfByte();
		Highgui.imencode(".png", img, matOfByte);
		byte[] byteArray = matOfByte.toArray();
		BufferedImage bufImage = null;
		try {
			InputStream in = new ByteArrayInputStream(byteArray);
			bufImage = ImageIO.read(in);
			JFrame frame = new JFrame();
			frame.setTitle(title);
			frame.getContentPane().add(new JLabel(new ImageIcon(bufImage)));
			frame.pack();
			frame.setVisible(true);			
		} catch (Exception e){
			e.printStackTrace();
		}						
	}
	
	public static Mat LectureImage(String fichier) {
		/* Ouvre une image et retourne une matrice (representant l'image en rgb) */

		File f = new File(fichier);
		Mat m = Highgui.imread(f.getAbsolutePath());
		return m;
	}
	
	public static Mat Seuils(Mat mat, Scalar min, Scalar max, Scalar min_2, Scalar max_2) {
		Mat hsv_image = Mat.zeros(mat.size(), mat.type());
		Imgproc.cvtColor(mat, hsv_image, Imgproc.COLOR_BGR2HSV);
		Mat threshold_img1 = new Mat();
		Mat threshold_img2 = new Mat();
		Mat threshold_img = new Mat();
		Core.inRange(hsv_image, min, max, threshold_img1);
		Core.inRange(hsv_image,min_2, max_2, threshold_img2);
		Core.bitwise_or(threshold_img1,  threshold_img2, threshold_img);
		Imgproc.GaussianBlur(threshold_img,  threshold_img,  new Size(9,9), 2,2);
		return(threshold_img);
		
	}
	
	public static List<MatOfPoint> DetecterContours_Seuil (Mat mat, Scalar min, Scalar max, Scalar min_2, Scalar max_2) {
		ImShow("Cercles", mat);
		Mat threshold_img = Seuils(mat, min, max, min_2, max_2);
		ImShow("Seuillage", threshold_img);
		int thresh = 100;
		Mat canny_output = new Mat();
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		MatOfInt4 hierarchy = new MatOfInt4();
		Imgproc.Canny(threshold_img, canny_output, thresh, thresh*2);
		Imgproc.findContours(canny_output, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		Mat drawing = Mat.zeros(canny_output.size(), CvType.CV_8UC3);
		Random rand = new Random();
		for (int i = 0; i<contours.size();i++) {
			Scalar color = new Scalar(rand.nextInt(255 - 0 + 1), rand.nextInt(255 - 0 + 1), rand.nextInt(255 - 0 + 1));
			Imgproc.drawContours(drawing, contours,i,color,1,8,hierarchy,0,new Point()); //pas pareil que dans les slides : Imgproc.drawContours(drawing, contours,i,color)  
		}
		ImShow("Contours",drawing);
		return (contours);
	}
	
public static List<Mat> DetecterPanneau_Seuil(Mat mat, Scalar min, Scalar max, Scalar min_2, Scalar max_2) {
		
		List<MatOfPoint> contours = DetecterContours_Seuil(mat, min, max, min_2, max_2);
		MatOfPoint2f matOfPoint2f = new MatOfPoint2f();
		float[] radius = new float[1];
		Point center = new Point();
		List<Mat> panneaux = new ArrayList<Mat>();
		for (int c=0; c < contours.size();c++) {
			MatOfPoint contour = contours.get(c);
			double contourArea = Imgproc.contourArea(contour);
			matOfPoint2f.fromList(contour.toList());
			Imgproc.minEnclosingCircle(matOfPoint2f, center,radius);
			if ((contourArea/(Math.PI*radius[0]*radius[0])) >=0.8){
				Core.circle(matOfPoint2f,  center ,  (int)radius[0], new Scalar(0,255,0),2);
				Rect rect = Imgproc.boundingRect(contour);
				Core.rectangle(mat, new Point(rect.x,rect.y),
						new Point(rect.x+rect.width,rect.y+rect.height),
						new Scalar(0,255,0),2);
				Mat tmp = mat.submat(rect.y,rect.y+rect.height,rect.x,rect.x+rect.width);
				Mat panneau = Mat.zeros(tmp.size(), tmp.type());
				tmp.copyTo(panneau);
				panneaux.add(panneau);
				ImShow("panneau",panneau);
			}
		}
		return(panneaux);
	}

public static Mat MiseEchelle(Mat mat,Mat panneau_test) {
	Mat sObject = new Mat();
	Imgproc.resize(mat, sObject, panneau_test.size());
	return sObject;
	}

public static void match(Mat panneau,Mat panneau_test) {
	//On met les deux images en gris//
	Mat grayPanneau = new Mat(panneau.rows(), panneau.cols(), panneau.type());
	Imgproc.cvtColor(panneau, grayPanneau, Imgproc.COLOR_BGRA2GRAY);
	Core.normalize(grayPanneau, grayPanneau, 0, 255, Core.NORM_MINMAX);
	
	Mat gray_panneau_test = new Mat(panneau_test.rows(),panneau_test.cols(),panneau_test.type());
	Imgproc.cvtColor(panneau_test, gray_panneau_test, Imgproc.COLOR_BGRA2GRAY);
	Core.normalize(gray_panneau_test, gray_panneau_test,0,255,Core.NORM_MINMAX);
	
	//On decrit les descripteurs et keypoins
	FeatureDetector orbDetector = FeatureDetector.create(FeatureDetector.ORB);
	DescriptorExtractor orbExtractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
	
	//On fait les keypoints
	MatOfKeyPoint panneauKeypoints = new MatOfKeyPoint();
	orbDetector.detect(grayPanneau, panneauKeypoints);
	
	MatOfKeyPoint panneauTestKeypoints = new MatOfKeyPoint();
	orbDetector.detect(gray_panneau_test, panneauTestKeypoints);
	
	//On fait les descripteurs
	Mat panneauDescriptor = new Mat(panneau.rows(), panneau.cols(),panneau.type());
	orbExtractor.compute(grayPanneau,panneauKeypoints, panneauDescriptor);
	
	Mat panneauTestDescriptor = new Mat(panneau_test.rows(), panneau_test.cols(),panneau_test.type());
	orbExtractor.compute(gray_panneau_test,panneauTestKeypoints, panneauTestDescriptor);
	
	//On fait le matching
	MatOfDMatch matchs = new MatOfDMatch();
	DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
	matcher.match(panneauDescriptor,panneauTestDescriptor, matchs);
	System.out.println(matchs.dump());
	Mat matchedImage = new Mat(panneau_test.rows(),panneau_test.cols()*2,panneau_test.type());
	Features2d.drawMatches(panneau,panneauKeypoints,panneau_test,panneauTestKeypoints,matchs,matchedImage);
	ImShow("match",matchedImage);
	}

}
