import Rendering.*;
import Collections.*;

public class Main {
    public static void main(String[] args) {

        JQueue queue = new JQueue()
                .withBackgroundChangeOnEveryOperation(true)
                .withStepsPerAnimation(Frames.VERY_SLOW)
                .withBackground(Scenery.STUDIO_GARDEN)
                .withQuality(Resolution.BALANCE)
                .withParticle(Effect.FIREWORKS)
                .withMaterial(Texture.MIRROR)
                .withRenderMode(Render.STEP_WISE)
                .withCameraFocus(Zoom.X4)
                .build();



        queue.add(1);
        queue.add(20);
        queue.add(30);
        queue.add(40);
        queue.add(50);
        queue.add(60);
        queue.poll();
        queue.poll();
        queue.poll();
        queue.poll();





    }
}







