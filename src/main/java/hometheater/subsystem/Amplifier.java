package hometheater.subsystem;

import java.util.Objects;

public class Amplifier {
    private static final int MIN_VOLUME = 0;
    private static final int MAX_VOLUME = 100;

    public void on() {
        System.out.println("Amplifier on");
    }

    public void off() {
        System.out.println("Amplifier off");
    }

    public void setDvd(DvdPlayer dvdPlayer) {
        Objects.requireNonNull(dvdPlayer, "dvdPlayer must not be null");
        System.out.println("Amplifier input set to DVD player");
    }

    public void surroundSoundOn() {
        System.out.println("Amplifier surround sound on");
    }

    public void setVolume(int volume) {
        validateLevel(volume, "volume");
        System.out.println("Amplifier volume set to " + volume);
    }

    private void validateLevel(int level, String name) {
        if (level < MIN_VOLUME || level > MAX_VOLUME) {
            throw new IllegalArgumentException(
                    name + " must be between " + MIN_VOLUME + " and " + MAX_VOLUME);
        }
    }
}
