import org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

import java.util.List;

/**
 * @author Diptopol
 * @since 12/22/2020 12:16 AM
 */
public class App {

    public static void main(String[] args) {
        tinkerGraphVertexCreationAndSearchExample();

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
}
