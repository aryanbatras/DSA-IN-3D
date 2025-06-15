import Collections.JGraphs;
import Animations.Dynamo;
import Rendering.*;

public class Main {
    public static void main(String[] args) {

        JGraphs graph = new JGraphs()
                .withQuality(Resolution.BALANCE)
                .withStepsPerAnimation(Frames.NORMAL)
                .withRenderMode(Render.LIVE)
                .withMaterial(Texture.METAL)
                .build();

        graph.addVertex(1);
        graph.addVertex(2);
        graph.addVertex(3);
        graph.addVertex(4);
        graph.addVertex(5);

        graph.addEdge(1, 2);
        graph.addEdge(2, 1);
        graph.addEdge(2, 3);
        graph.addEdge(3, 2);
        graph.addEdge(3, 4);
        graph.addEdge(4, 3);
        graph.addEdge(4, 5);
        graph.addEdge(5, 4);

        graph.removeVertex(3);


    }
}










