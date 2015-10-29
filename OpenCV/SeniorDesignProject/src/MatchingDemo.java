import java.net.URL;

import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgcodecs.*;

class MatchingDemo {
	public void run(String inFile, String templateFile, int match_method) {
	    System.out.println("\nRunning Template Matching");
	    Mat img = Imgcodecs.imread(getClass().getResource(inFile).getPath());
	    Mat templ = Imgcodecs.imread(getClass().getResource(templateFile).getPath());
	    double minlocvalue = 7;
	    double maxlocvalue = 7;
	
	    double minminvalue = 7;
	    double maxmaxvalue = 7;
	
	
	    // / Create the result matrix
	    int result_cols = img.cols() - templ.cols() + 1;
	    int result_rows = img.rows() - templ.rows() + 1;
	    System.out.println(result_cols);
	    System.out.println(result_rows);
	    Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);
	
	    // / Do the Matching and Normalize
	    Imgproc.matchTemplate(img, templ, result, match_method);
	    Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());
	
	    // / Localizing the best match with minMaxLoc
	    MinMaxLocResult mmr = Core.minMaxLoc(result);
	
	    Point matchLoc;
	    if (match_method == Imgproc.TM_SQDIFF || match_method == Imgproc.TM_SQDIFF_NORMED) {
	        matchLoc = mmr.minLoc;
	        minminvalue = mmr.minVal; // test 
	    } else {
	        matchLoc = mmr.maxLoc;
	        maxmaxvalue = mmr.minVal; // test
	    }
	
	    // / Show me what you got
	    Imgproc.rectangle(img, matchLoc, new Point(matchLoc.x + templ.cols(),
	            matchLoc.y + templ.rows()), new Scalar(0, 255, 0));
		
	    System.out.println("MinVal "+ minminvalue);
	    System.out.println("MaxVal "+ maxmaxvalue);
	
	    System.out.println("xVal "+ matchLoc.x);
	    System.out.println("yVal "+ matchLoc.y);
	
	}
}