import Algorithms.Array;
import Algorithms.MaxHeap;
import Algorithms.MinHeap;
import Collections.JAVLTree;
import Collections.JArrayList;
import Collections.JMinHeap;
import Rendering.*;

public class Main {
    public static void main(String[] args) {

        JArrayList arr = new JArrayList<>()
                .withBackgroundChangeOnEveryOperation(true)
                .withStepsPerAnimation(Frames.NORMAL)
                .withQuality(Resolution.FASTEST)
                .withRenderMode(Render.LIVE)
                .withMaterial(Texture.METAL)
                .withCameraFocus(Zoom.X4)
                .build();

        arr.add(20);
        arr.add(15);
        arr.add(10);
        arr.add(5);
        arr.add(2);
        arr.add(1);

        Array.BINARY_SEARCH.run(arr);
    }
}










