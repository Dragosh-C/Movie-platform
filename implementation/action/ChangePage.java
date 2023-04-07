package implementation.action;
import implementation.Movie;
import implementation.GlobalVariables;
import implementation.User;
import implementation.readandwritejson.Output;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
@AllArgsConstructor
public final class ChangePage {

    /**
     * Performing actions Change Page
     * @param action Data from input
     * @param out Write to JSON object
     * @param output ArrayNode for performing writing
     * @param users List of users
     */
    public void execute(final Actions action, final User currentUser, final Output out,
                        final ArrayNode output, final List<User> users) {
        String page = action.getPage();

        if (GlobalVariables.getInstance().getCurrentPage().equals("unauthenticated page")) {
            if (!(page.equals("login") || page.equals("register"))) {

                makeOutput("Error", null, null, out, output);

            } else {
                if (page.equals("login")) {
                    GlobalVariables.getInstance().setCurrentPage("login");
                }

                if (page.equals("register")) {
                    GlobalVariables.getInstance().setCurrentPage("register");
                }
            }
        }

        if (GlobalVariables.getInstance().getCurrentPage().equals("authenticated page")) {
            if (page.equals("login")) {
                makeOutput("Error", null, null, out, output);
                return;
            }

            if (!(page.equals("movies") || page.equals("upgrades") || page.equals("logout"))) {

                GlobalVariables.getInstance().setCurrentPage("unauthenticated page");
                makeOutput("Error", null, null, out, output);
            }


            if (page.equals("movies")) {
                GlobalVariables.getInstance().setCurrentPage("movies");
                GlobalVariables.getInstance().getPagesVisited().add("movies");
                makeOutput(null, currentUser, currentUser.getCurrentMovies(), out, output);
            }

            if (page.equals("upgrades")) {
                GlobalVariables.getInstance().setCurrentPage("upgradesInAuthPage");
                GlobalVariables.getInstance().getPagesVisited().add("upgradesInAuthPage");
            }
            logoutAction(page, currentUser, users);
            return;
        }

        if (GlobalVariables.getInstance().getCurrentPage().equals("movies")) {
            if (!(page.equals("authenticated page") || page.equals("see details")
                    || page.equals("logout") || page.equals("movies"))) {
                GlobalVariables.getInstance().setCurrentPage("unauthenticated page");
                makeOutput("Error", null, null, out, output);
            }

            if (page.equals("movies")) {
                GlobalVariables.getInstance().setCurrentPage("movies");
                GlobalVariables.getInstance().getPagesVisited().add("movies");
                makeOutput(null, currentUser, currentUser.getCurrentMovies(), out, output);
            }

            if (page.equals("authenticated page")) {
                GlobalVariables.getInstance().setCurrentPage("authenticated page");
            }

            if (page.equals("see details")) {
                List<Movie> detailsMovie = new ArrayList<>();
                for (Movie movie : GlobalVariables.getInstance().getFilteredMovies()) {
                    if (movie.getName().equals(action.getMovie())) {
                        detailsMovie.add(movie);
                    }
                }

                if (!detailsMovie.isEmpty()) {
                    GlobalVariables.getInstance().setCurrentPage("see details");
                    GlobalVariables.getInstance().getPagesVisited().add("see details");
                    GlobalVariables.getInstance().setMovie(action.getMovie());
                    makeOutput(null, currentUser, detailsMovie, out, output);
                } else {
                    makeOutput("Error", null, null, out, output);
                }
            }

            // Add data of current user in users array
            saveUserData(page, currentUser, users);
            return;
        }

        if (GlobalVariables.getInstance().getCurrentPage().equals("see details")) {
            setMoviesToJson(page, currentUser, currentUser.getCurrentMovies(), out, output);
                if (page.equals("upgrade")) {
                    GlobalVariables.getInstance().setCurrentPage("upgradeInSeeDetails");
                    GlobalVariables.getInstance().getPagesVisited().add("upgradeInSeeDetails");
                }
            logoutAction(page, currentUser, users);
            return;
        }

        if (GlobalVariables.getInstance().getCurrentPage().equals("upgradesInAuthPage")) {
            setMoviesToJson(page, currentUser, currentUser.getCurrentMovies(), out, output);
            if (page.equals("logout")) {
                GlobalVariables.getInstance().setCurrentPage("logout");
                GlobalVariables.getInstance().getPagesVisited().add("logout");
                for (int i = 0; i < users.size(); i++) {
                    if (users.get(i).getCredentials().getName()
                            .equals(currentUser.getCredentials().getName())) {
                        users.set(i, new User(currentUser));
                        break;
                    }
                }
            }
        }
    }

    private void saveUserData(final String page, final User currentUser, final List<User> users) {
        if (page.equals("logout")) {
            GlobalVariables.getInstance().setCurrentPage("unauthenticated page");
                for (int i = 0; i < users.size(); i++) {
                    if (users.get(i).getCredentials().getName()
                            .equals(currentUser.getCredentials().getName())) {
                        users.set(i, new User(currentUser));
                        break;
                    }
                }
                GlobalVariables.getInstance().setPagesVisited(new ArrayList<>());
        }
    }

    private void logoutAction(final String page, final User currentUser, final List<User> users) {
        saveUserData(page, currentUser, users);
    }

    private void setMoviesToJson(final String page, final User currentUser, final List<Movie>
                                currentMovies, final Output out, final ArrayNode output) {
        if (page.equals("authenticated page")) {
            GlobalVariables.getInstance().setCurrentPage("authenticated page");
        }

        if (page.equals("movies")) {
            GlobalVariables.getInstance().setCurrentPage("movies");
            GlobalVariables.getInstance().getPagesVisited().add("movies");
            makeOutput(null, currentUser, currentMovies, out, output);
        }
    }

    private static void makeOutput(final String error, final User currentUser,
                                   final List<Movie> foundMovies, final Output out,
                                   final ArrayNode output) {
        out.setCurrentUser(currentUser);
        out.setError(error);
        out.setCurrentMoviesList(foundMovies);
        out.objectToJson(output);
    }
}
