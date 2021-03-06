package video;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import Utilitaires.MaBibliothequeTraitementImage;
import Utilitaires.MaBibliothequeTraitementImageEtendue;
import tesseract.Preparation;
import tesseract.ScannedImage;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JTextArea;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Toolkit;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import java.awt.image.BufferedImage;

public class Interface extends JFrame {

	static boolean detecte = false;
	private JPanel contentPane;
	private JTextField imgFile;
	private JTextArea panneau;
	private JTextArea panneau_2;
	private JTextArea panneau_3;
	private JPanel panel_0;
	private JPanel panel_1 ;
	private static JPanel panel_2 ;
	private static JPanel panel_3;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Interface frame = new Interface();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Interface() throws IOException {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(50, 1, 1470, 700);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		contentPane.setBackground(Color.orange);
		JPanel panel = new JPanel();
		panel.setBounds(10, 10, 1430, 639);
		contentPane.add(panel);
		panel.setLayout(null);
		

		
		BufferedImage myPicture = ImageIO.read(new File("Logo_ensem.png"));
		JLabel picLabel = new JLabel(new ImageIcon(myPicture));
		picLabel.setBounds(10, 490, 254, 138);
		panel.add(picLabel);


		panel_1 = new JPanel();
		panel_1.setBackground(Color.lightGray);
		panel_1.setBounds(274, 46, 880, 582); //emplacement image analys?e
		panel.add(panel_1);
		
		panel_2 = new JPanel();
		panel_2.setBackground(Color.lightGray);
		panel_2.setBounds(10, 200, 254, 254); //emplacement panneau detecte methode1
		panel.add(panel_2);
		
		panel_3 = new JPanel();
		panel_3.setBackground(Color.lightGray);
		panel_3.setBounds(1165, 45, 254, 254); //emplacement panneau d?tect? m?thode 2
		panel.add(panel_3);
		
		panel_0 = new JPanel();
		panel_0.setBackground(Color.lightGray);
		panel_0.setBounds(1165, 352, 254, 254); //emplacement panneau d?tect? m?thode 3
		panel.add(panel_0);

		

		
		JButton btnChargerImage = new JButton("Charger l'image s?lectionn?e");
		btnChargerImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
				Mat m=Imgcodecs.imread(imgFile.getText());
				panel_1.removeAll();
				panel_1.repaint();
				panel_1.add(new JLabel(new ImageIcon(Function.Mat2bufferedImage(m))));
				validate();
			}
		});

		btnChargerImage.setBounds(10, 40, 200, 20);
		panel.add(btnChargerImage);

		imgFile = new JTextField();
		imgFile.setBounds(10, 10, 259, 20);
		panel.add(imgFile);

		JButton btnNiveauGris = new JButton("Niveaux de Gris");
		btnNiveauGris.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
				Mat m=Imgcodecs.imread(imgFile.getText());
				//Conversion du panneau extrait de l'image en gris et normalisation et redimensionnement ? la taille du panneau de r?ference
				Mat grayObject = new Mat(m.rows(), m.cols(), m.type());
				Imgproc.cvtColor(m, grayObject, Imgproc.COLOR_BGRA2GRAY);
				panel_1.removeAll();
				panel_1.repaint();
				panel_1.add(new JLabel(new ImageIcon(Function.Mat2bufferedImage(grayObject))));
				validate();
			}
		});
		btnNiveauGris.setBounds(274, 10, 150, 20);
		panel.add(btnNiveauGris);

		JButton btnButtonHSV = new JButton("Domaine HSV");
		btnButtonHSV.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

				Mat imageOriginale=Imgcodecs.imread(imgFile.getText());
				Mat imageTransformee=MaBibliothequeTraitementImage.transformeBGRversHSV(imageOriginale);
				//Mat imageSatureExemple=MaBibliothequeTraitementImage.seuillage_exemple(imageTransformee, 170);	
				//Mat imageSaturee=MaBibliothequeTraitementImage.seuillage(imageTransformee, 6, 170, 110);
				//MaBibliothequeTraitementImageEtendue.afficheImage("Image HSV", imageTransformee);
				panel_1.removeAll();
				panel_1.repaint();
				panel_1.add(new JLabel(new ImageIcon(Function.Mat2bufferedImage(imageTransformee))));
				validate();
			}
		});
		btnButtonHSV.setBounds(434, 10, 150, 20);
		panel.add(btnButtonHSV);

		JButton btnSaturationRouge = new JButton("Saturation des rouges");
		btnSaturationRouge.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

				Mat imageOriginale=Imgcodecs.imread(imgFile.getText());
				Mat imageTransformee=MaBibliothequeTraitementImage.transformeBGRversHSV(imageOriginale);
				Mat imageSaturee=MaBibliothequeTraitementImage.seuillage(imageTransformee, 6, 170, 110);
				panel_1.removeAll();
				panel_1.repaint();
				panel_1.add(new JLabel(new ImageIcon(Function.Mat2bufferedImage(imageSaturee))));
				validate();
			}
		});
		btnSaturationRouge.setBounds(594, 10, 200, 20);
		panel.add(btnSaturationRouge);

		JButton btnContours = new JButton("Detection des contours de l'image");
		btnContours.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panneau.setText("");
				System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

				Mat imageOriginale=Imgcodecs.imread(imgFile.getText());
				Mat imageTransformee=MaBibliothequeTraitementImage.transformeBGRversHSV(imageOriginale);
				Mat input=MaBibliothequeTraitementImage.seuillage(imageTransformee, 6, 170, 110);
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
				panel_1.removeAll();
				panel_1.repaint();
				panel_1.add(new JLabel(new ImageIcon(Function.Mat2bufferedImage(drawing))));
				validate();

			}
		});
		btnContours.setBounds(804, 10, 250, 20);
		panel.add(btnContours);

		JButton btnMatching = new JButton("Detection methode 1");
		btnMatching.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String fileImg = "";
				//Ouverture de l'image et saturation des rouges
				System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
				Mat m=Imgcodecs.imread(imgFile.getText());
				//MaBibliothequeTraitementImageEtendue.afficheImage("Image test?e", m);
				Mat transformee=MaBibliothequeTraitementImageEtendue.transformeBGRversHSV(m);
				//la methode seuillage est ici extraite de l'archivage jar du meme nom 
				Mat saturee=MaBibliothequeTraitementImage.seuillage(transformee, 6, 170, 90);
				Mat objetrond = null;

				//Cr?ation d'une liste des contours ? partir de l'image satur?e
				List<MatOfPoint> ListeContours= MaBibliothequeTraitementImageEtendue.ExtractContours(saturee);
				double [] scores=new double [6];
				//Pour tous les contours de la liste
				for (MatOfPoint contour: ListeContours  ){
					objetrond=Function.DetectForm(m,contour);

					if (objetrond!=null){
						scores[0]=Function.Similitude(objetrond,"ref30.jpg");
						scores[1]=Function.Similitude(objetrond,"ref50.jpg");
						scores[2]=Function.Similitude(objetrond,"ref70.jpg");
						scores[3]=Function.Similitude(objetrond,"ref90.jpg");
						scores[4]=Function.Similitude(objetrond,"ref110.jpg");
						scores[5]=Function.Similitude(objetrond,"refdouble.jpg");


						//recherche de l'index du maximum et affichage du panneau detect?
						double scoremax=Double.POSITIVE_INFINITY;
						int indexmax=0;
						for(int j=0;j<scores.length;j++){
							if (scores[j]<scoremax){scoremax=scores[j];indexmax=j;}}	
						if(scoremax<0){System.out.println("Aucun Panneau d?tect?");}
						else{switch(indexmax){

						case -1:;break;
						case 0:
							panneau.setText("Panneau 30 d?tect?");
							fileImg="ref30.jpg";
							break;
						case 1:
							panneau.setText("Panneau 50 d?tect?");
							fileImg="ref50.jpg";
							break;
						case 2:
							panneau.setText("Panneau 70 d?tect?");
							fileImg="ref70.jpg";
							break;
						case 3:
							panneau.setText("Panneau 90 d?tect?");
							fileImg="ref90.jpg";
							break;
						case 4:
							panneau.setText("Panneau 110 d?tect?");
							fileImg="ref110.jpg";
							break;
						case 5:
							panneau.setText("Panneau interdiction de d?passer d?tect?");
							fileImg="refdouble.jpg";
							break;
						}
						}

					}
					
				}
				ImageIcon IMAGE = new ImageIcon(Toolkit.getDefaultToolkit().createImage(fileImg));
				panel_2.removeAll();
				panel_2.repaint();
				panel_2.add(new JLabel(IMAGE));
				validate();	
			}
		}
				);
		btnMatching.setBounds(35, 170, 200, 20);
		panel.add(btnMatching);

		panneau = new JTextArea();
		panneau.setBounds(10, 460, 254, 20);
		panel.add(panneau);
		panneau.setColumns(10);

		// Bouton d?tection m?thode 2
				JButton btnMatching2 = new JButton("Detection m?thode 2");
				btnMatching2.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						String fileImg1 = "";
						//Ouverture de l'image et saturation des rouges
						System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
						Mat m=Imgcodecs.imread(imgFile.getText());
						//MaBibliothequeTraitementImageEtendue.afficheImage("Image test?e", m);
						Mat transformee=MaBibliothequeTraitementImageEtendue.transformeBGRversHSV(m);
						//la methode seuillage est ici extraite de l'archivage jar du meme nom 
						Mat saturee=MaBibliothequeTraitementImage.seuillage(transformee, 6, 170, 90);
						Mat objetrond = null;

						//Cr?ation d'une liste des contours ? partir de l'image satur?e
						List<MatOfPoint> ListeContours= MaBibliothequeTraitementImageEtendue.ExtractContours(saturee);
						double [] scores=new double [6];
						//Pour tous les contours de la liste
						BufferedImage transfo = (null);
						for (MatOfPoint contour: ListeContours  ){
							objetrond=Function.DetectForm(m,contour);

							if (objetrond!=null){
								String value = ScannedImage.reconnaissance(objetrond);
								/*Mat transformation = Preparation.transformation(objetrond);
								Function.afficheImage("Differents panneaux", transformation);*/
								System.out.println(value);
								switch(value) {
								case "30":
									panneau_2.setText("Panneau 30 d?tect?");
									fileImg1="ref30.jpg";
									break;
								case "50":
									panneau_2.setText("Panneau 50 d?tect?");
									fileImg1="ref50.jpg";
									break;
								case "70" :
									panneau_2.setText("Panneau 70 d?tect?");
									fileImg1="ref70.jpg";
									break;
								case "90" :
									panneau_2.setText("Panneau 90 d?tect?");
									fileImg1="ref90.jpg";
									break;
								case "110":
									panneau_2.setText("Panneau 110 d?tect?");
									fileImg1="ref110.jpg";
									break;
								}
								
							}
						}
						ImageIcon IMAGE1 = new ImageIcon(Toolkit.getDefaultToolkit().createImage(fileImg1));
						panel_3.removeAll();
						panel_3.repaint();
						panel_3.add(new JLabel(IMAGE1));
						validate();	
					
					}
				}
						);
	
				btnMatching2.setBounds(1190, 20, 200, 20);
				panel.add(btnMatching2);

				panneau_2 = new JTextArea();
				panneau_2.setBounds(1165, 302, 254, 20);
				panel.add(panneau_2);
				panneau_2.setColumns(10);
				
				// Bouton d?tection m?thode 3
				JButton btnMatching3 = new JButton("Detection m?thode 3");
				btnMatching3.setBounds(1190, 330, 200, 20);
				panel.add(btnMatching3);

				panneau_3 = new JTextArea();
				panneau_3.setBounds(1165, 608, 254, 20);
				panel.add(panneau_3);
				panneau_2.setColumns(10);
		//Bouton Vid?o
		JButton btnVideo = new JButton("Lire la vid?o");	
		btnVideo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
				System.loadLibrary("opencv_java2413");
				MyThread thread = new MyThread(imgFile.getText(),panneau,panel_1);
				thread.start();
			}

		});
		btnVideo.setBounds(10, 70, 140, 20);
		panel.add(btnVideo);

	}

	public static void LectureVideo(String nomVideo, JTextArea panneau, JPanel panel_1 ) {

		Mat frame = new Mat();
		VideoCapture camera = new VideoCapture(nomVideo);

		while (camera.read(frame)) {
			/*
			if (detecte==true) {
				for (int j=0;j<5;j++) {
					camera.read(frame);
				}
			}*/
			String fileImg = "";

			panel_1.removeAll();

			panel_1.add(new JLabel(new ImageIcon(Function.Mat2bufferedImage(frame))));
			panel_1.repaint();
			panel_1.validate();



			Mat transformee=MaBibliothequeTraitementImageEtendue.transformeBGRversHSV(frame);
			//la methode seuillage est ici extraite de l'archivage jar du meme nom 
			Mat saturee=MaBibliothequeTraitementImage.seuillage(transformee, 6, 170, 90); 
			Mat objetrond = null;
			//Cr?ation d'une liste des contours ? partir de l'image satur?e
			List<MatOfPoint> ListeContours= MaBibliothequeTraitementImageEtendue.ExtractContours(saturee);
			int indexmax=0;
			for (MatOfPoint contour: ListeContours  ){
				objetrond=Function.DetectForm(frame,contour);
				indexmax=Function.identifiepanneau(objetrond);
				switch(indexmax){
				case -1:
					break;
				case 0:
					panneau.setText("Panneau 30 d?tect?");
					fileImg="ref30.jpg";
					break;
				case 1:
					panneau.setText("Panneau 50 d?tect?");
					fileImg="ref50.jpg";
					break;
				case 2:
					panneau.setText("Panneau 70 d?tect?");
					fileImg="ref70.jpg";
					break;
				case 3:
					panneau.setText("Panneau 90 d?tect?");
					fileImg="ref90.jpg";
					break;
				case 4:
					panneau.setText("Panneau 110 d?tect?");
					fileImg="ref110.jpg";
					break;
				case 5:
					panneau.setText("Panneau interdiction de d?passer d?tect?");
					fileImg="refdouble.jpg";
					break;
				}
				if (indexmax>=0) {
					detecte=true;
				}
			}
			panel_2.removeAll();
			panel_2.repaint();
			panel_2.add(new JLabel(new ImageIcon(fileImg)));
			panel_2.validate();	
		}
	}
}
