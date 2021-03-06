package tesseract;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgcodecs.Imgcodecs;

import Utilitaires.MaBibliothequeTraitementImage;

public class Preparation {
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	
		
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
		public static Mat seuillage_V(Mat input, int seuilRougeViolet){
			// Decomposition en 3 cannaux HSV
			Vector<Mat> channels = MaBibliothequeTraitementImage.splitHSVChannels(input);
			//cr?ation d'un seuil 
			Scalar rougeviolet = new Scalar(seuilRougeViolet);
			//Cr?ation d'une matrice
			Mat rouges=new Mat();
			//Comparaison et saturation des pixels dont la composante rouge est plus grande que le seuil rougeViolet
			Core.compare(channels.get(2), rougeviolet, rouges, Core.CMP_GT);
			//image satur?e ? retourner
			return rouges;

		}
		
		 public static Mat transformation(Mat imageOriginale,int seuil)
		    {
		//Mat imageOriginale=Highgui.imread("p1.jpg",Highgui.CV_LOAD_IMAGE_COLOR);
		Mat imageTransformee=MaBibliothequeTraitementImage.transformeBGRversHSV(imageOriginale);
		Mat Image = seuillage_V(imageTransformee, seuil);
		/*for (int i = 0;i<Image.height();i++){
			for (int j = 0;j<Image.width();j++){
				if(i<30 || i>130||j<30||j>130){
					Image.put(i,j,255);
				}

			}
		}*/
		return Image;
		    }
		 

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
}
