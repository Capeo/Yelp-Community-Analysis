

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.gephi.appearance.api.AppearanceController;
import org.gephi.appearance.api.AppearanceModel;
import org.gephi.appearance.api.Function;
import org.gephi.appearance.api.Partition;
import org.gephi.appearance.api.PartitionFunction;
import org.gephi.appearance.plugin.PartitionElementColorTransformer;
import org.gephi.appearance.plugin.palette.Palette;
import org.gephi.appearance.plugin.palette.PaletteManager;
import org.gephi.datalab.api.datatables.AttributeTableCSVExporter;
import org.gephi.graph.api.*;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.EdgeDirectionDefault;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.layout.plugin.AutoLayout;
import org.gephi.layout.plugin.forceAtlas.ForceAtlasLayout;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.statistics.plugin.Modularity;
import org.openide.util.Lookup;

/**
 * This demo shows how to get partitions and apply color transformation to them.
 * <p>
 * Partitions are always created from an attribute column, in the data since
 * import or computed from an algorithm. The demo so the following tasks:
 * <ul><li>Import a graph file.</li>
 * <li>Create and export to partition1.pdf the graph colored from the 'source'
 * column.</li>
 * <li>Run modularity algorithm, detecting communities. The algorithm create a
 * new column and label nodes with a community number.</li>
 * <li>Create and export to partition2.pdf the graph colored from the
 * 'modularity_class' column</li></ul>
 * Partitions are built from the <code>PartitionController</code> service. Then
 * the color transformer is created and passed with the partition to the
 * controller to apply colors.
 *
 * @author Mathieu Bastian
 */
public class CommunityAnalysis {

    public void script(String city, NetworkType networkType, double resolution, Boolean visualize, Boolean filterSingleEdges, Boolean filterSingleNodes, int edgeFilterThreshold, double ratingBias) {
        Input input = new Input(city, networkType, ratingBias);
        if (networkType == NetworkType.Categories){
            input.readInputCategories("join_" + city + "_restaurants.json");
        }
        else if(networkType == NetworkType.Attributes){
            input.readInputAttributes("join_" + city + "_restaurants.json");
        }
        else {
            input.readInputReviews("join_" + city + "_restaurants.json");
        }
        input.createNetwork(filterSingleEdges, filterSingleNodes, edgeFilterThreshold);
        //Init a project - and therefore a workspace
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        Workspace workspace = pc.getCurrentWorkspace();

        //Get controllers and models
        ImportController importController = Lookup.getDefault().lookup(ImportController.class);
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel();
        AppearanceController appearanceController = Lookup.getDefault().lookup(AppearanceController.class);
        AppearanceModel appearanceModel = appearanceController.getModel();

        //Import file
        Container container;
        try {
            File file = new File("Results/" + city + "/graph.gml");
            container = importController.importFile(file);
            container.getLoader().setEdgeDefault(EdgeDirectionDefault.UNDIRECTED);
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

        //Append imported data to GraphAPI
        importController.process(container, new DefaultProcessor(), workspace);

        //See if graph is well imported
        DirectedGraph graph = graphModel.getDirectedGraph();
        System.out.println("Nodes: " + graph.getNodeCount());
        System.out.println("Edges: " + graph.getEdgeCount());

        //Run modularity algorithm - community detection
        Modularity modularity = new Modularity();
        modularity.setRandom(true);
        modularity.setResolution(resolution);        // Set Resolution
        modularity.execute(graphModel);
        System.out.println("Modularity: " + modularity.getModularity());    // Print Modularity
        System.out.println("Random: " + modularity.getRandom());            // Print Random - True or False
        System.out.println("UseWeight: " + modularity.getUseWeight());      // Print UseWeight - True or False
        //Partition with 'modularity_class', just created by Modularity algorithm
        Column modColumn = graphModel.getNodeTable().getColumn(Modularity.MODULARITY_CLASS);
        Function func2 = appearanceModel.getNodeFunction(graph, modColumn, PartitionElementColorTransformer.class);
        Partition partition2 = ((PartitionFunction) func2).getPartition();
        System.out.println(partition2.size() + " partitions found");
        Palette palette2 = PaletteManager.getInstance().randomPalette(partition2.size());
        partition2.setColors(palette2.getColors());
        appearanceController.transform(func2);

        // Create Visualisation
        AutoLayout autoLayout = new AutoLayout(10, TimeUnit.SECONDS);
        autoLayout.setGraphModel(graphModel);
        //YifanHuLayout firstLayout = new YifanHuLayout(null, new StepDisplacement(1f));
        ForceAtlasLayout secondLayout = new ForceAtlasLayout(null);
        AutoLayout.DynamicProperty adjustBySizeProperty = AutoLayout.createDynamicProperty("forceAtlas.adjustSizes.name", Boolean.TRUE, 0.1f);//True after 10% of layout time
        AutoLayout.DynamicProperty repulsionProperty = AutoLayout.createDynamicProperty("forceAtlas.repulsionStrength.name", new Double(500.), 0f);//500 for the complete period
        //autoLayout.addLayout(firstLayout, 0.5f);
        autoLayout.addLayout(secondLayout, 1f, new AutoLayout.DynamicProperty[]{adjustBySizeProperty, repulsionProperty});
        autoLayout.execute();

        if(visualize){
            //Export
            ExportController ec = Lookup.getDefault().lookup(ExportController.class);
            try {
                File file = new File("Results/" + city + "/image.pdf");
                file.getParentFile().mkdirs();
                ec.exportFile(file);
            } catch (IOException ex) {
                ex.printStackTrace();
                return;
            }
        }

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
        ArrayList<Integer> values = new ArrayList<Integer>(communityCount.values());
        Collections.sort(values, Collections.reverseOrder());
        for (Integer i : values){
            System.out.print(i + ", ");
        }
        System.out.println();

        try{
            File nodeFile = new File("Results/" + city + "/nodes.csv");
            File edgeFile = new File("Results/" + city + "/edges.csv");
            nodeFile.getParentFile().mkdirs();
            AttributeTableCSVExporter.writeCSVFile(graphModel.getGraph(), graphModel.getNodeTable(), nodeFile);
            AttributeTableCSVExporter.writeCSVFile(graphModel.getGraph(), graphModel.getEdgeTable(), edgeFile);
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
}