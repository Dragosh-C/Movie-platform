package implementation;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter

public class Movie {
    private String name;
    private int year;
    private int duration;
    private List<String> genres;
    private List<String> actors;
    private List<String> countriesBanned;

    private int numLikes;
    private double rating;
    private int numRating;
    private double sumRating;


    public Movie() {

    }

    public Movie(final Movie movie) {
        this.name = movie.getName();
        this.year = movie.getYear();
        this.duration = movie.getDuration();
        this.genres = new ArrayList<>(movie.getGenres());
        this.actors = new ArrayList<>(movie.getActors());
        this.countriesBanned = new ArrayList<>(movie.countriesBanned);
        this.numLikes = movie.getNumLikes();
        this.rating = movie.getRating();
        this.numRating = movie.getNumRating();
        this.sumRating = movie.getSumRating();
    }

    public static class Builder {
        private final String name;
        private final int year;
        private final int duration;
        private final List<String> genres;
        private final List<String> actors;
        private final List<String> countriesBanned;
        private final int numLikes;
        private final double rating;
        private final int numRating;
        private double sumRating = 0.0;

        public Builder(final String name,final int year, final int duration,
                       final List<String> genres, final List<String> actors,
                       final List<String> countriesBanned, final int numLikes,
                       final double rating, final int numRating) {
            this.name = name;
            this.year = year;
            this.duration = duration;
            this.genres = genres;
            this.actors = actors;
            this.countriesBanned = countriesBanned;
            this.numLikes = numLikes;
            this.rating = rating;
            this.numRating = numRating;
        }

        public Builder sumRating(double sumRating) {
            this.sumRating = sumRating;
            return this;
        }

        public Movie build() {
            return new Movie(this);
        }
    }

    private Movie(Builder builder) {
        this.name = builder.name;
        this.year = builder.year;
        this.duration = builder.duration;
        this.genres = builder.genres;
        this.actors = builder.actors;
        this.countriesBanned = builder.countriesBanned;
        this.numLikes = builder.numLikes;
        this.rating = builder.rating;
        this.numRating = builder.numRating;
    }
}
