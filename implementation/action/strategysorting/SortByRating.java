package implementation.action.strategysorting;

public class SortByRating {
    static int sortByRating(final double num1, final double num2, final String type) {
        if (type.equals("increasing")) {
            if (Double.compare(num1, num2) < 0) {
                return 1;
            }

        }
        if (Double.compare(num1, num2) == 0) {
            return 0;
        }
        return -1;
    }
}
