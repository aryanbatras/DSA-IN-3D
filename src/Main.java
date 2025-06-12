import Algorithms.*;
import Animations.Entrance;
import Rendering.*;
import Collections.*;

public class Main {
    public static void main(String[] args) {

        JArrayList<String> arr = new JArrayList<>()
                .withBackgroundChangeOnEveryOperation(true)
                .withStepsPerAnimation(Frames.NORMAL)
                .withQuality(Resolution.FASTEST)
                .withRenderMode(Render.VIDEO)
                .withMaterial(Texture.METAL)
                .withCameraFocus(Zoom.X1)
                .build();

        arr.add("Hello", Entrance.SLIDE_FROM_TOP);
        arr.add("World", Entrance.BOUNCE);


    }

}










