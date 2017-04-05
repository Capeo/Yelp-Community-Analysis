import Models.Business;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import Models.ResultJoin;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.fabric.xmlrpc.base.Array;
import org.apache.commons.codec.binary.StringUtils;

/**
 * Created by oddca on 22/02/2017.
 */
public class Input {

    private HashMap<String, Integer> businesses;
    private HashMap<String, ArrayList<String>> connectedBusinesses;
    private HashMap<String, HashMap<String, Double>> reviewRatings;
    private ArrayList<Business> businessInfo;
    private String city;
    private NetworkType type;
    private double ratingBias;

    public Input(String city, NetworkType type, double ratingBias){
        businesses = new HashMap<String, Integer>();
        connectedBusinesses = new HashMap<String, ArrayList<String>>();
        businessInfo = new ArrayList<Business>();
        reviewRatings = new HashMap<String, HashMap<String, Double>>();
        this.city = city;
        this.type = type;
        this.ratingBias = ratingBias;
    }

    public void createNetwork(Boolean filterSingleEdges, Boolean filterSingleNodes, int edgeFilterThreshold){
        // Create three hashmaps/dictionaries containing: list of businesses, list of businesses per user, list of edges
        HashMap<Integer, HashMap<Integer, Double>> edges = new HashMap<Integer, HashMap<Integer, Double>>();

        // Create the list of edges based on businesses and connectedBusinesses
        for (String connection : connectedBusinesses.keySet()){
            for (int i = 0; i < connectedBusinesses.get(connection).size(); i++) {
                for (int j = 0; j < i; j++) {
                    String busId1 = connectedBusinesses.get(connection).get(i);
                    String busId2 = connectedBusinesses.get(connection).get(j);
                    int business1 = businesses.get(connectedBusinesses.get(connection).get(i));
                    int business2 = businesses.get(connectedBusinesses.get(connection).get(j));
                    if (business2 > business1){
                        // Swap so that business1 > business2
                        int temp = business1;
                        business1 = business2;
                        business2 = temp;
                    }
                    if (type == NetworkType.Ratings){
                        double ratingDifference = ratingDifference(reviewRatings.get(connection).get(busId1), reviewRatings.get(connection).get(busId2), ratingBias);
                        if (edges.keySet().contains(business1)){
                            if (edges.get(business1).keySet().contains(business2)){
                                edges.get(business1).put(business2, edges.get(business1).get(business2) + ratingDifference);
                            }
                            else {
                                edges.get(business1).put(business2, ratingDifference);
                            }
                        }
                        else {
                            HashMap<Integer, Double> edge = new HashMap<Integer, Double>();
                            edge.put(business2, ratingDifference);
                            edges.put(business1, edge);
                        }
                    }
                    else {
                        if (edges.keySet().contains(business1)){
                            if (edges.get(business1).keySet().contains(business2)){
                                edges.get(business1).put(business2, edges.get(business1).get(business2) + 1.0);
                            }
                            else {
                                edges.get(business1).put(business2, 1.0);
                            }
                        }
                        else {
                            HashMap<Integer, Double> edge = new HashMap<Integer, Double>();
                            edge.put(business2, 1.0);
                            edges.put(business1, edge);
                        }
                    }
                }
            }
        }

        HashSet<Integer> connectedNodes = new HashSet<>();

        try {
            int edgeId = 0;
            File file = new File("Results/" + city + "/graph.gml");
            file.getParentFile().mkdirs();
            PrintWriter graphWriter = new PrintWriter(file);
            graphWriter.println("graph");
            graphWriter.println("[");
            for (int b1 : edges.keySet()){
                for (int b2 : edges.get(b1).keySet()){
                    if(!filterSingleEdges || edges.get(b1).get(b2) > edgeFilterThreshold){
                        graphWriter.println("edge\n[");
                        graphWriter.println("id " + edgeId);
                        graphWriter.println("source " + b1);
                        if (!connectedNodes.contains(b1)){
                            connectedNodes.add(b1);
                        }
                        graphWriter.println("target " + b2);
                        if (!connectedNodes.contains(b2)){
                            connectedNodes.add(b2);
                        }
                        graphWriter.println("weight " + edges.get(b1).get(b2));
                        graphWriter.println("]");
                        edgeId++;
                    }
                }
            }
            for (String b : businesses.keySet()){
                if (!filterSingleNodes || connectedNodes.contains(businesses.get(b))){
                    graphWriter.println("node\n[");
                    graphWriter.println("id " + businesses.get(b));
                    graphWriter.println("label " + b);
                    graphWriter.println("]");
                }
            }
            graphWriter.println("]");
            graphWriter.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private double ratingDifference(double rating1, double rating2, double bias){
        return bias - Math.abs(rating1 - rating2);
    }


    public void readInputCategories(String filename){
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
                    if (connectedBusinesses.keySet().contains(category)){
                        if (!connectedBusinesses.get(category).contains(busId)){
                            connectedBusinesses.get(category).add(busId);
                        }
                    }
                    else {
                        ArrayList<String> businessList = new ArrayList<String>();
                        businessList.add(busId);
                        connectedBusinesses.put(category, businessList);
                    }
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void readInputAttributes(String filename){
        ObjectMapper mapper = new ObjectMapper();
        try{
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            int businessId = 0;
            while ((line = reader.readLine()) != null){
                Business bus = mapper.readValue(line, Business.class);
                businessInfo.add(bus);
                String busId = bus.getBusinessId();
                if (!businesses.keySet().contains(busId)){
                    businesses.put(busId, businessId);
                    businessId++;
                }
                if (bus.getAttributes() != null){
                    for (String attribute : bus.getAttributes()){
                        String[] parts = attribute.split(": ");
                        ArrayList<String> names = getAttributeNames(parts);
                        for (String name : names){
                            if (connectedBusinesses.keySet().contains(name)){
                                if (!connectedBusinesses.get(name).contains(busId)){
                                    connectedBusinesses.get(name).add(busId);
                                }
                            }
                            else {
                                ArrayList<String> businessList = new ArrayList<String>();
                                businessList.add(busId);
                                connectedBusinesses.put(name, businessList);
                            }
                        }
                    }
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private ArrayList<String> getAttributeNames(String[] parts){
        ArrayList<String> names = new ArrayList<String>();
        String nameBase = parts[0].replaceAll("'", "");
        if (parts.length > 1){
            // Test if attribute is boolean
            if (parts[1].equals("True") || parts[1].equals("False")){
                if (parts[1].equals("True")){
                    names.add(nameBase);
                }
            }
            // Test if attribute is number
            else if (isNumeric(parts[1])){
                names.add(nameBase + ":" + parts[1]);
            }
            // Test if attribute list
            else if (parts[1].startsWith("{")){
                String l = parts[1].replace("{","").replace("}","");
                String[] list = l.split(", ");
                for (String element : list){
                    String[] subParts = element.split(": ");
                    names.addAll(getAttributeNames(subParts));
                }
            }
            // Assume attribute is string
            else {
                names.add(nameBase + ":" + parts[1]);
            }
        }
        return names;
    }

    private boolean isNumeric(String str){
        return str.matches("[-+]?\\d*\\.?\\d+");
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
                    if (connectedBusinesses.keySet().contains(user)){
                        if (!connectedBusinesses.get(user).contains(business)){
                            connectedBusinesses.get(user).add(business);
                        }
                    }
                    else {
                        ArrayList<String> businessList = new ArrayList<String>();
                        businessList.add(business);
                        connectedBusinesses.put(user, businessList);
                    }
                }
                lineNr++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void readInputReviews(String filename){
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
                    if (connectedBusinesses.keySet().contains(userId)){
                        if (!connectedBusinesses.get(userId).contains(busId)){
                            connectedBusinesses.get(userId).add(busId);
                        }
                        if (!reviewRatings.get(userId).containsKey(busId)){
                            reviewRatings.get(userId).put(busId, (double) review.getStars());
                        }
                    }
                    else {
                        ArrayList<String> businessList = new ArrayList<String>();
                        businessList.add(busId);
                        connectedBusinesses.put(userId, businessList);
                        HashMap<String, Double> reviewScores = new HashMap<String, Double>();
                        reviewScores.put(busId, (double) review.getStars());
                        reviewRatings.put(userId, reviewScores);
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
            File file = new File("Results/" + city + "/" + city + "_businesses.tsv");
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
            businessWriter.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void writeVisits(){
        try{
            File file = new File("Results/" + city + "/" + city + "_visits.csv");
            file.getParentFile().mkdirs();
            PrintWriter visitsWriter = new PrintWriter(file);
            visitsWriter.println("businessId,userId");
            for (Business bus : businessInfo){
                if (bus.getResultJoin() != null){
                    for (ResultJoin review : bus.getResultJoin()){
                        visitsWriter.println(review.getBusinessId() + "," + review.getUserId());
                    }
                }
            }
            visitsWriter.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

}
