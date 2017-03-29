/**
 * Created by hansolav on 22.02.2017.
 */

public class Main {
    public static void main(String[] args){
        CommunityAnalysis communityAnalysis = new CommunityAnalysis();
        communityAnalysis.script("Edinburgh", NetworkType.Categories, 0.8, false, true, true, 3, 5);
    }
}
