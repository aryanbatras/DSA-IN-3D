import Algorithms.MaxHeap;
import Algorithms.MinHeap;
import Collections.JAVLTree;
import Collections.JMaxHeap;
import Collections.JMinHeap;
import Rendering.*;

public class Main {
    public static void main(String[] args) {

        JMaxHeap heap = new JMaxHeap()
                .withBackgroundChangeOnEveryOperation(true)
                .withStepsPerAnimation(Frames.SLOW)
                .withQuality(Resolution.BALANCE)
                .withRenderMode(Render.LIVE)
                .withMaterial(Texture.METAL)
                .withCameraFocus(Zoom.X16)
                .build();

        heap.add(10);
        heap.add(15);
        heap.add(20);
        heap.add(5);
        heap.add(2);
        heap.add(1);

        MaxHeap.HEAP_SORT.run(heap);
    }
}










