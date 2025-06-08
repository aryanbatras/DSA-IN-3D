import Collections.*;
import Animations.*;
import Rendering.*;

public class Main {
    public static void main(String[] args) {

        JArrayList arr = new JArrayList().withRenderMode(Render.VIDEO).build();
        JArrayList arr2 = new JArrayList().withRenderMode(Render.LIVE).build();

        for (int i = 0; i < 10; i++) {
            arr.add(i);
            arr2.add(i);
        }

    }
}




