import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.IO;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Diptopol
 * @since 12/22/2020 12:16 AM
 */
public class App {

    public static void main(String[] args) {
        tinkerGraphVertexCreationAndSearchExample();
        tinkerGraphVertexEdgeCreationAndSearchExample();
        tinkerGraphVertexCreationFromGraphExample();
        tinkerGraphSerializationExample();
        tinkerGraphDeserializationExample();
        tinkerGraphProcessingInGremlinServer();
        tinkerGraphVertexCreationIfNotExistsExample();
        tinkerGraphAddingListProperty();
        tinkerGraphAddEdgeBasedOnPropertyValue();
        tinkerGraphPersistingInJanushGraphThroughGremlin();
    }

    /**
     * This method opens an in-memory graph database. Using GraphTraversalSource creates vertex and later search for
     * vertex with particular property.
     */
    private static void tinkerGraphVertexCreationAndSearchExample() {
        TinkerGraph tinkerGraph = TinkerGraph.open();
        GraphTraversalSource g = AnonymousTraversalSource.traversal().withEmbedded(tinkerGraph);

        g.addV("person")
                .property("name", "marko")
                .property("age", 30)
                .next();

        List<Object> outputList = g.V().has("name", "marko").values("age").toList();

        for (Object output : outputList) {
            System.out.println(output);
        }
    }

    /**
     * This method creates two vertices and one edge and later search for particular out vertex property
     */
    private static void tinkerGraphVertexEdgeCreationAndSearchExample() {
        TinkerGraph tinkerGraph = TinkerGraph.open();
        GraphTraversalSource g = AnonymousTraversalSource.traversal().withEmbedded(tinkerGraph);

        Vertex personVertex = g.addV("person")
                .property("name", "marko")
                .property("age", 30)
                .next();

        Vertex jobVertex = g.addV("job")
                .property("name", "Software Engineer")
                .property("company", "abcdefgh")
                .next();

        g.addE("works").from(personVertex).to(jobVertex).iterate();

        List<Object> outputList = g.V().has("name", "marko").out("works").values("name").toList();

        for (Object output : outputList) {
            System.out.println(output);
        }
    }

    /**
     * Vertex can also be added in TinkerGraph instead of using GraphTraversalSource and later can be searched using the
     * GraphTraversalSource
     */
    private static void tinkerGraphVertexCreationFromGraphExample() {
        TinkerGraph tinkerGraph = TinkerGraph.open();

        Vertex person = tinkerGraph.addVertex("name", "marko");
        Vertex job = tinkerGraph.addVertex("Job Position", "Software Engineer");

        person.addEdge("works", job);

        List<Object> outputList = tinkerGraph.traversal().V()
                .has("name", "marko")
                .out("works")
                .values("Job Position")
                .toList();

        for (Object output : outputList) {
            System.out.println(output);
        }

    }

    /**
     * This method provides the example of serializing the graph in a file
     */
    private static void tinkerGraphSerializationExample() {
        TinkerGraph tinkerGraph = TinkerGraph.open();

        Vertex person = tinkerGraph.addVertex("name", "marko");
        Vertex job = tinkerGraph.addVertex("Job Position", "Software Engineer");

        person.addEdge("works", job);

        tinkerGraph.traversal().io("resource\\graph.kryo")
                .with(IO.writer, IO.gryo)
                .write().iterate();
    }

    /**
     * This method provides the example of de-serializing the graph from a file
     */
    private static void tinkerGraphDeserializationExample() {
        TinkerGraph tinkerGraph = TinkerGraph.open();

        tinkerGraph.traversal().io("resource\\graph.kryo")
                .with(IO.reader, IO.gryo)
                .read().iterate();

        List<Object> outputList = tinkerGraph.traversal().V()
                .has("name", "marko")
                .out("works")
                .values("Job Position")
                .toList();

        for (Object output : outputList) {
            System.out.println(output);
        }
    }

    /**
     * This method connects to gremlin server to process the graph
     */
    private static void tinkerGraphProcessingInGremlinServer() {
        GraphTraversalSource traversalSource = AnonymousTraversalSource.traversal()
                .withRemote(DriverRemoteConnection.using("localhost", 8182, "g"));

        Vertex personVertex = traversalSource.addV("person")
                .property("name", "marko")
                .property("age", 30)
                .next();

        Vertex jobVertex = traversalSource.addV("job")
                .property("name", "Software Engineer")
                .property("company", "abcdefgh")
                .next();

        traversalSource.addE("works").from(personVertex).to(jobVertex).iterate();

        List<Object> outputList = traversalSource.V().has("name", "marko").out("works").values("name").toList();

        for (Object output : outputList) {
            System.out.println(output);
        }

        try {
            traversalSource.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This method connects to JanushGraph through Gremlin
     *
     * JanushGraph implements TinkerPop API. So the graph is accessible through gremlin server.
     * A docker image for Janush graph is available.
     *
     * For more info
     * see <a href="https://docs.janusgraph.org/getting-started/installation/">installation</a>
     * also <a href="https://docs.janusgraph.org/connecting/java/">Connecting With Java</a>
     */
    private static void tinkerGraphPersistingInJanushGraphThroughGremlin() {
        try {
            GraphTraversalSource traversalSource = AnonymousTraversalSource.traversal().withRemote("conf/remote-graph.properties");

            Vertex personVertex = traversalSource.addV("person")
                    .property("name", "marko")
                    .property("age", 30)
                    .next();

            Vertex jobVertex = traversalSource.addV("job")
                    .property("name", "Software Engineer")
                    .property("company", "abcdefgh")
                    .next();

            traversalSource.addE("works").from(personVertex).to(jobVertex).iterate();

            List<Object> outputList = traversalSource.V().has("name", "marko").out("works").values("name").toList();

            for (Object output : outputList) {
                System.out.println(output);
            }

            traversalSource.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method check whether vertex exists before adding the vertex
     */
    private static void tinkerGraphVertexCreationIfNotExistsExample() {
        TinkerGraph tinkerGraph = TinkerGraph.open();
        GraphTraversalSource g = AnonymousTraversalSource.traversal().withEmbedded(tinkerGraph);

        g.addV("person")
                .property("name", "marko")
                .property("age", 30)
                .next();

        boolean isVertexExists = g.V().has("name", "marko").has("age", 30).toSet().size() > 0;

        if (!isVertexExists) {
            g.addV("person")
                    .property("name", "marko")
                    .property("age", 30)
                    .next();
        }

        List<Object> outputList = g.V().has("name", "marko").values("age").toList();

        for (Object output : outputList) {
            System.out.println(output);
        }
    }

    /**
     * This method saves multiple values in the property
     */
    private static void tinkerGraphAddingListProperty() {
        TinkerGraph tinkerGraph = TinkerGraph.open();
        GraphTraversalSource g = AnonymousTraversalSource.traversal().withEmbedded(tinkerGraph);

        List<String> hobbies = new ArrayList<String>();
        hobbies.add("Reading books");
        hobbies.add("Playing Video Games");

        Vertex v = g.addV("person")
                .property("name", "marko")
                .property("age", 30)
                .next();

        for (String hobby : hobbies) {
            v.property(VertexProperty.Cardinality.list, "hobbies", hobby);
        }

        g.V().has("name", "marko").toStream().forEach(vertex -> {
            Iterator<VertexProperty<String>> iterator = v.properties("hobbies");
            List<String> hobbiesOutput = new ArrayList<>();

            while (iterator.hasNext()) {
                VertexProperty<String> property = iterator.next();
                hobbiesOutput.add(property.value());
            }

            hobbiesOutput.forEach(System.out::println);
        });
    }

    /**
     * This method adds edge between two vertices using the property value
     */
    private static void tinkerGraphAddEdgeBasedOnPropertyValue() {
        TinkerGraph tinkerGraph = TinkerGraph.open();
        GraphTraversalSource g = AnonymousTraversalSource.traversal().withEmbedded(tinkerGraph);

        g.addV("person")
                .property("name", "marko")
                .property("age", 30)
                .property("son", "mar")
                .next();

        g.addV("person")
                .property("name", "mar")
                .property("age", 5)
                .next();

        Set<Vertex> vertexSet = g.V().has("name", "marko")
                .has("son").toSet();

        vertexSet.forEach(fromVertex -> {
            String name = fromVertex.<String>property("son").value();
            Set<Vertex> toVertexSet = g.V().has("name", name).toSet();

            toVertexSet.forEach(toVertex -> g.addE("son").from(fromVertex).to(toVertex).iterate());
        });

        List<Integer> valueList = g.V().has("name", "marko").out("son").<Integer>values("age").toList();

        for (Integer value : valueList) {
            System.out.println(value);
        }
    }
}
