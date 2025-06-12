package Utility;
import Utility.Code;

public class Condition {
    public static <T extends Comparable<T>> boolean isGreater(T a, T b) {
        boolean result = a.compareTo(b) > 0;
        Code.setConditionResult("→ " + result);  // FIXED
        return result;
    }

    public static <T extends Comparable<T>> boolean isSmaller(T a, T b) {
        boolean result = a.compareTo(b) < 0;
        Code.setConditionResult("→ " + result);  // FIXED
        return result;
    }

    public static <T extends Comparable<T>> boolean isEqual(T a, T b) {
        boolean result = a.compareTo(b) == 0;
        Code.setConditionResult("→ " + result);  // FIXED
        return result;
    }

}