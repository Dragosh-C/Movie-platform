package implementation.action;

import implementation.Movie;
import implementation.UserCredentials;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Actions {
    private String type;
    private String page;
    private String feature;
    private String startsWith;
    private String objectType;
    private String movie;
    private UserCredentials credentials;
    private Filters filters;
    private int count;
    private int rate;
    private String subscribedGenre;
    private Movie addedMovie;
    private String deletedMovie;

}
