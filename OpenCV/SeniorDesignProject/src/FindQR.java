import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
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

class FindQR {
	public HashMap<String, Double> run(String inFile, String templateFile, int match_method) {
	    System.out.println("\nRunning Features2D...\n");
	    	    
	    Mat img_1 = Imgcodecs.imread(getClass().getResource(inFile).getPath());
	    Mat img_2 = Imgcodecs.imread(getClass().getResource(templateFile).getPath());	  
	    	     
        if (img_1.empty() || img_2.empty()) {
            System.out.println(" --(!) Error reading images ");
            return null;
        }

        // -- Step 1: Detect the keypoints using SURF Detector
        //I am not sure where to use it
        int minHessian = 400;

        FeatureDetector detector = FeatureDetector.create(FeatureDetector.FAST);

        MatOfKeyPoint keypoints_1 = new MatOfKeyPoint();
        MatOfKeyPoint keypoints_2 = new MatOfKeyPoint();

        detector.detect(img_1, keypoints_1);
        detector.detect(img_2, keypoints_2);

        // -- Step 2: Calculate descriptors (feature vectors)
        DescriptorExtractor extractor = DescriptorExtractor
                .create(DescriptorExtractor.ORB);

        Mat descriptors_1 = new Mat();
        Mat descriptors_2 = new Mat();

        extractor.compute(img_1, keypoints_1, descriptors_1);
        extractor.compute(img_2, keypoints_2, descriptors_2);

        // -- Step 3: Matching descriptor vectors using FLANN matcher
        DescriptorMatcher matcher = DescriptorMatcher
                .create(DescriptorExtractor.ORB);
        MatOfDMatch matches = new MatOfDMatch();
        matcher.match(descriptors_1, descriptors_2, matches);
        DMatch[] matchesArr = matches.toArray();

        double max_dist = 0;
        double min_dist = 20000;

        // -- Quick calculation of max and min distances between keypoints
        for (int i = 0; i < matchesArr.length; i++) {
            double dist = matchesArr[i].distance;
            if (dist < min_dist)
                min_dist = dist;
            if (dist > max_dist)
                max_dist = dist;
        }

        System.out.printf("-- Max dist : %f \n", max_dist);
        System.out.printf("-- Min dist : %f \n", min_dist);

        // -- Draw only "good" matches (i.e. whose distance is less than
        // 2*min_dist,
        // -- or a small arbitary value ( 0.02 ) in the event that min_dist is
        // very
        // -- small)
        // -- PS.- radiusMatch can also be used here.
        MatOfDMatch good_matches = new MatOfDMatch();

        for (int i = 0; i < matchesArr.length; i++) {
        	
            if (matchesArr[i].distance <= Math.max(2 * min_dist, 0.02)) {
            	System.out.println(matchesArr[i].distance);
                good_matches.push_back(matches.row(i));
            }
        }

        // -- Draw only "good" matches
        Mat img_matches = new Mat();
        Features2d.drawMatches(img_1, keypoints_1, img_2, keypoints_2,
                good_matches, img_matches);//, Scalar.all(-1), Scalar.all(-1),
                //null, Features2d.NOT_DRAW_SINGLE_POINTS);
        boolean saved = Imgcodecs.imwrite("outputImgNew.png", img_matches);

        // ----Here i had to Patch around a little----
        MatOfByte matOfByte = new MatOfByte();

        Imgcodecs.imencode(".jpg", img_matches, matOfByte);

        byte[] byteArray = matOfByte.toArray();
        BufferedImage bufImage = null;
        try {

            InputStream in = new ByteArrayInputStream(byteArray);
            bufImage = ImageIO.read(in);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
        System.out.println(good_matches);

//        for (int i = 0; i < (int) good_matches.rows(); i++) {
//            System.out.printf(
//                    "-- Good Match [%d] Keypoint 1: %d  -- Keypoint 2: %d  \n",
//                    i, good_matches.toArray()[i].queryIdx,
//                    good_matches.toArray()[i].trainIdx);
//        }
	    
        System.out.println("130");
        
//	    FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB);
//	    DescriptorExtractor descriptor = DescriptorExtractor.create(DescriptorExtractor.ORB);
//	    DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE);
//
//	    //set up img (scene)
//	    Mat descriptors1 = new Mat();
//	    MatOfKeyPoint keypoints1 = new MatOfKeyPoint();
//	    //calculate descriptor for img
//	    detector.detect(img, keypoints1);
//	    descriptor.compute(img, keypoints1, descriptors1);
//
//	    //set up templ (template)
//	    Mat descriptors2 = new Mat();
//	    MatOfKeyPoint keypoints2 = new MatOfKeyPoint();
//	    //calculate descriptor for templ
//	    detector.detect(templ, keypoints2);
//	    descriptor.compute(templ, keypoints2, descriptors2);
//
//	    //match 2 images' descriptors
//	    MatOfDMatch matches = new MatOfDMatch();
//	    System.out.println("descriptors1 = " + descriptors1);
//	    System.out.println("descriptors2 = " + descriptors2);
//	    System.out.println("keypoints2 = + " + keypoints2);
//	    System.out.println("matches = " + matches);
//	    System.out.println("templ = " + templ);
//	    matcher.match(descriptors1, descriptors2, matches);
//	    
//	    //calculate max and min distances between keypoints
//	    double max_dist = 0;
//	    double min_dist = 2000;
//
//	    List<DMatch> matchesList = matches.toList();
//	    for(int i=0;i<descriptors1.rows();i++)
//	    {
//	        double dist = matchesList.get(i).distance;
//	        if (dist<min_dist && dist > 0.0) min_dist = dist;
//	        if (dist>max_dist) max_dist = dist;
//	    }
//	    
//	    //set up good matches, add matches if close enough
//	    LinkedList<DMatch> good_matches = new LinkedList<DMatch>();
//	    MatOfDMatch gm = new MatOfDMatch();
//	    for (int i=0;i<descriptors2.rows();i++)
//	    {
//	        if(matchesList.get(i).distance<1.3*min_dist)
//	        {
//	            good_matches.addLast(matchesList.get(i));
//	        }
//	    }
//	    gm.fromList(good_matches);
//	    System.out.println("size of good matches = " + good_matches.size());
//	    
//	    //put keypoints mats into lists
//	    List<KeyPoint> keypoints1_List = keypoints1.toList();
//	    List<KeyPoint> keypoints2_List = keypoints2.toList();
//
//	    //put keypoints into point2f mats so calib3d can use them to find homography
//	    LinkedList<Point> objList = new LinkedList<Point>();
//	    LinkedList<Point> sceneList = new LinkedList<Point>();
//	    for(int i=0;i<good_matches.size();i++)
//	    {
//	        objList.addLast(keypoints2_List.get(good_matches.get(i).queryIdx).pt);
//	        System.out.println(keypoints1_List.get(good_matches.get(i).trainIdx).pt);
//	        sceneList.addLast(keypoints1_List.get(good_matches.get(i).trainIdx).pt);
//	    }
//	    MatOfPoint2f obj = new MatOfPoint2f();
//	    MatOfPoint2f scene = new MatOfPoint2f();
//	    obj.fromList(objList);
//	    scene.fromList(sceneList);
//
//	    //output image
//	    Mat outputImg = new Mat();
//	    MatOfByte drawnMatches = new MatOfByte();
//	    Features2d.drawMatches(img, keypoints1, templ, keypoints2, gm, outputImg, Scalar.all(-1), Scalar.all(-1), drawnMatches,Features2d.NOT_DRAW_SINGLE_POINTS);
//	    	    
//	    //run homography on object and scene points
//	    Mat H = Calib3d.findHomography(obj, scene);
//	    Mat tmp_corners = new Mat(4,1,CvType.CV_32FC2);
//	    Mat scene_corners = new Mat(4,1,CvType.CV_32FC2);
//
//	    //get corners from object
//	    tmp_corners.put(0, 0, new double[] {0,0});
//	    tmp_corners.put(1, 0, new double[] {templ.cols(),0});
//	    tmp_corners.put(2, 0, new double[] {templ.cols(),templ.rows()});
//	    tmp_corners.put(3, 0, new double[] {0,templ.rows()});
//
//	    
//	    System.out.println("tmp_corners = " + tmp_corners);
//	    System.out.println("scene_corners = " + scene_corners);
//	    boolean saved = Imgcodecs.imwrite("outputImg.png", outputImg);
//	    Core.perspectiveTransform(tmp_corners,scene_corners, H);
//	    
//	    
//	    System.out.println("119");
//	    System.out.println(scene_corners);
//	    System.out.println(scene_corners.cols());
//	    System.out.println(scene_corners.size());
	    
	    
	    


//	    Core.line(outputImg, new Point(scene_corners.get(0,0)), new Point(scene_corners.get(1,0)), new Scalar(0, 255, 0),4);
//	    Core.line(outputImg, new Point(scene_corners.get(1,0)), new Point(scene_corners.get(2,0)), new Scalar(0, 255, 0),4);
//	    Core.line(outputImg, new Point(scene_corners.get(2,0)), new Point(scene_corners.get(3,0)), new Scalar(0, 255, 0),4);
//	    Core.line(outputImg, new Point(scene_corners.get(3,0)), new Point(scene_corners.get(0,0)), new Scalar(0, 255, 0),4);
	    
//	    MatOfKeyPoint keyPointsImg = new MatOfKeyPoint();
//	    MatOfKeyPoint keyPointsTempl = new MatOfKeyPoint();
//	    FeatureDetector myFeatureDetector = FeatureDetector.create(FeatureDetector.FAST);
//	    myFeatureDetector.detect(img,keyPointsImg);
//	    myFeatureDetector.detect(templ, keyPointsTempl);
//	    Mat result = new Mat();
//	    
//	    Features2d.drawMatches(img, keyPointsImg, templ, keyPointsTempl, matches, result);
	    
	    
//	    Mat mGray = img;
//	    Mat mObject = templ;
//	    
//	    Mat mView = mGray.clone();
//	    FeatureDetector myFeatureDetector=FeatureDetector.create(FeatureDetector.FAST);
//	    MatOfKeyPoint keypoints=new MatOfKeyPoint();
//	    myFeatureDetector.detect(mGray,keypoints);
//	    MatOfKeyPoint objectkeypoints=new MatOfKeyPoint();
//	    myFeatureDetector.detect(mObject,objectkeypoints);
//	    DescriptorExtractor Extractor=DescriptorExtractor.create(DescriptorExtractor.ORB);
//	    Mat sourceDescriptors=new Mat();
//	    Mat objectDescriptors=new Mat();
//	    Extractor.compute(mGray,keypoints,sourceDescriptors);
//	    Extractor.compute(mGray,objectkeypoints,objectDescriptors);
//	    DescriptorMatcher matcher=DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
//	    MatOfDMatch matches=new MatOfDMatch();
//	    matcher.match(sourceDescriptors,objectDescriptors,matches);
//	    Features2d.drawMatches(mGray,keypoints,mObject,objectkeypoints,matches,mView);
//	    Imgproc.resize(mView,mView,mGray.size());
//	    boolean saved = Imgcodecs.imwrite("testwithfeatures2d.png", mView);
	
	    //coordsResult stores the dimensions of the large image (largeWidth, largeHeight)
	    //as well as the location of the middle of the QR code (xCoord, yCoord)
	    HashMap<String, Double> coordsResult = new HashMap<String, Double>();
	    coordsResult.put("largeWidth", (double) img_1.cols());
	    coordsResult.put("largeHeight", (double) img_1.rows());
	    coordsResult.put("xCoord", -1.0);
	    coordsResult.put("yCoord", -1.0);
	    
	    return coordsResult;	    
	}
}