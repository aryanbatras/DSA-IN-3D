import Collections.*;
import Collections.Animations.*;

public class Main {
    public static void main(String[] args) {
        JArrayList arr = new JArrayList(new JArrayListRandomAnimation());
        arr.add(10);
        arr.add(30);
        arr.add(50);
        arr.remove(0);
        arr.remove(1);
        arr.remove(0);
    }
}



