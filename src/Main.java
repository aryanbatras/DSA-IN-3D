import Rendering.*;
import Randomizer.*;
import Animations.*;
import Collections.*;

public class Main {
    public static void main(String[] args) {

        JArrayList list = new JArrayList()
                .withRandomizer(JArrayListRandomizer.INSTANCE.withCrazyMode())
                .withBackgroundChangeOnEveryOperation(true)
                .withStepsPerAnimation(Steps.SLOW)
                .withParticle(Particle.GALAXY)
                .withQuality(Quality.FASTEST)
                .withRenderMode(Render.LIVE)
                .withCameraFocus(Focus.X16)
                .build();

            list.add(10);
            list.add(50);
            list.add(100);
            list.remove(2);
            list.remove(1);
            list.remove(0);

            list.add(10);
            list.add(50);
            list.add(100);
            list.remove(2);
            list.remove(1);
            list.remove(0);


    }
}







