import java.io.IOException;
import java.util.Scanner;

/**
 * Created by hansolav on 22.02.2017.
 */

public class Main {
    public static void main(String[] args){
        CommunityAnalysis communityAnalysis = new CommunityAnalysis();
        communityAnalysis.script("Edinburgh", NetworkType.Categories, 1, true, true, 1, 5);
    }
}
