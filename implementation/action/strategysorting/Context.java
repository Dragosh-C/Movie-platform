package implementation.action.strategysorting;

import implementation.Movie;

import java.util.List;

public class Context {

    private Strategy strategy;
    public Context(final Strategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Strategy pattern method
     */
    public void executeStrategy(final List<Movie> movies, final String type) {
        strategy.sort(movies, type);
    }
}
