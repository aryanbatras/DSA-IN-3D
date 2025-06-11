import Algorithms.*;
import Rendering.*;
import Collections.*;

public class Main {
    public static void main(String[] args) {

//        JArrayList arr = new JArrayList()
//                .withBackground(Scenery.THE_SKY_IS_ON_FIRE)
//                .withStepsPerAnimation(Frames.VERY_SLOW)
//                .withAlgoVisualizer(Array.SHIFT_LEFT)
//                .withParticle(Effect.FIREWORKS)
//                .withMaterial(Texture.MIRROR)
//                .withQuality(Resolution.GOOD)
//                .withRenderMode(Render.LIVE)
//                .withCameraFocus(Zoom.X4)
//                .build();
//
//        arr.add(60);
//        arr.add(20);
//        arr.add(40);
//        arr.add(30);
//        arr.run(); // THIS WILL CALL THE SORT


       JStack stack = new JStack()
                .withBackground(Scenery.THE_SKY_IS_ON_FIRE)
                .withStepsPerAnimation(Frames.VERY_SLOW)
                .withAlgoVisualizer(Stack.REVERSE_STACK)
                .withQuality(Resolution.FASTEST)
                .withParticle(Effect.FIREWORKS)
                .withMaterial(Texture.MIRROR)
                .withRenderMode(Render.LIVE)
                .withCameraFocus(Zoom.X1)
                .build();
       
        stack.run();

    }

}










