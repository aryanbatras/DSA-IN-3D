import Rendering.*;
import Randomizer.*;
import Animations.*;
import Collections.*;

public class Main {
    public static void main(String[] args) {

        JLinkedList list = new JLinkedList()
                .withRandomizer(JLinkedListRandomizer.INSTANCE.withCrazyMode())
                .withBackgroundChangeOnEveryOperation(true)
                .withStepsPerAnimation(Steps.VERY_SLOW)
                .withParticle(Particle.GALAXY)
                .withQuality(Quality.BALANCE)
                .withRenderMode(Render.LIVE)
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







