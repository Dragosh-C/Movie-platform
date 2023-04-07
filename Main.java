import implementation.Movie;
import implementation.GlobalVariables;
import implementation.User;
import implementation.action.Actions;
import implementation.action.DoActions;
import implementation.readandwritejson.JsonRead;
import implementation.readandwritejson.Output;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Starting point for implementation
 * Loading and writing to JSON files
 */
public class Main {
    /**
     * main method
     * @param args list with input and output address
     */
    public static void main(String[] args) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        JsonRead inputData;
        if (args.length != 0) {
            inputData = objectMapper.readValue(new File(args[0]), JsonRead.class);

            ArrayNode output = objectMapper.createArrayNode();

            List<Actions> actions = inputData.getActions();
            List<User> users = inputData.getUsers();
            User currentUser = new User();
            List<Movie> movies = inputData.getMovies();
            Output out = new Output();

            DoActions doActions = new DoActions(actions, output, currentUser, movies, out, users);

            GlobalVariables.getInstance().setCurrentPage("unauthenticated page");

            doActions.executeActions();

            ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
            objectWriter.writeValue(new File(args[1]), output);
        }
    }
}
