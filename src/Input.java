import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by oddca on 22/02/2017.
 */
public class Input {

    public static void TransformInput(String filename){
        // Create three hashmaps/dictionaries containing: list of businesses, list of businesses per user, list of edges
        HashMap<String, Integer> businesses = new HashMap<String, Integer>();
        HashMap<String, ArrayList<String>> reviewedBusinesses = new HashMap<String, ArrayList<String>>();
        HashMap<Integer, HashMap<Integer, Integer>> edges = new HashMap<Integer, HashMap<Integer, Integer>>();

        // Read in file
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            int lineNr = 0;
            int businessId = 0;
            while ((line = reader.readLine()) != null){
                if (lineNr != 0){
                    String[] parts = line.split(",");
                    String business = parts[1];
                    String user = parts[2];
                    if (!businesses.keySet().contains(business)){
                        businesses.put(business, businessId);
                        businessId++;
                    }
                    if (reviewedBusinesses.keySet().contains(user)){
                        if (!reviewedBusinesses.get(user).contains(business)){
                            reviewedBusinesses.get(user).add(business);
                        }
                    }
                    else {
                        ArrayList<String> businessList = new ArrayList<String>();
                        businessList.add(business);
                        reviewedBusinesses.put(user, businessList);
                    }
                }
                lineNr++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create the list of edges based on businesses and reviewedBusinesses
        for (String user : reviewedBusinesses.keySet()){
            for (int i = 0; i < reviewedBusinesses.get(user).size(); i++) {
                for (int j = 0; j < i; j++) {
                    int business1 = businesses.get(reviewedBusinesses.get(user).get(i));
                    int business2 = businesses.get(reviewedBusinesses.get(user).get(j));
                    if (business2 > business1){
                        // Swap so that business1 > business2
                        int temp = business1;
                        business1 = business2;
                        business2 = temp;
                    }
                    if (edges.keySet().contains(business1)){
                        if (edges.get(business1).keySet().contains(business2)){
                            edges.get(business1).put(business2, edges.get(business1).get(business2) + 1);
                        }
                        else {
                            edges.get(business1).put(business2, 1);
                        }
                    }
                    else {
                        HashMap<Integer, Integer> edge = new HashMap<Integer, Integer>();
                        edge.put(business2, 1);
                        edges.put(business1, edge);
                    }
                }
            }
        }

        try {
            /*PrintWriter nodeWriter = new PrintWriter("Nodes.csv", "UTF-8");
            nodeWriter.println("Id,Label");
            for (String b : businesses.keySet()){
                nodeWriter.println(businesses.get(b) + "," + b);
            }
            nodeWriter.close();

            int edgeId = 0;
            PrintWriter edgeWriter = new PrintWriter("Edges.csv", "UTF-8");
            edgeWriter.println("Id,Source,Target,Weight");
            for (int b1 : edges.keySet()){
                for (int b2 : edges.get(b1).keySet()){
                    edgeWriter.println(edgeId + "," + b1 + "," + b2 + "," + edges.get(b1).get(b2));
                    edgeId++;
                }
            }
            edgeWriter.close();
    */
            int edgeId = 0;
            PrintWriter graphWriter = new PrintWriter("graph.gml", "UTF-8");
            graphWriter.println("graph");
            graphWriter.println("[");
            for (String b : businesses.keySet()){
                graphWriter.println("node\n[");
                graphWriter.println("id " + businesses.get(b));
                graphWriter.println("label " + b);
                graphWriter.println("]");
            }
            for (int b1 : edges.keySet()){
                for (int b2 : edges.get(b1).keySet()){
                    graphWriter.println("edge\n[");
                    graphWriter.println("id " + edgeId);
                    graphWriter.println("source " + b1);
                    graphWriter.println("target " + b2);
                    graphWriter.println("weight " + edges.get(b1).get(b2));
                    graphWriter.println("]");
                    edgeId++;
                }
            }
            graphWriter.println("]");
            graphWriter.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

}
