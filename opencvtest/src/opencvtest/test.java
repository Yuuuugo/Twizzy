package opencvtest;
import org.opencv.core.*;

public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat mat = Mat.eye(3, 3,CvType.CV_8UC1);
		System.out.println("mat = " +mat.dump());
	}

}
