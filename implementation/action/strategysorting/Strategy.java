package implementation.action.strategysorting;
import implementation.Movie;
import java.util.List;


public interface Strategy {
    /**
     * Sort method for strategy pattern
     */
    void sort(List<Movie> movies, String type);
}
