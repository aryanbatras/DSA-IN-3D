import Collections.*;
import Animations.*;
import Algorithms.*;
import Rendering.*;

public class Main {
    public static void main(String[] args) {

        JArrayList arr = new JArrayList()
                .withBackgroundChangeOnEveryOperation(true)
                .withAlgoVisualizer(Algorithms.Array.BUBBLE_SORT)
                .withStepsPerAnimation(Rendering.Frames.NORMAL)
                .withQuality(Rendering.Resolution.FASTEST)
                .withRenderMode(Rendering.Render.LIVE)
                .withMaterial(Rendering.Texture.METAL)
                .withCameraFocus(Zoom.X1)
                .build();

        arr.add("Hello", Entrance.SLIDE_FROM_TOP);
        arr.add("World", Entrance.BOUNCE);
        arr.add("Hello", Entrance.SLIDE_FROM_TOP);
        arr.add("World", Entrance.BOUNCE);
        arr.run();
    }

}










