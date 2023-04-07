package implementation.readandwritejson;

import implementation.Movie;
import implementation.User;
import implementation.action.Actions;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public final class JsonRead {
    private List<User> users;
    private List<Movie> movies;
    private List<Actions> actions;
}
