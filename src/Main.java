import Algorithms.*;
import Rendering.*;
import Collections.*;

public class Main {
    public static void main(String[] args) {

        JArrayList<String> arr = new JArrayList<>()
                .withBackgroundChangeOnEveryOperation(true)
                .withStepsPerAnimation(Frames.VERY_SLOW)
                .withAlgoVisualizer(Array.QUICK_SORT)
                .withQuality(Resolution.BALANCE)
                .withRenderMode(Render.LIVE)
                .withMaterial(Texture.METAL)
                .withCameraFocus(Zoom.X4)
                .withSharedEncoder(true)
                .build();

        JStack<String> stack = new JStack<>()
                .withBackgroundChangeOnEveryOperation(true)
                .withAlgoVisualizer(Stack.REVERSE_STACK)
                .withStepsPerAnimation(Frames.NORMAL)
                .withQuality(Resolution.FASTEST)
                .withRenderMode(Render.LIVE)
                .withMaterial(Texture.METAL)
                .withCameraFocus(Zoom.X4)
                .withSharedEncoder(true)
                .build();


        stack.push("a");
        arr.add("c");
        stack.push("(");
        arr.add("e");
        stack.push("b");
        arr.add("d");
        stack.push(")");
        arr.add("a");
        stack.push("c");
        arr.add("b");
        stack.push(")");
        arr.run();
        stack.run();


    }

}










