package hometheater;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        HomeTheaterFacade theater = HomeTheaterSetup.create();
        theater.watchMovie("Inception");
        theater.endMovie();
    }
}
