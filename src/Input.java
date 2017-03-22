import Models.Business;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Models.ResultJoin;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.fabric.xmlrpc.base.Array;
import com.sun.xml.internal.ws.api.ha.StickyFeature;

/**
 * Created by oddca on 22/02/2017.
 */
public class Input {

    private HashMap<String, Integer> businesses;
    private HashMap<String, ArrayList<String>> reviewedBusinesses;
    private ArrayList<Business> businessInfo;
    private String city;

    public Input(String city){
        businesses = new HashMap<String, Integer>();
        reviewedBusinesses = new HashMap<String, ArrayList<String>>();
        businessInfo = new ArrayList<Business>();
        this.city = city;
    }

    public void transformInputReviews(Boolean filterSingles){
        // Create three hashmaps/dictionaries containing: list of businesses, list of businesses per user, list of edges
        HashMap<Integer, HashMap<Integer, Integer>> edges = new HashMap<Integer, HashMap<Integer, Integer>>();

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
            int edgeId = 0;
            File file = new File("Results\\" + city + "\\graph.gml");
            file.getParentFile().mkdirs();
            PrintWriter graphWriter = new PrintWriter(file);
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
                    if(!filterSingles || edges.get(b1).get(b2) > 1){
                        graphWriter.println("edge\n[");
                        graphWriter.println("id " + edgeId);
                        graphWriter.println("source " + b1);
                        graphWriter.println("target " + b2);
                        graphWriter.println("weight " + edges.get(b1).get(b2));
                        graphWriter.println("]");
                        edgeId++;
                    }
                }
            }
            graphWriter.println("]");
            graphWriter.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }


    public void readInputCategories(String filename){
        HashMap<String, ArrayList<String>> categoriesBusinesses = new HashMap<String, ArrayList<String>>();
        HashMap<Integer, HashMap<Integer, Integer>> edges = new HashMap<Integer, HashMap<Integer, Integer>>();

        // Read JSON. Create list of businesses and map from categories to list of businesses
        ObjectMapper mapper = new ObjectMapper();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            int businessId = 0;
            while((line = reader.readLine()) != null){
                Business bus = mapper.readValue(line, Business.class);
                businessInfo.add(bus);
                String busId = bus.getBusinessId();
                if (!businesses.keySet().contains(busId)){
                    businesses.put(busId, businessId);
                    businessId++;
                }
                for (String category : bus.getCategories()){
                    if (reviewedBusinesses.keySet().contains(category)){
                        if (!reviewedBusinesses.get(category).contains(busId)){
                            reviewedBusinesses.get(category).add(busId);
                        }
                    }
                    else {
                        ArrayList<String> businessList = new ArrayList<String>();
                        businessList.add(busId);
                        reviewedBusinesses.put(category, businessList);
                    }
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }


    public void readCSV(String filename){
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
    }


    public void parseJSON(String filename){
        ObjectMapper mapper = new ObjectMapper();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            int businessId = 0;
            while((line = reader.readLine()) != null){
                Business bus = mapper.readValue(line, Business.class);
                businessInfo.add(bus);
                String busId = bus.getBusinessId();
                List<ResultJoin> reviews = bus.getResultJoin();
                if (!businesses.keySet().contains(busId)){
                    businesses.put(busId, businessId);
                    businessId++;
                }
                for(ResultJoin review : reviews){
                    String userId = review.getUserId();
                    if (reviewedBusinesses.keySet().contains(userId)){
                        if (!reviewedBusinesses.get(userId).contains(busId)){
                            reviewedBusinesses.get(userId).add(busId);
                        }
                    }
                    else {
                        ArrayList<String> businessList = new ArrayList<String>();
                        businessList.add(busId);
                        reviewedBusinesses.put(userId, businessList);
                    }
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void writeBusinessData(HashMap<String, Integer> modularityClasses){
        try{
            File file = new File("Results\\" + city + "\\" + city + "_businesses.tsv");
            file.getParentFile().mkdirs();
            PrintWriter businessWriter = new PrintWriter(file);
            businessWriter.println("businessId\tname\tcity\tstars\tlatitude\tlongitude\treview_count\tattributes\tcategories\tmodularity_class");
            System.out.println("Size " + businessInfo.size());
            int i = 0;
            for (i = 0; i < businessInfo.size(); i++) {
                Business bus = businessInfo.get(i);
                String busId = bus.getBusinessId();
                Integer modClass = modularityClasses.get(busId);
                businessWriter.print(busId + "\t" + bus.getName() + "\t" + bus.getCity() + "\t" + bus.getStars() + "\t" + bus.getLatitude() + "\t" + bus.getLongitude() + "\t" + bus.getReviewCount() + "\t[");
                if (bus.getAttributes() != null){
                    for (int j = 0; j < bus.getAttributes().size(); j++){
                        businessWriter.print(bus.getAttributes().get(j));
                        if (j < bus.getAttributes().size() - 1){
                            businessWriter.print(", ");
                        }
                    }
                }
                businessWriter.print("]\t[");
                if (bus.getCategories() != null){
                    for (int j = 0; j < bus.getCategories().size(); j++){
                        businessWriter.print(bus.getCategories().get(j));
                        if (j < bus.getCategories().size() - 1){
                            businessWriter.print(", ");
                        }
                    }
                }
                businessWriter.print("]\t" + modClass + "\n");
                if(businessWriter.checkError()){
                    System.out.println("Write error");
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }

}
