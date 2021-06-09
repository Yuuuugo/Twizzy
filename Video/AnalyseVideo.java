
package video;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
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
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
//import org.opencv.highgui.Highgui;
//import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import java.awt.image.BufferedImage;

import Utilitaires.MaBibliothequeTraitementImage;
import Utilitaires.MaBibliothequeTraitementImageEtendue;

import org.opencv.imgcodecs.Imgcodecs;
//import org.opencv.videoio.VideoCapture;
public class AnalyseVideo {
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	static Mat imag = null;
	static boolean Trigger = false;

	public static void main(String[] args) {
		JFrame jframe = new JFrame("Detection de panneaux sur un flux vid�o");
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
					for (int j=0;j<30;j++) {
						camera.read(frame);
					}
				}
				Trigger = false;
				ImageIcon image = new ImageIcon(Function.Mat2bufferedImage(frame));
				vidpanel.setIcon(image);
				Mat trans=MaBibliothequeTraitementImageEtendue.transformeBGRversHSV(frame);
				Mat saturee=MaBibliothequeTraitementImage.seuillage(trans, 6, 170, 110);
				Mat objetrond = null;

				//Cr�ation d'une liste des contours 
				List<MatOfPoint> ListeContours= MaBibliothequeTraitementImageEtendue.ExtractContours(saturee);
				//D�tection des objets
				for (MatOfPoint contour: ListeContours  ){
					
					objetrond=Function.DetectForm(frame,contour);
					int indexmax=Function.identifiepanneau(objetrond);
					if (objetrond!=null && objetrond.cols()>6 && objetrond.rows()>6){
					switch(indexmax){
					case -1:;break;
					case 0:
						System.out.println("Panneau 30 d�t�ct�");
						Function.afficheImage("Panneau detecte",objetrond);
					break;
					case 1:
						System.out.println("Panneau 50 d�t�ct�");
						Function.afficheImage("Panneau detecte",objetrond);
					break;
					case 2:
						System.out.println("Panneau 70 d�t�ct�");
						Function.afficheImage("Panneau detecte",objetrond);
					break;
					case 3:
						System.out.println("Panneau 90 d�t�ct�");
						Function.afficheImage("Panneau detecte",objetrond);
					break;
					case 4:
						System.out.println("Panneau 110 d�t�ct�");
						Function.afficheImage("Panneau detecte",objetrond);
					break;
					case 5:
						System.out.println("Panneau interdiction de d�passer d�t�ct�");
						Function.afficheImage("Panneau detecte",objetrond);
					break;
					}
					if (indexmax>=0) {
						Trigger=true;
					}
				}
				}
			vidpanel.repaint();
		}
	}






}