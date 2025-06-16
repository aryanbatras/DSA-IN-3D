import Collections.JMaxHeap;
import Rendering.*;

public class Main {
    public static void main(String[] args) {

        JMaxHeap heap = new JMaxHeap()
                .withQuality(Resolution.FASTEST)
                .withStepsPerAnimation(Frames.VERY_SLOW)
                .withRenderMode(Render.STEP_WISE)
                .withMaterial(Texture.METAL)
                .build();

        heap.add(10);
        heap.add(2);
        heap.add(5);
        heap.add(3);
        heap.add(4);
        heap.add(1);
        heap.add(6);
        heap.remove();
        heap.remove();
        heap.remove();
        heap.remove();
        heap.remove();
        heap.remove();

    }
}










