package Utilitaire;
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

public class AnalyseVideo {
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	static Mat imag = null;
	static boolean Trigger = false;

	public static void main(String[] args) {
		JFrame jframe = new JFrame("Detection de panneaux sur un flux vidéo");
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JLabel vidpanel = new JLabel();
		jframe.setContentPane(vidpanel);
		jframe.setSize(720, 480);
		jframe.setVisible(true);

		Mat frame = new Mat();
		VideoCapture camera = new VideoCapture("video1.avi");
		Mat PanneauAAnalyser = null;

			while (camera.read(frame)) {
				if (Trigger==true) {
					for (int j=0;j<40;j++) {
						camera.read(frame);
					}
				}
				Trigger = false;
				ImageIcon image = new ImageIcon(Mat2bufferedImage(frame));
				vidpanel.setIcon(image);
				Mat trans=MaBibliothequeTraitementImageEtendue.transformeBGRversHSV(frame);
				Mat saturee=MaBibliothequeTraitementImage.seuillage(trans, 6, 170, 110);
				Mat objetrond = null;

				//Création d'une liste des contours 
				List<MatOfPoint> ListeContours= MaBibliothequeTraitementImageEtendue.ExtractContours(saturee);
				//Détection des objets
				for (MatOfPoint contour: ListeContours  ){
					objetrond=MaBibliothequeTraitementImageEtendue.DetectForm(frame,contour);
					int indexmax=identifiepanneau(objetrond);
					switch(indexmax){
					case -1:;break;
					case 0:System.out.println("Panneau 30 détécté");break;
					case 1:System.out.println("Panneau 50 détécté");break;
					case 2:System.out.println("Panneau 70 détécté");break;
					case 3:System.out.println("Panneau 90 détécté");break;
					case 4:System.out.println("Panneau 110 détécté");break;
					case 5:System.out.println("Panneau interdiction de dépasser détécté");break;
					}
					if (indexmax>=0) {
						Trigger=true;
					}
				}
			vidpanel.repaint();
		}
	}






	public static BufferedImage Mat2bufferedImage(Mat image) {
		MatOfByte bytemat = new MatOfByte();
		Highgui.imencode(".jpg", image, bytemat);
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
			scores[0]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"ref30.jpg");
			scores[1]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"ref50.jpg");
			scores[2]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"ref70.jpg");
			scores[3]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"ref90.jpg");
			scores[4]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"ref110.jpg");
			scores[5]=MaBibliothequeTraitementImageEtendue.Similitude(objetrond,"refdouble.jpg");

			double scoremax=scores[0];

			for(int j=1;j<scores.length;j++){
				if (scores[j]<scoremax){scoremax=scores[j];indexmax=j;}}	


		}
		return indexmax;
	}


}