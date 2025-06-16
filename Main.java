import Collections.JAVLTree;
import Collections.JMaxHeap;
import Rendering.*;

public class Main {
    public static void main(String[] args) {

        JAVLTree avl = new JAVLTree()
                .withQuality(Resolution.FASTEST)
                .withRenderMode(Render.LIVE)
                .withStepsPerAnimation(Frames.VERY_SLOW)
                .withMaterial(Texture.METAL)
                .build();

        avl.add(10);
        avl.add(15);
        avl.add(20);
        avl.add(5);
        avl.add(2);
        avl.add(1);
        avl.remove(10);

    }
}










