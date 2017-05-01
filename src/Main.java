/**
 * Created by hansolav on 22.02.2017.
 */

public class Main {

    public static void main(String[] args){
        CommunityAnalysis communityAnalysis = new CommunityAnalysis();
        communityAnalysis.script("LasVegas", NetworkType.Attributes, 1, true, true, 0, 5, true);
    }
}
