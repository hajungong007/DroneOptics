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
    	String firstImage = "/1a.png";
    	String secondImage = "/1b.png";
    	
    	//first with template matching
        //HashMap<String, Double> coordsResultTemplateMatching = 
        		//new FindQRTemplateMatching().run(firstImage, secondImage, Imgproc.TM_SQDIFF);
        //System.out.println("TM large image width: " + coordsResultTemplateMatching.get("largeWidth"));
        //System.out.println("TM large image height: " + coordsResultTemplateMatching.get("largeHeight"));
        //System.out.println("TM QR image center x coordinate: " + coordsResultTemplateMatching.get("xCoord"));
        //System.out.println("TM QR image center y coordinate: " + coordsResultTemplateMatching.get("yCoord"));
        
        //now with features2D framework
//        HashMap<String, Double> coordsResultFeatures2D = 
//        		new FindQR().run(firstImage, secondImage, Imgproc.TM_SQDIFF);
//        System.out.println("F2D large image width: " + coordsResultFeatures2D.get("largeWidth"));
//        System.out.println("F2D large image height: " + coordsResultFeatures2D.get("largeHeight"));
//        System.out.println("F2D QR image center x coordinate: " + coordsResultFeatures2D.get("xCoord"));
//        System.out.println("F2D QR image center y coordinate: " + coordsResultFeatures2D.get("yCoord"));
        
        //now with FLANN framework
        HashMap<String, Double> coordsResultFLANN = 
        		new FLANNMatchingQR().run(firstImage, secondImage, Imgproc.TM_SQDIFF);
        
        System.out.println("Exiting program.");
        System.exit(1);
    }
}