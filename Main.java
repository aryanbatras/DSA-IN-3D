import Collections.*;
import Animations.*;
import Algorithms.*;
import Rendering.*;

public class Main {
    public static void main(String[] args) {

        JTrees<Integer> tree = new JTrees<>()
                .withBackgroundChangeOnEveryOperation(true)
                .withStepsPerAnimation(Frames.VERY_SLOW)
                .withMaterial(Rendering.Texture.METAL)
                .withRenderMode(Render.LIVE)
                .withQuality(Resolution.FASTEST)
                .withCameraFocus(Zoom.X1)
                .build();

        tree.add(10);
        tree.add(5);
        tree.add(15);
        tree.add(3);
        tree.add(7);
        tree.add(12);
        tree.add(20);
    }

}










