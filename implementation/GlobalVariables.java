package implementation;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Using singleton to store global variables
 */
@Getter
@Setter
public final class GlobalVariables {
    private static GlobalVariables instance = null;

    private String currentPage = "unauthenticated page";
    private String movie;
    private final int nrFreePremiumMovies = 15;
    private List<Movie> filteredMovies = new ArrayList<>();
    private List<String> pagesVisited = new ArrayList<>();


    /**
     * @return instance of Singleton
     */
    public static GlobalVariables getInstance() {
        if (instance == null) {
            instance = new GlobalVariables();
        }
        return instance;
    }
}
