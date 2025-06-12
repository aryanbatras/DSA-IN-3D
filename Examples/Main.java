package Examples;

import Algorithms.Array;

public class Main {
    public static void main(String[] args) {

        Collections.JArrayList<String> arr = new Collections.JArrayList<>()
                .withBackgroundChangeOnEveryOperation(true)
                .withAlgoVisualizer(Array.BUBBLE_SORT)
                .withStepsPerAnimation(Rendering.Frames.NORMAL)
                .withQuality(Rendering.Resolution.FASTEST)
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










