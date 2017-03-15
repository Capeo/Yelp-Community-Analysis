/**
 * Created by hansolav on 22.02.2017.
 */


public class Main {
    public static void main(String[] args){
        CommunityAnalysis communityAnalysis = new CommunityAnalysis();
        communityAnalysis.script("join_Edinburgh_restaurants.json", 1, false, true);
    }
}
