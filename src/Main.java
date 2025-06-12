import Algorithms.Array;
import Rendering.*;
import Animations.*;
import Collections.*;

public class Main {
    public static void main(String[] args) {

        JArrayList<String> arr = new JArrayList<>()
                .withBackgroundChangeOnEveryOperation(true)
                .withAlgoVisualizer(Array.BUBBLE_SORT)
                .withStepsPerAnimation(Frames.NORMAL)
                .withQuality(Resolution.FASTEST)
                .withRenderMode(Render.LIVE)
                .withMaterial(Texture.METAL)
                .withCameraFocus(Zoom.X1)
                .build();

        arr.add("Hello", Entrance.SLIDE_FROM_TOP);
        arr.add("World", Entrance.BOUNCE);
        arr.add("Hello", Entrance.SLIDE_FROM_TOP);
        arr.add("World", Entrance.BOUNCE);
        arr.run();
    }

}










