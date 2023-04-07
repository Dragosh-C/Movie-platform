package implementation.action.strategysorting;

import implementation.Movie;

import java.util.List;

public class Decreasing implements Strategy {
    /**
     * Sort list decreasingly first by duration
     * and if equals by rating
     */
    @Override
    public void sort(final List<Movie> movies, final String type) {
        movies.sort((d2, d1) -> {
            if (d2.getDuration() != d1.getDuration()) {
                return d1.getDuration() - d2.getDuration();
            }
            return SortByRating.sortByRating(d1.getRating(), d2.getRating(), type);
        });

    }
}
