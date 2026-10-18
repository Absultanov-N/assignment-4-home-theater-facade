package hometheater.subsystem;

import java.util.Objects;

public class DvdPlayer {
    public void on() {
        System.out.println("DVD player on");
    }

    public void off() {
        System.out.println("DVD player off");
    }

    public void play(String movie) {
        Objects.requireNonNull(movie, "movie must not be null");
        if (movie.isBlank()) {
            throw new IllegalArgumentException("movie must not be blank");
        }
        System.out.println("DVD player playing \"" + movie + "\"");
    }

    public void stop() {
        System.out.println("DVD player stopped");
    }

    public void eject() {
        System.out.println("DVD player ejecting disc");
    }
}
