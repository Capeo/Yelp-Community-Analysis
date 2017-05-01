

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import org.gephi.appearance.api.*;
import org.gephi.datalab.api.datatables.AttributeTableCSVExporter;
import org.gephi.graph.api.*;
import org.gephi.graph.impl.GraphModelImpl;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.statistics.plugin.Modularity;
import org.openide.util.Lookup;

import static java.nio.file.StandardCopyOption.*;

/**
 * Created by oddca on 22/02/2017.
 */
public class CommunityAnalysis {

    public void script(String city, NetworkType networkType, double resolution, Boolean filterEdges, Boolean filterSingleNodes, int edgeFilterThreshold, double ratingBias, Boolean plot) {
        String outPath = "Results/" + city + "/";
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        Workspace workspace = pc.getCurrentWorkspace();
        AppearanceController appearanceController = Lookup.getDefault().lookup(AppearanceController.class);
        AppearanceModel appearanceModel = appearanceController.getModel();
        GraphModel graphModel = new GraphModelImpl();

        IO input = new IO(city, networkType, ratingBias);
        String filename = "join_" + city + "_restaurants.json";
        if (networkType == NetworkType.Categories){
            input.readInputCategories(filename);
        }
        else if(networkType == NetworkType.Attributes){
            input.readInputAttributes(filename);
        }
        else if(networkType == NetworkType.AttributesAndCategories){
            input.readInputAttributesAndCategories(filename);
        }
        else {
            input.readInputReviews(filename);
        }
        input.determineEdges();
        input.fillGraph(graphModel, filterEdges, filterSingleNodes, edgeFilterThreshold);
        input.transformAttributes();

        //Check that graph is correctly created
        UndirectedGraph graph = graphModel.getUndirectedGraph();
        System.out.println("Nodes: " + graph.getNodeCount());
        System.out.println("Edges: " + graph.getEdgeCount());

        //Run modularity algorithm - community detection
        Modularity modularity = new Modularity();
        modularity.setRandom(true);
        modularity.setResolution(resolution);
        modularity.execute(graphModel);
        System.out.println("Modularity: " + modularity.getModularity());
        System.out.println("Random: " + modularity.getRandom());
        System.out.println("UseWeight: " + modularity.getUseWeight());

        // Add modularity_class to businesses.tsv
        HashMap<String, Integer> modularityClasses = new HashMap<String, Integer>();
        HashMap<Integer, Integer> communityCount = new HashMap<Integer, Integer>();
        for (Node node : graph.getNodes()){
            String businessId = node.getLabel();
            int modClass = (Integer) node.getAttribute("modularity_class");
            modularityClasses.put(businessId, modClass);
            if (communityCount.containsKey(modClass)){
                communityCount.put(modClass, communityCount.get(modClass) + 1);
            }
            else {
                communityCount.put(modClass, 1);
            }
        }
        input.writeBusinessData(modularityClasses);
        input.writeVisits(modularityClasses);

        ArrayList<Integer> values = new ArrayList<Integer>(communityCount.values());
        Collections.sort(values, Collections.reverseOrder());
        for (Integer i : values){
            System.out.print(i + ", ");
        }
        System.out.println();

        // Write csv files of nodes and edges
        try{
            File nodeFile = new File(outPath + "nodes.csv");
            File edgeFile = new File(outPath + "edges.csv");
            nodeFile.getParentFile().mkdirs();
            AttributeTableCSVExporter.writeCSVFile(graphModel.getGraph(), graphModel.getNodeTable(), nodeFile);
            AttributeTableCSVExporter.writeCSVFile(graphModel.getGraph(), graphModel.getEdgeTable(), edgeFile);
        }catch(IOException ex){
            ex.printStackTrace();
        }

        // Plot data using python scripts
        if (plot){
            try {
                Runtime.getRuntime().exec("python community_stats.py " + outPath + city + "_businesses.tsv");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        // Ask user if data should be saved
        Scanner sc = new Scanner(System.in);
        System.out.println("Save result? (y/n): ");
        String save = sc.nextLine().toLowerCase().trim();
        /*
        String save = "n";
        if (modularity.getModularity() > 0.1){
            save = "y";
        }
        */
        if (save.equals("y") || save.equals("yes")){
            try{
                System.out.println("Enter id: ");
                String id = sc.nextLine().trim();
                String savePath = "Results/SavedResults/" + id + "/";
                File parameters = new File(savePath + "parameters.txt");
                parameters.getParentFile().mkdirs();
                PrintWriter parameterWriter = new PrintWriter(parameters);
                parameterWriter.println("City " + city);
                parameterWriter.println("Modularity " + modularity.getModularity());
                parameterWriter.println("Resolution " + resolution);
                parameterWriter.println("Network_type " + networkType.toString());
                parameterWriter.println("Filter_edges " + filterEdges);
                parameterWriter.println("Filter_nodes " + filterSingleNodes);
                if (filterEdges){
                    parameterWriter.println("Edge_filter_threshold " + edgeFilterThreshold);
                }
                parameterWriter.close();
                Files.copy(Paths.get(outPath + city + "_businesses.tsv"), Paths.get(savePath + city + "_businesses.tsv"), REPLACE_EXISTING);
                Files.copy(Paths.get(outPath + city + "_visits.csv"), Paths.get(savePath + city + "_visits.csv"), REPLACE_EXISTING);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }

    }
}