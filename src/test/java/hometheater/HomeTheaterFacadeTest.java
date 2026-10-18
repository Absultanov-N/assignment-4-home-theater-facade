import hometheater.HomeTheaterFacade;
import hometheater.subsystem.Amplifier;
import hometheater.subsystem.DvdPlayer;
import hometheater.subsystem.PopcornPopper;
import hometheater.subsystem.Projector;
import hometheater.subsystem.Screen;
import hometheater.subsystem.TheaterLights;

import java.util.ArrayList;
import java.util.List;

public final class HomeTheaterFacadeTest {
    public static void main(String[] args) {
        watchMovieCoordinatesSubsystemsInOrder();
        endMovieCoordinatesSubsystemsInOrder();
        invalidMovieNamesDoNotStartSubsystems();
        subsystemNumericValuesAreValidated();
        failedStartupCleansUpOnlyStartedDevices();
        cleanupFailureDoesNotReplaceStartupFailure();
        System.out.println("All HomeTheaterFacade tests passed");
    }

    private static void watchMovieCoordinatesSubsystemsInOrder() {
        EventLog events = new EventLog();
        HomeTheaterFacade facade = createFacade(events);

        facade.watchMovie("Inception");

        events.assertEquals(
                "popcorn.on", "popcorn.pop", "lights.dim(10)", "screen.down",
                "projector.on", "projector.wideScreenMode", "amplifier.on",
                "amplifier.setDvd", "amplifier.setVolume(50)",
                "amplifier.surroundSoundOn", "dvd.on", "dvd.play(Inception)");
    }

    private static void endMovieCoordinatesSubsystemsInOrder() {
        EventLog events = new EventLog();
        HomeTheaterFacade facade = createFacade(events);

        facade.endMovie();

        events.assertEquals(
                "dvd.stop", "dvd.eject", "dvd.off", "amplifier.off",
                "projector.off", "screen.up", "lights.on", "popcorn.off");
    }

    private static void invalidMovieNamesDoNotStartSubsystems() {
        EventLog events = new EventLog();
        HomeTheaterFacade facade = createFacade(events);

        expectThrows(NullPointerException.class, () -> facade.watchMovie(null));
        assertEmpty(events);
        expectThrows(IllegalArgumentException.class, () -> facade.watchMovie(" \t"));
        assertEmpty(events);
    }

    private static void subsystemNumericValuesAreValidated() {
        Amplifier amplifier = new Amplifier();
        TheaterLights lights = new TheaterLights();

        amplifier.setVolume(0);
        amplifier.setVolume(100);
        lights.dim(0);
        lights.dim(100);
        expectThrows(IllegalArgumentException.class, () -> amplifier.setVolume(-1));
        expectThrows(IllegalArgumentException.class, () -> amplifier.setVolume(101));
        expectThrows(IllegalArgumentException.class, () -> lights.dim(-1));
        expectThrows(IllegalArgumentException.class, () -> lights.dim(101));
    }

    private static void failedStartupCleansUpOnlyStartedDevices() {
        EventLog events = new EventLog();
        FailingPopcornPopper popcorn = new FailingPopcornPopper(events);
        HomeTheaterFacade facade = new HomeTheaterFacade(
                new RecordingAmplifier(events),
                new RecordingDvdPlayer(events),
                new RecordingProjector(events),
                new RecordingScreen(events),
                new RecordingTheaterLights(events),
                popcorn);

        expectThrows(IllegalStateException.class, () -> facade.watchMovie("Inception"));
        events.assertEquals("popcorn.on", "popcorn.pop", "popcorn.off");
    }

    private static void cleanupFailureDoesNotReplaceStartupFailure() {
        EventLog events = new EventLog();
        FailingDvdPlayer dvd = new FailingDvdPlayer(events);
        HomeTheaterFacade facade = new HomeTheaterFacade(
                new RecordingAmplifier(events),
                dvd,
                new RecordingProjector(events),
                new RecordingScreen(events),
                new RecordingTheaterLights(events),
                new RecordingPopcornPopper(events));

        RuntimeException failure = expectThrows(
                IllegalStateException.class, () -> facade.watchMovie("Inception"));
        assertEquals("play failed", failure.getMessage());
        assertEquals(1, failure.getSuppressed().length);
        events.assertEquals(
                "popcorn.on", "popcorn.pop", "lights.dim(10)", "screen.down",
                "projector.on", "projector.wideScreenMode", "amplifier.on",
                "amplifier.setDvd", "amplifier.setVolume(50)",
                "amplifier.surroundSoundOn", "dvd.on", "dvd.play(Inception)",
                "dvd.stop", "dvd.eject", "dvd.off", "amplifier.off",
                "projector.off", "screen.up", "lights.on", "popcorn.off");
    }

    private static HomeTheaterFacade createFacade(EventLog events) {
        return new HomeTheaterFacade(
                new RecordingAmplifier(events),
                new RecordingDvdPlayer(events),
                new RecordingProjector(events),
                new RecordingScreen(events),
                new RecordingTheaterLights(events),
                new RecordingPopcornPopper(events));
    }

    private static void assertEmpty(EventLog events) {
        events.assertEquals();
    }

    private static <T extends RuntimeException> T expectThrows(
            Class<T> expectedType, Runnable action) {
        try {
            action.run();
        } catch (RuntimeException actual) {
            if (!expectedType.isInstance(actual)) {
                throw new AssertionError(
                        "Expected " + expectedType.getSimpleName()
                                + " but got " + actual.getClass().getSimpleName(), actual);
            }
            return expectedType.cast(actual);
        }
        throw new AssertionError("Expected " + expectedType.getSimpleName());
    }

    private static void assertEquals(Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected " + expected + " but got " + actual);
        }
    }

    private static class EventLog {
        private final List<String> events = new ArrayList<>();

        void add(String event) {
            events.add(event);
        }

        void assertEquals(String... expected) {
            List<String> expectedEvents = List.of(expected);
            if (!expectedEvents.equals(events)) {
                throw new AssertionError(
                        "Expected events " + expectedEvents + " but got " + events);
            }
        }
    }

    private static class RecordingAmplifier extends Amplifier {
        private final EventLog events;

        RecordingAmplifier(EventLog events) {
            this.events = events;
        }

        @Override
        public void on() {
            events.add("amplifier.on");
        }

        @Override
        public void off() {
            events.add("amplifier.off");
        }

        @Override
        public void setDvd(DvdPlayer dvdPlayer) {
            events.add("amplifier.setDvd");
        }

        @Override
        public void surroundSoundOn() {
            events.add("amplifier.surroundSoundOn");
        }

        @Override
        public void setVolume(int volume) {
            events.add("amplifier.setVolume(" + volume + ")");
        }
    }

    private static class RecordingDvdPlayer extends DvdPlayer {
        private final EventLog events;

        RecordingDvdPlayer(EventLog events) {
            this.events = events;
        }

        @Override
        public void on() {
            events.add("dvd.on");
        }

        @Override
        public void off() {
            events.add("dvd.off");
        }

        @Override
        public void play(String movie) {
            events.add("dvd.play(" + movie + ")");
        }

        @Override
        public void stop() {
            events.add("dvd.stop");
        }

        @Override
        public void eject() {
            events.add("dvd.eject");
        }
    }

    private static class FailingDvdPlayer extends RecordingDvdPlayer {
        private final EventLog events;

        FailingDvdPlayer(EventLog events) {
            super(events);
            this.events = events;
        }

        @Override
        public void play(String movie) {
            super.play(movie);
            throw new IllegalStateException("play failed");
        }

        @Override
        public void off() {
            events.add("dvd.off");
            throw new IllegalStateException("cleanup failed");
        }
    }

    private static class RecordingProjector extends Projector {
        private final EventLog events;

        RecordingProjector(EventLog events) {
            this.events = events;
        }

        @Override
        public void on() {
            events.add("projector.on");
        }

        @Override
        public void off() {
            events.add("projector.off");
        }

        @Override
        public void wideScreenMode() {
            events.add("projector.wideScreenMode");
        }
    }

    private static class RecordingScreen extends Screen {
        private final EventLog events;

        RecordingScreen(EventLog events) {
            this.events = events;
        }

        @Override
        public void down() {
            events.add("screen.down");
        }

        @Override
        public void up() {
            events.add("screen.up");
        }
    }

    private static class RecordingTheaterLights extends TheaterLights {
        private final EventLog events;

        RecordingTheaterLights(EventLog events) {
            this.events = events;
        }

        @Override
        public void on() {
            events.add("lights.on");
        }

        @Override
        public void dim(int level) {
            events.add("lights.dim(" + level + ")");
        }
    }

    private static class RecordingPopcornPopper extends PopcornPopper {
        private final EventLog events;

        RecordingPopcornPopper(EventLog events) {
            this.events = events;
        }

        @Override
        public void on() {
            events.add("popcorn.on");
        }

        @Override
        public void off() {
            events.add("popcorn.off");
        }

        @Override
        public void pop() {
            events.add("popcorn.pop");
        }
    }

    private static class FailingPopcornPopper extends RecordingPopcornPopper {
        FailingPopcornPopper(EventLog events) {
            super(events);
        }

        @Override
        public void pop() {
            super.pop();
            throw new IllegalStateException("pop failed");
        }
    }
}
