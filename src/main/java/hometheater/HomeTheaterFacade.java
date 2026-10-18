package hometheater;

import hometheater.subsystem.Amplifier;
import hometheater.subsystem.DvdPlayer;
import hometheater.subsystem.PopcornPopper;
import hometheater.subsystem.Projector;
import hometheater.subsystem.Screen;
import hometheater.subsystem.TheaterLights;

import java.util.Objects;

public class HomeTheaterFacade {
    private static final int MOVIE_LIGHT_LEVEL = 10;
    private static final int MOVIE_VOLUME = 50;

    private final Amplifier amplifier;
    private final DvdPlayer dvdPlayer;
    private final Projector projector;
    private final Screen screen;
    private final TheaterLights theaterLights;
    private final PopcornPopper popcornPopper;

    public HomeTheaterFacade(
            Amplifier amplifier,
            DvdPlayer dvdPlayer,
            Projector projector,
            Screen screen,
            TheaterLights theaterLights,
            PopcornPopper popcornPopper) {
        this.amplifier = Objects.requireNonNull(amplifier, "amplifier must not be null");
        this.dvdPlayer = Objects.requireNonNull(dvdPlayer, "dvdPlayer must not be null");
        this.projector = Objects.requireNonNull(projector, "projector must not be null");
        this.screen = Objects.requireNonNull(screen, "screen must not be null");
        this.theaterLights =
                Objects.requireNonNull(theaterLights, "theaterLights must not be null");
        this.popcornPopper =
                Objects.requireNonNull(popcornPopper, "popcornPopper must not be null");
    }

    public void watchMovie(String movie) {
        validateMovieName(movie);

        boolean popcornPopperStarted = false;
        boolean theaterLightsDimmed = false;
        boolean screenLowered = false;
        boolean projectorStarted = false;
        boolean amplifierStarted = false;
        boolean dvdPlayerStarted = false;

        try {
            popcornPopper.on();
            popcornPopperStarted = true;
            popcornPopper.pop();
            theaterLights.dim(MOVIE_LIGHT_LEVEL);
            theaterLightsDimmed = true;
            screen.down();
            screenLowered = true;
            projector.on();
            projectorStarted = true;
            projector.wideScreenMode();
            amplifier.on();
            amplifierStarted = true;
            amplifier.setDvd(dvdPlayer);
            amplifier.setVolume(MOVIE_VOLUME);
            amplifier.surroundSoundOn();
            dvdPlayer.on();
            dvdPlayerStarted = true;
            dvdPlayer.play(movie);
        } catch (RuntimeException startupFailure) {
            cleanupAfterFailedStartup(
                    startupFailure,
                    popcornPopperStarted,
                    theaterLightsDimmed,
                    screenLowered,
                    projectorStarted,
                    amplifierStarted,
                    dvdPlayerStarted);
            throw startupFailure;
        }
    }

    public void endMovie() {
        dvdPlayer.stop();
        dvdPlayer.eject();
        dvdPlayer.off();
        amplifier.off();
        projector.off();
        screen.up();
        theaterLights.on();
        popcornPopper.off();
    }

    private void cleanupAfterFailedStartup(
            RuntimeException startupFailure,
            boolean popcornPopperStarted,
            boolean theaterLightsDimmed,
            boolean screenLowered,
            boolean projectorStarted,
            boolean amplifierStarted,
            boolean dvdPlayerStarted) {
        if (dvdPlayerStarted) {
            cleanupStep(startupFailure, dvdPlayer::stop);
            cleanupStep(startupFailure, dvdPlayer::eject);
            cleanupStep(startupFailure, dvdPlayer::off);
        }
        if (amplifierStarted) {
            cleanupStep(startupFailure, amplifier::off);
        }
        if (projectorStarted) {
            cleanupStep(startupFailure, projector::off);
        }
        if (screenLowered) {
            cleanupStep(startupFailure, screen::up);
        }
        if (theaterLightsDimmed) {
            cleanupStep(startupFailure, theaterLights::on);
        }
        if (popcornPopperStarted) {
            cleanupStep(startupFailure, popcornPopper::off);
        }
    }

    private void cleanupStep(RuntimeException startupFailure, Runnable cleanupOperation) {
        try {
            cleanupOperation.run();
        } catch (RuntimeException cleanupFailure) {
            startupFailure.addSuppressed(cleanupFailure);
        }
    }

    private void validateMovieName(String movie) {
        Objects.requireNonNull(movie, "movie must not be null");
        if (movie.isBlank()) {
            throw new IllegalArgumentException("movie must not be blank");
        }
    }
}
