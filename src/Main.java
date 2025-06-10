import Rendering.*;
import Randomizer.*;
import Animations.*;
import Collections.*;

public class Main {
    public static void main(String[] args) {

        JArrayList jArrayList = new JArrayList( )
                .withRandomizer(JArrayListRandomAnimation.INSTANCE)
                .withInsertAnimation(JArrayListInsertAnimation.SLIDE_FROM_RIGHT)
                .withRemoveAnimation(JArrayListRandomAnimation.randomRemoveAnimation())
                .withRenderMode(Render.LIVE)
                .build();

        jArrayList.add(1);
        jArrayList.add(2);
        jArrayList.add(4);
        jArrayList.set(1, 3);
        jArrayList.remove(0);
    }
}







