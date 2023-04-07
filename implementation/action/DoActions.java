package implementation.action;

import com.fasterxml.jackson.databind.node.ArrayNode;
import implementation.Movie;
import implementation.Notification;
import implementation.GlobalVariables;
import implementation.User;
import implementation.readandwritejson.Output;
import java.util.*;

public final class DoActions {
    private final List<Actions> actions;
    private final ArrayNode output;
    private final User currentUser;
    private final List<Movie> movies;
    private final Output out;
    private final OnPage onPage = new OnPage();
    private final ChangePage changePage = new ChangePage();
    private final List<User> users;
    public DoActions(final List<Actions> actions, final ArrayNode output, final User currentUser,
                    final List<Movie> movies, final Output out, final List<User> users) {
        this.actions = actions;
        this.output = output;
        this.currentUser = currentUser;
        this.movies = movies;
        this.out = out;
        this.users = users;
    }

    /**
     * I go through all the elements in the list,
     * and depending on which type they are, I perform that action
     */
    public void executeActions() {
        currentUser.setCurrentMovies(new ArrayList<>(movies));
        for (Actions action : actions) {
            if (action.getType().equals("change page")) {
                changePage.execute(action, currentUser, out, output, users);
            }

            if (action.getType().equals("on page")) {
                onPage.execute(action, currentUser, out, output, users, movies);
            }

            if (action.getType().equals("back")) {
                backAction(action);
            }
            // Add and delete movies from database
            if (action.getType().equals("database")) {
                if (action.getFeature().equals("add")) {
                    ListIterator<Movie> iter = movies.listIterator();
                    boolean alreadyHaveThisMovie = false;
                    while (iter.hasNext()) {
                        if (iter.next().getName().equals(action.getAddedMovie().getName())) {
                            out.setCurrentUser(null);
                            out.setError("Error");
                            out.setCurrentMoviesList(null);
                            out.objectToJson(output);
                            alreadyHaveThisMovie = true;
                            break;
                        }
                    }
                    if (!alreadyHaveThisMovie) {
                        Movie movie = new Movie.Builder(action.getAddedMovie().getName(),
                                action.getAddedMovie().getYear(), action.getAddedMovie()
                                .getDuration(), action.getAddedMovie().getGenres(),
                                action.getAddedMovie().getActors(), action.getAddedMovie()
                                .getCountriesBanned(), action.getAddedMovie().getNumLikes(),
                                action.getAddedMovie().getRating(), action.getAddedMovie()
                                .getNumRating()).sumRating(action.getAddedMovie()
                                .getSumRating()).build();

                        movies.add(movie);
                        for (User usrItr : users) {
                            for (String genreItr : usrItr.getSubscribedGenre()) {
                                for (String addedGenreItr : action.getAddedMovie().getGenres()) {
                                    if (genreItr.equals(addedGenreItr)) {
                                        Notification note = new Notification();
                                        note.setMessage("ADD");
                                        note.setMovieName(action.getAddedMovie().getName());
                                        usrItr.getNotifications().add(note);
                                    }
                                }
                            }
                        }
                        boolean addedNotification = false;
                        for (String genreItr : currentUser.getSubscribedGenre()) {
                            if (addedNotification) {
                                break;
                            }
                            for (String addedGenreItr : action.getAddedMovie().getGenres()) {
                                if (genreItr.equals(addedGenreItr)) {
                                    Notification note = new Notification();
                                    note.setMessage("ADD");
                                    note.setMovieName(action.getAddedMovie().getName());
                                    currentUser.getNotifications().add(note);
                                    addedNotification = true;
                                    break;
                                }
                            }
                        }

                        boolean banned = false;
                        for (String genreItr : action.getAddedMovie().getGenres()) {
                            if (genreItr.equals(currentUser.getCredentials().getCountry())) {
                                banned = true;
                                break;
                            }
                        }
                        if (!banned) {
                            currentUser.getCurrentMovies().add(action.getAddedMovie());
                        }
                    }
                }
                if (action.getFeature().equals("delete")) {
                    ListIterator<Movie> iter = movies.listIterator();
                    boolean wasDeleted = false;
                    while (iter.hasNext()) {
                        if (iter.next().getName().equals(action.getDeletedMovie())) {
                            iter.remove();
                            wasDeleted = true;
                            break;
                        }
                    }
                    if (!wasDeleted) {
                        out.setCurrentUser(null);
                        out.setError("Error");
                        out.setCurrentMoviesList(null);
                        out.objectToJson(output);
                    }

                    iter = currentUser.getCurrentMovies().listIterator();
                    while (iter.hasNext()) {
                        if (iter.next().getName().equals(action.getDeletedMovie())) {
                            iter.remove();
                            break;
                        }
                    }
                }
            }
        }
        // Notify premium user after all action are done
        if (currentUser.getCredentials().getAccountType().equals("premium")
                && !GlobalVariables.getInstance().getCurrentPage().equals("unauthenticated page")) {

            notifyUsers();
        }
    }

    /**
     *  Method for Notifying users about movies
     */
    private void notifyUsers() {

        // Store genres by number of likes
        TreeMap<String, Integer> numAppearancesGenres = new TreeMap<>();
        for (Movie likedMovies: currentUser.getLikedMovies()) {
            for (String genreLikedMovies : likedMovies.getGenres()) {
                if (numAppearancesGenres.get(genreLikedMovies) == null ) {
                    numAppearancesGenres.merge(genreLikedMovies, 1, Integer::sum);
                } else {
                    numAppearancesGenres.merge(genreLikedMovies,
                            numAppearancesGenres.get(genreLikedMovies) + 1, Integer::sum);
                }
                }
        }
        // Sort movies decreasingly by likes
        Comparator<Movie> sortMovies = Comparator.comparing(Movie::getNumLikes);
        currentUser.getCurrentMovies().sort(Collections.reverseOrder(sortMovies));

        Set<Map.Entry<String, Integer>> entrySet = numAppearancesGenres.entrySet();
        Map.Entry<String, Integer>[] entryArray = entrySet.toArray(new Map.Entry[entrySet.size()]);

        boolean notFound = true;
        for (Map.Entry<String, Integer> itrArray : entryArray) {
            if (!notFound) {
                break;
            }

            for (Movie movieItr : currentUser.getCurrentMovies()) {
                for (String genre : movieItr.getGenres()) {
                    if (genre.equals(itrArray.getKey())) {
                        for (Movie movieLiked : currentUser.getLikedMovies()) {
                            if (movieLiked.getName().equals(movieItr.getName())) {
                                break;
                            }

                            Notification stdNotification = new Notification();
                            stdNotification.setMovieName(movieItr.getName());
                            stdNotification.setMessage("Recommendation");
                            currentUser.getNotifications().add(stdNotification);
                            notFound = false;
                            break;
                        }
                    }
                }
                if (!notFound) {
                    break;
                }
            }
        }
        if (notFound) {
            Notification stdNotification = new Notification();
            stdNotification.setMovieName("No recommendation");
            stdNotification.setMessage("Recommendation");
            currentUser.getNotifications().add(stdNotification);
        }

        Output output1 = new Output(true, currentUser);
        output1.objectToJson(output);
    }

    /**
     * Perform back action
     */
    private void backAction(final Actions action) {
        if ((GlobalVariables.getInstance().getPagesVisited().size() - 1) > 0) {
            GlobalVariables.getInstance().getPagesVisited().
                remove(GlobalVariables.getInstance().getPagesVisited().size() - 1);

        action.setPage(GlobalVariables.getInstance().getPagesVisited().
                get(GlobalVariables.getInstance().getPagesVisited().size() - 1));
        changePage.execute(action, currentUser, out, output, users);
    } else {
            out.setCurrentUser(null);
            out.setError("Error");
            out.setCurrentMoviesList(null);
            out.objectToJson(output);
        }
    }
}
