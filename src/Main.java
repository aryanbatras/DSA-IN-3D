import Collections.*;

public class Main {
    public static void main(String[] args) {

        JArrayList arr = new JArrayList( );

        arr.add(10);
        arr.add(50);
        for (int i = 0; i < arr.size() - 1; i++) {
            for (int j = 0; j < arr.size() - i - 1; j++) {
                if (arr.get(j) > arr.get(j + 1)) {
                    int temp = arr.get(j);
                    arr.set(j, arr.get(j + 1));
                    arr.set(j + 1, temp);
                 }
            }
        }

    }
}




