import Rendering.*;
import Randomizer.*;
import Animations.*;
import Collections.*;

public class Main {
    public static void main(String[] args) {

        JArrayList arr = new JArrayList()
                .withBackgroundChangeOnEveryOperation(true)
                .withRenderMode(Render.STEP_WISE_INTERACTIVE)
                .withBackground(Background.STUDIO_GARDEN)
                .withStepsPerAnimation(Steps.SLOW)
                .withParticle(Particle.AURORA)
                .withQuality(Quality.FASTEST)
                .withMaterial(Material.METAL)
                .withCameraFocus(Focus.X1)
                .build();


        arr.add(1);
        arr.add(20);
        arr.add(300);
        arr.get(0);
        arr.remove(0);
        arr.remove(0);
        arr.remove(0);






    }
}







