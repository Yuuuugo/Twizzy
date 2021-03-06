
package tesseract;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import Utilitaires.MaBibliothequeTraitementImage;
import Utilitaires.MaBibliothequeTraitementImageEtendue;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
  
public class ScannedImage {
    public static void main(String[] args)
    {
        Tesseract tesseract = new Tesseract();
        try {
  
            tesseract.setDatapath("D:/Tess4J/tessdata");
  
            // the path of your tess data folder
            // inside the extracted file
            String text = tesseract.doOCR(new File("test70.png"));
            //(new File("test70.png")
            // path of your image file
            System.out.print(text);
        }
        catch (TesseractException e) {
            e.printStackTrace();
        }
    }
    
    
    public static String reconnaissance(Mat Image) {
    	String tout = "";
    	int i = 80;
    	/*for(int i = 0;i<80;i=i+10) {*/
    	Mat Im = Preparation.transformation(Image,i);
    	Tesseract tesseract = new Tesseract();
    	String text = "";
    	 try {
    		  
             tesseract.setDatapath("D:/Tess4J/tessdata");
             // the path of your tess data folder
             // inside the extracted file
             BufferedImage Atester = Preparation.Mat2bufferedImage(Im);
            tout = text + tesseract.doOCR(Atester);
             // path of your image file
         }
         catch (TesseractException e) {
             e.printStackTrace();
         }
    	
   /* }*/
    	 return tout;
    }
    
}