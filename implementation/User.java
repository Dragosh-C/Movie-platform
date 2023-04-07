package implementation;


import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class User {
    private UserCredentials credentials;
    private int tokensCount;
    private int numFreePremiumMovies = GlobalVariables.getInstance().getNrFreePremiumMovies();
    private List<Movie> purchasedMovies = new ArrayList<>();
    private List<Movie> watchedMovies = new ArrayList<>();
    private List<Movie> likedMovies = new ArrayList<>();
    private List<Movie> ratedMovies = new ArrayList<>();
    private List<Movie> currentMovies;
    private List<String> subscribedGenre = new ArrayList<>();
    private Map<String, Integer> userRate = new HashMap<>();
    private List<Notification> notifications = new ArrayList<>();
    public List<Movie> getCurrentMovies() {
        return this.currentMovies;
    }


    public User() {
    }

    /**
     * Deep copy of user object
     * @param usr make a copy for usr
     */
    public User(User usr) {
        this.credentials = new UserCredentials(usr.getCredentials());
        this.tokensCount = usr.getTokensCount();
        this.numFreePremiumMovies = usr.getNumFreePremiumMovies();
        this.purchasedMovies = new ArrayList<>(usr.getPurchasedMovies());
        this.watchedMovies = new ArrayList<>(usr.getWatchedMovies());
        this.likedMovies = new ArrayList<>(usr.getLikedMovies());
        this.ratedMovies = new ArrayList<>(usr.getRatedMovies());
        this.currentMovies = usr.getCurrentMovies();
        this.userRate = new HashMap<>(usr.getUserRate());
        this.subscribedGenre = new ArrayList<>(usr.subscribedGenre);
        this.notifications = new ArrayList<>(usr.notifications);
    }
}
