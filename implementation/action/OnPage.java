package implementation.action;

import implementation.Movie;
import implementation.GlobalVariables;
import implementation.User;
import implementation.action.strategysorting.Context;
import implementation.action.strategysorting.Decreasing;
import implementation.action.strategysorting.Increasing;
import implementation.readandwritejson.Output;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.*;

/**
 * Implement On page actions
 */
public final class OnPage {
    public OnPage() {
    }

    /**
     * Call the methods for all actions
     *
     * @param action keep the current action
     * @param output write data to JSON
     */
    public void execute(final Actions action, final User currentUser,
                        final Output out, final ArrayNode output,
                        final List<User> users, final List<Movie> movies) {

        if (GlobalVariables.getInstance().getCurrentPage().equals("authenticated page")
                && action.getFeature().equals("login")) {
            errorDefault(out, output);
            return;
        }

        if (longinAction(action, currentUser, out, output, users, movies)) {
            return;
        }
        if (registerAction(action, currentUser, out, output, users)) {
            return;
        }
        if (searchOnMoviePage(action, currentUser, out, output)) {
            return;
        }
        if (filterOnPageMovies(action, currentUser, out, output)) {
            return;
        }
        if (upgradesInAuthPage(action, currentUser, out, output)) {
            return;
        }
        if (actionOnSeeDetailPage(action, currentUser, out, output)) {
            return;
        }
        if (subscribeAction(action, currentUser)) {
            return;
        }

        errorDefault(out, output);
    }

    private boolean subscribeAction(final Actions action, final User currentUser) {
        if (action.getFeature().equals("subscribe")) {
            if (GlobalVariables.getInstance().getCurrentPage().equals("see details")) {
                for (String genreItr : currentUser.getSubscribedGenre()) {
                    if (genreItr.equals(action.getSubscribedGenre())) {
                        return false;
                    }
                }
                for (Movie movie : currentUser.getCurrentMovies()) {
                    if (movie.getName().equals(GlobalVariables.getInstance().getMovie())) {
                        for (String genre : movie.getGenres()) {
                            if (genre.equals(action.getSubscribedGenre())) {
                                currentUser.getSubscribedGenre().add(genre);
                                return true;
                            }
                        }
                    }
                }
            } else {
                return false;
            }
        }
        return false;
    }

    private boolean longinAction(final Actions action, final User currentUser, final Output out,
                                 final ArrayNode output, final List<User> users,
                                 final List<Movie> movies) {
        if (GlobalVariables.getInstance().getCurrentPage().equals("login")) {
            if (action.getFeature().equals("login")) {
                boolean isLogged = false;

                for (User usr : users) {
                    if (usr.getCredentials().getName().equals(action.getCredentials().getName())
                            && usr.getCredentials().getPassword()
                            .equals(action.getCredentials().getPassword())) {
                        GlobalVariables.getInstance().setCurrentPage("authenticated page");
                        GlobalVariables.getInstance().getPagesVisited().add("authenticated page");
                        initializeCurrentUser(currentUser, movies, usr);
                        removeBannedMovies(currentUser.getCurrentMovies(),
                                currentUser.getCredentials().getCountry());
                        GlobalVariables.getInstance()
                                .setFilteredMovies(currentUser.getCurrentMovies());
                        makeOutput(currentUser, null, out, output);
                        isLogged = true;
                        break;
                    }
                }

                if (!isLogged) {
                    GlobalVariables.getInstance().setCurrentPage("unauthenticated page");
                    errorDefault(out, output);
                }
            }
            return true;
        }
        return false;
    }

    private static void initializeCurrentUser(final User currentUser,
                                              final List<Movie> movies, final User usr) {
        currentUser.setCredentials(usr.getCredentials());
        currentUser.setCurrentMovies(new ArrayList<>(movies));
        currentUser.setWatchedMovies(new ArrayList<>(usr.getWatchedMovies()));
        currentUser.setPurchasedMovies(new ArrayList<>(usr.getPurchasedMovies()));
        currentUser.setRatedMovies(new ArrayList<>(usr.getRatedMovies()));
        currentUser.setLikedMovies(new ArrayList<>(usr.getLikedMovies()));
        currentUser.setTokensCount(usr.getTokensCount());
        currentUser.setNumFreePremiumMovies(usr.getNumFreePremiumMovies());
        currentUser.setUserRate(usr.getUserRate());
    }

    private static boolean searchOnMoviePage(final Actions action, final User currentUser,
                                             final Output out, final ArrayNode output) {
        if (GlobalVariables.getInstance().getCurrentPage().equals("movies")) {
            if (action.getFeature().equals("search")) {
                List<Movie> foundMovies = new ArrayList<>();
                for (Movie movie : currentUser.getCurrentMovies()) {
                    if (movie.getName().startsWith(action.getStartsWith())) {
                        foundMovies.add(movie);
                    }
                }
                makeOutput(currentUser, foundMovies, out, output);
                return true;
            }
        }
        return false;
    }

    private static void makeOutput(final User currentUser, final List<Movie> foundMovies,
                                   final Output out, final ArrayNode output) {
        out.setCurrentUser(currentUser);
        out.setError(null);
        out.setCurrentMoviesList(foundMovies);
        out.objectToJson(output);
    }

    private boolean actionOnSeeDetailPage(final Actions action, final User currentUser,
                                          final Output out, final ArrayNode output) {
        if (GlobalVariables.getInstance().getCurrentPage().equals("see details")) {
            if (action.getFeature().equals("purchase")) {
                for (Movie movie : currentUser.getPurchasedMovies()) {
                    if (movie.getName().equals(GlobalVariables.getInstance().getMovie())) {
                        errorDefault(out, output);
                        return true;
                    }
                }
                boolean isPurchased = false;
                List<Movie> purchasedMovie = new ArrayList<>();
                for (Movie movie : currentUser.getCurrentMovies()) {
                    if (movie.getName().equals(GlobalVariables.getInstance().getMovie())) {
                        currentUser.getPurchasedMovies().add(movie);

                        if (currentUser.getCredentials().getAccountType().equals("premium")
                                && currentUser.getNumFreePremiumMovies() > 0) {
                            currentUser.setNumFreePremiumMovies(
                                    currentUser.getNumFreePremiumMovies() - 1);
                        } else {
                            final int moviePrice = 2;
                            currentUser.setTokensCount(currentUser.getTokensCount()
                                    - moviePrice);
                        }

                        purchasedMovie.add(movie);
                        isPurchased = true;
                        break;
                    }
                }
                writeToJson(currentUser, out, output, isPurchased, purchasedMovie);
                return true;
            }

            if (action.getFeature().equals("watch")) {
                for (Movie movie : currentUser.getWatchedMovies()) {
                    if (movie.getName().equals(GlobalVariables.getInstance().getMovie())) {
                        List<Movie> movieDetails = new ArrayList<>();
                        movieDetails.add(movie);
                        writeToJson(currentUser, out, output, true, movieDetails);
                        return true;
                    }
                }

                boolean isWatched = false;
                List<Movie> watchedMovie = new ArrayList<>();
                for (Movie movie : currentUser.getPurchasedMovies()) {
                    if (movie.getName().equals(GlobalVariables.getInstance().getMovie())) {
                        currentUser.getWatchedMovies().add(movie);
                        watchedMovie.add(movie);
                        isWatched = true;
                        break;
                    }
                }
                writeToJson(currentUser, out, output, isWatched, watchedMovie);
                return true;
            }

            if (action.getFeature().equals("like")) {
                for (Movie movie : currentUser.getLikedMovies()) {
                    if (movie.getName().equals(GlobalVariables.getInstance().getMovie())) {
                        errorDefault(out, output);
                        return true;
                    }
                }
                boolean isLiked = false;
                List<Movie> likedMovie = new ArrayList<>();
                for (Movie movie : currentUser.getWatchedMovies()) {
                    if (movie.getName().equals(GlobalVariables.getInstance().getMovie())) {
                        movie.setNumLikes(movie.getNumLikes() + 1);
                        currentUser.getLikedMovies().add(movie);
                        likedMovie.add(movie);
                        isLiked = true;

                        break;
                    }
                }
                writeToJson(currentUser, out, output, isLiked, likedMovie);
                return true;
            }

            if (action.getFeature().equals("rate")) {
                boolean changeRate = false;
                for (Movie movie : currentUser.getRatedMovies()) {
                    if (movie.getName().equals(GlobalVariables.getInstance().getMovie())) {
                          changeRate = true;
                    }
                }
                boolean isRated = false;
                List<Movie> ratedMovie = new ArrayList<>();
                final int lowestRate = 1;
                final int highestRate = 5;
                if (action.getRate() >= lowestRate && action.getRate() <= highestRate) {
                    for (Movie movie : currentUser.getWatchedMovies()) {
                        if (movie.getName().equals(GlobalVariables.getInstance().getMovie())) {
                            if (!changeRate) {
                            movie.setSumRating(movie.getSumRating() + action.getRate());
                            currentUser.getUserRate().put(GlobalVariables.getInstance().getMovie(),
                                    action.getRate());
                            movie.setNumRating(movie.getNumRating() + 1);
                            movie.setRating(movie.getSumRating() / movie.getNumRating());
                            currentUser.getRatedMovies().add(movie);
                            } else {
                                movie.setSumRating(movie.getSumRating()
                                        - currentUser.getUserRate().get(GlobalVariables
                                        .getInstance().getMovie()) + action.getRate());
                                movie.setRating(movie.getSumRating() / movie.getNumRating());
                            }
                            ratedMovie.add(movie);
                            isRated = true;
                            break;
                        }
                    }
                }
                writeToJson(currentUser, out, output, isRated, ratedMovie);

                return true;
            }
        }
        return false;
    }

    private boolean upgradesInAuthPage(final Actions action, final User currentUser,
                                       final Output out, final ArrayNode output) {
        if (GlobalVariables.getInstance().getCurrentPage().equals("upgradesInAuthPage")) {
            if (action.getFeature().equals("buy tokens")) {
                if (currentUser.getCredentials().getBalance() - action.getCount() >= 0) {
                    currentUser.getCredentials()
                            .setBalance(currentUser.getCredentials()
                                    .getBalance() - action.getCount());
                    currentUser.setTokensCount(currentUser.getTokensCount()
                                                    + action.getCount());
                } else {
                    errorDefault(out, output);
                }
            }

            if (action.getFeature().equals("buy premium account")) {
                // Verify if already have premium
                final int premiumAccountPrice = 10;
                if (currentUser.getTokensCount() >= premiumAccountPrice) {
                    currentUser.setTokensCount(currentUser.getTokensCount()
                            - premiumAccountPrice);
                    currentUser.getCredentials().setAccountType("premium");
                } else {
                    errorDefault(out, output);
                }
            }
            return true;
        }
        return false;
    }

    private static boolean filterOnPageMovies(final Actions action, final User currentUser,
                                              final Output out, final ArrayNode output) {
        if (GlobalVariables.getInstance().getCurrentPage().equals("movies")) {
            if (action.getFeature().equals("filter")) {
                if (action.getFilters().getSort() != null) {
                    if (action.getFilters().getSort().getDuration() != null) {
                        if (action.getFilters().getSort().getDuration().equals("increasing")) {
                            Context sortIncreasing = new Context(new Increasing());
                            sortIncreasing.executeStrategy(currentUser.getCurrentMovies(),
                                    action.getFilters().getSort().getRating());
                        } else {
                            Context sortDecreasing = new Context(new Decreasing());
                            sortDecreasing.executeStrategy(currentUser.getCurrentMovies(),
                                    action.getFilters().getSort().getRating());
                        }
                    } else {
                        sortByRating(action, currentUser);
                    }
                }
                List<Movie> filteredMovies = new ArrayList<>(currentUser.getCurrentMovies());
                if (action.getFilters().getContains() != null) {
                    if (action.getFilters().getContains().getActors() != null) {
                        ListIterator<Movie> iter = filteredMovies.listIterator();
                        while (iter.hasNext()) {
                            int numOfEquals = 0;
                            for (String filterActor : action.getFilters()
                                    .getContains().getActors()) {
                                for (String actor : iter.next().getActors()) {

                                    if (actor.equals(filterActor)) {
                                        numOfEquals++;
                                    }
                                }
                                if (numOfEquals != action.getFilters()
                                        .getContains()
                                        .getActors()
                                        .size()) {
                                    iter.remove();
                                }
                            }
                        }
                    }
                }

                if (action.getFilters().getContains() != null) {
                    if (action.getFilters().getContains().getGenre() != null) {
                        ListIterator<Movie> iter = filteredMovies.listIterator();
                        while (iter.hasNext()) {
                            int numOfEquals = 0;
                            for (String filterGenres : action.getFilters()
                                    .getContains().getGenre()) {
                                for (String actor : iter.next().getGenres()) {

                                    if (actor.equals(filterGenres)) {
                                        numOfEquals++;
                                    }
                                }
                                if (numOfEquals != action.getFilters()
                                        .getContains()
                                        .getGenre()
                                        .size()) {
                                    iter.remove();
                                }
                            }
                        }
                    }
                }
                makeOutput(currentUser, filteredMovies, out, output);
                GlobalVariables.getInstance().setFilteredMovies(filteredMovies);
                return true;
            }
        }
        return false;
    }

    private static void sortByRating(final Actions action, final User currentUser) {
        currentUser.getCurrentMovies().sort((d2, d1) -> {
            if (action.getFilters().getSort().getRating()
                    .equals("increasing")) {
                if (Double.compare(d1.getRating(), d2.getRating()) < 0) {
                    return 1;
                }
            }
            if (Double.compare(d1.getRating(), d2.getRating()) == 0) {
                return 0;
            }
            return -1;

        });
    }

    private boolean registerAction(final Actions action, final User currentUser, final Output out,
                                   final ArrayNode output, final List<User> users) {
        if (GlobalVariables.getInstance().getCurrentPage().equals("register")) {
            if (action.getFeature().equals("register")) {
                boolean alreadyExists = false;
                for (User usr : users) {
                    if (usr.getCredentials().getName().equals(action.getCredentials().getName())) {
                        GlobalVariables.getInstance().setCurrentPage("unauthenticated page");
                        errorDefault(out, output);
                        out.objectToJson(output);
                        alreadyExists = true;
                        break;
                    }
                }
                if (!alreadyExists) {
                    GlobalVariables.getInstance().setCurrentPage("authenticated page");
                    GlobalVariables.getInstance().getPagesVisited().add("authenticated page");
                    currentUser.setCredentials(action.getCredentials());
                    users.add(currentUser);
                    removeBannedMovies(currentUser.getCurrentMovies(),
                            currentUser.getCredentials().getCountry());
                    GlobalVariables.getInstance().setFilteredMovies(currentUser.getCurrentMovies());
                    makeOutput(currentUser, null, out, output);
                }
            }
            return true;
        }
        return false;
    }

    private void writeToJson(final User currentUser, final Output out, final ArrayNode output,
                             final boolean isPurchased, final List<Movie> purchasedMovie) {
        if (isPurchased) {
            out.setCurrentUser(currentUser);
            out.setError(null);
            out.setCurrentMoviesList(purchasedMovie);
            out.objectToJson(output);
            return;
        }
        errorDefault(out, output);
    }

    private static void errorDefault(final Output out, final ArrayNode output) {
        out.setCurrentUser(null);
        out.setError("Error");
        out.setCurrentMoviesList(null);
        out.objectToJson(output);
    }

    private void removeBannedMovies(final List<Movie> currentMovies, final String country) {

        ListIterator<Movie> iter = currentMovies.listIterator();
        while (iter.hasNext()) {
            for (String countryBanned : iter.next().getCountriesBanned()) {
                if (countryBanned.equals(country)) {
                    iter.remove();
                }
            }
        }
    }
}
