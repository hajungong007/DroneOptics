import java.util.HashMap;

import org.opencv.core.Core;
import org.opencv.imgproc.Imgproc;


public class MainSearch {
    public static void main(String[] args) {
    	System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    	//coordsResult stores the dimensions of the large image (largeWidth, largeHeight)
	    //as well as the location of the middle of the QR code (xCoord, yCoord)
    	
    	//the run method takes in two images...the first image is the large image,
    	//and the second image is the QR code
        HashMap<String, Double> coordsResult = 
        		new FindQR().run("/2a.png", "/2b.png", Imgproc.TM_SQDIFF);
        System.out.println("large image width: " + coordsResult.get("largeWidth"));
        System.out.println("large image height: " + coordsResult.get("largeHeight"));
        System.out.println("QR image center x coordinate: " + coordsResult.get("xCoord"));
        System.out.println("QR image center y coordinate: " + coordsResult.get("yCoord"));
    }
}