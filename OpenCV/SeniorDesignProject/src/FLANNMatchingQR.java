import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;

import javax.imageio.ImageIO;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgcodecs.*;

class FLANNMatchingQR {
	public HashMap<String, Double> run(String inFile, String templateFile, int match_method) {
	    System.out.println("\nRunning FLANN...\n");
	    	
	    Mat img_1 = Imgcodecs.imread(getClass().getResource(inFile).getPath());
	    Mat img_2 = Imgcodecs.imread(getClass().getResource(templateFile).getPath());	  
	    
	    // check that images imported properly
	    if(img_1.empty() || img_2.empty()) { 
	    	System.out.println(" --(!) Error reading images \n"); 
	    	System.exit(1);
	    }
	    
	    //-- Step 1: Detect the keypoints using SURF Detector	    
	    int minHessian = 400;

        FeatureDetector detector = FeatureDetector.create(FeatureDetector.FAST);

        MatOfKeyPoint keypoints_1 = new MatOfKeyPoint();
        MatOfKeyPoint keypoints_2 = new MatOfKeyPoint();

        detector.detect(img_1, keypoints_1);
        detector.detect(img_2, keypoints_2);
	    
	    //-- Step 2: Calculate descriptors (feature vectors)
        DescriptorExtractor extractor = DescriptorExtractor
                .create(DescriptorExtractor.ORB);

        Mat descriptors_1 = new Mat();
        Mat descriptors_2 = new Mat();
        
        extractor.compute(img_1, keypoints_1, descriptors_1);
        extractor.compute(img_2, keypoints_2, descriptors_2);
	    
	    //-- Step 3: Matching descriptor vectors using FLANN matcher
        DescriptorMatcher matcher = DescriptorMatcher
                .create(DescriptorExtractor.ORB);
        MatOfDMatch matches = new MatOfDMatch();
        if (descriptors_1.cols() != descriptors_2.cols()) {
        	System.out.println("Error with descriptors...exiting...");
        	System.exit(1);
        } 
        
        matcher.match(descriptors_1, descriptors_2, matches);
        DMatch[] matchesArr = matches.toArray();
        
        double max_dist = 0; 
        double min_dist = 2000;
    
	    //-- Quick calculation of max and min distances between keypoints        
        for (int i = 0; i < matchesArr.length; i++) {
            double dist = matchesArr[i].distance;
            if (dist < min_dist)
                min_dist = dist;
            if (dist > max_dist)
                max_dist = dist;
        }

        System.out.printf("-- Max dist : %f \n", max_dist);
        System.out.printf("-- Min dist : %f \n", min_dist);
	    
	    //-- Find only "good" matches (i.e. whose distance is less than 2*min_dist,
	    //-- or a small arbitary value ( 600 ) in the event that min_dist is very
	    //-- small)
        MatOfDMatch good_matches = new MatOfDMatch();
        int arbitraryValue = 600;
        double accuracyMult = 2.0;
        System.out.println(accuracyMult +"*min_dist = " + accuracyMult*min_dist + " | arbitrary value = " + arbitraryValue);
        
        for (int i = 0; i < matchesArr.length; i++) {        	
            if (matchesArr[i].distance <= Math.max(accuracyMult * min_dist, arbitraryValue)) {
                good_matches.push_back(matches.row(i));
            }
        }
        
        // -- Draw only "good" matches
        Mat img_matches = new Mat();
        Features2d.drawMatches(img_1, keypoints_1, img_2, keypoints_2,
                good_matches, img_matches);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
 	    //get current date time with Date()
 	    Date date = new Date();
        boolean saved = Imgcodecs.imwrite("outputImg" + dateFormat.format(date) + ".png", img_matches);

        System.out.println(saved);
        System.out.println(good_matches);
        
        // Localize the Object
//        Point obj;
//        Point scene;
//                
//        // Get the keypoints from the good matches
//        for( int i = 0; i < good_matches.size().height; i++ ) {
//        	//-- Get the keypoints from the good matches
//        	System.out.println(good_matches.row(i);
////        	obj.push_back( keypoints_object[ good_matches[i].queryIdx ].pt );
////        	scene.push_back( keypoints_scene[ good_matches[i].trainIdx ].pt );
//        }
        
	    //-- Draw only "good" matches
	    
	    //-- Show detected matches
	    
	    return null;
	}
}