package hometheater.subsystem;

public class TheaterLights {
    private static final int MIN_LEVEL = 0;
    private static final int MAX_LEVEL = 100;

    public void on() {
        System.out.println("Theater lights on");
    }

    public void dim(int level) {
        if (level < MIN_LEVEL || level > MAX_LEVEL) {
            throw new IllegalArgumentException(
                    "light level must be between " + MIN_LEVEL + " and " + MAX_LEVEL);
        }
        System.out.println("Theater lights dimmed to " + level + "%");
    }
}
