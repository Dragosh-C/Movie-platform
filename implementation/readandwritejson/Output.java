package implementation.readandwritejson;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import implementation.Movie;
import implementation.Notification;
import implementation.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public final class Output {
    private String error = null;
    private List<Movie> currentMoviesList;
    private User currentUser;
    private boolean premiumNotification = false;
    public Output(final String error, final List<Movie> currentMoviesList, final User currentUser) {
        this.error = error;
        this.currentMoviesList = currentMoviesList;
        this.currentUser = currentUser;
    }
    public Output(final boolean premiumNotification, final User currentUser) {
        this.premiumNotification = premiumNotification;
        this.currentUser = currentUser;
    }

    public Output() {

    }

    /**
     * @param output ArrayNode for JSON
     */
    public void objectToJson(final ArrayNode output) {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode object = mapper.createObjectNode();
        if (!premiumNotification) {
            ArrayNode movieList = mapper.createArrayNode();
            if (currentMoviesList != null) {
                for (Movie iterMovieList : currentMoviesList) {
                    addMoviesToJson(mapper, movieList, iterMovieList);
                }
            }

            object.put("error", error);
            object.set("currentMoviesList", movieList);
        }
        else {
            object.set("error", null);
            object.set("currentMoviesList", null);
        }
        ObjectNode user = mapper.createObjectNode();
        ObjectNode credentials = mapper.createObjectNode();
        if (currentUser != null && currentUser.getCredentials() != null) {
            addUserToJson(mapper, object, user, credentials);
        } else {
            object.set("currentUser", null);
        }
        output.add(object);

    }
    private void addUserToJson(final ObjectMapper mapper, final ObjectNode object,
                               final ObjectNode user, final ObjectNode credentials) {
        credentials.put("name", currentUser.getCredentials().getName());
        credentials.put("password", currentUser.getCredentials().getPassword());
        credentials.put("accountType", currentUser.getCredentials().getAccountType());
        credentials.put("country", currentUser.getCredentials().getCountry());
        credentials.put("balance", Integer.toString(currentUser.getCredentials()
                                                                        .getBalance()));

        user.set("credentials", credentials);
        user.put("tokensCount", currentUser.getTokensCount());
        user.put("numFreePremiumMovies", currentUser.getNumFreePremiumMovies());

        ArrayNode purchasedMovies = mapper.createArrayNode();
        if (currentUser.getPurchasedMovies() != null) {
            for (Movie movie : currentUser.getPurchasedMovies()) {
                addMoviesToJson(mapper, purchasedMovies, movie);
            }
        }
        user.set("purchasedMovies", purchasedMovies);

        ArrayNode watchedMovies = mapper.createArrayNode();
        if (currentUser.getWatchedMovies() != null) {
            for (Movie movie : currentUser.getWatchedMovies()) {
                addMoviesToJson(mapper, watchedMovies, movie);
            }
        }
        user.set("watchedMovies", watchedMovies);

        ArrayNode likedMovies = mapper.createArrayNode();
        if (currentUser.getLikedMovies() != null) {
            for (Movie movie : currentUser.getLikedMovies()) {
                addMoviesToJson(mapper, likedMovies, movie);
            }
        }
        user.set("likedMovies", likedMovies);

        ArrayNode ratedMovies = mapper.createArrayNode();
        if (currentUser.getRatedMovies() != null) {
            for (Movie movie : currentUser.getRatedMovies()) {
                addMoviesToJson(mapper, ratedMovies, movie);
            }
        }
        user.set("ratedMovies", ratedMovies);
        ArrayNode notifications = mapper.createArrayNode();

        for (Notification notItr : currentUser.getNotifications()) {
            ObjectNode fieldNot = mapper.createObjectNode();
            fieldNot.put("movieName", notItr.getMovieName());
            fieldNot.put("message", notItr.getMessage());
            notifications.add(fieldNot);
        }
        user.set("notifications", notifications);
        object.set("currentUser", user);
    }

    private void addMoviesToJson(final ObjectMapper mapper,
                                 final ArrayNode movieNode, final Movie movie) {
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("name", movie.getName());
        objectNode.put("year", Integer.toString(movie.getYear()));
        objectNode.put("duration", movie.getDuration());
        ArrayNode genres = mapper.createArrayNode();
        for (String iterGenres : movie.getGenres()) {
            genres.add(iterGenres);
        }
        objectNode.set("genres", genres);
        ArrayNode actors = mapper.createArrayNode();
        for (String iterActors : movie.getActors()) {
            actors.add(iterActors);
        }
        objectNode.set("actors", actors);

        ArrayNode countriesBanned = mapper.createArrayNode();
        for (String iterCountriesBanned : movie.getCountriesBanned()) {
            countriesBanned.add(iterCountriesBanned);
        }
        objectNode.set("countriesBanned", countriesBanned);
        objectNode.put("numLikes", movie.getNumLikes());
        objectNode.put("rating", movie.getRating());
        objectNode.put("numRatings", movie.getNumRating());

        movieNode.add(objectNode);
    }
}
