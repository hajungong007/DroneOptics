import org.opencv.core.Core;
import org.opencv.imgproc.Imgproc;


public class TemplateMatching {
    public static void main(String[] args) {
    	System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        new MatchingDemo().run("/2a.png", "/2b.png", Imgproc.TM_SQDIFF);
    }
}