package hometheater;

import hometheater.subsystem.Amplifier;
import hometheater.subsystem.DvdPlayer;
import hometheater.subsystem.PopcornPopper;
import hometheater.subsystem.Projector;
import hometheater.subsystem.Screen;
import hometheater.subsystem.TheaterLights;

public final class HomeTheaterSetup {
    private HomeTheaterSetup() {
    }

    public static HomeTheaterFacade create() {
        Amplifier amplifier = new Amplifier();
        DvdPlayer dvdPlayer = new DvdPlayer();
        Projector projector = new Projector();
        Screen screen = new Screen();
        TheaterLights theaterLights = new TheaterLights();
        PopcornPopper popcornPopper = new PopcornPopper();

        return new HomeTheaterFacade(
                amplifier, dvdPlayer, projector, screen, theaterLights, popcornPopper);
    }
}
