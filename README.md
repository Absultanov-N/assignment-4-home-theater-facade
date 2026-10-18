# Home Theater Facade

## Project Overview

This Java 17 project demonstrates the **Facade design pattern** through a home theater system. The example coordinates several independent devices so that a client can start and finish a movie with a small number of simple operations.

## Design Pattern

The Facade pattern provides a simplified interface to a group of subsystem classes. `HomeTheaterFacade` hides the detailed order of operations required by the home theater devices. The client can use `watchMovie()` and `endMovie()` instead of coordinating each device directly.

## Project Structure

The source code uses the `hometheater` package:

- `hometheater.Main` is the application entry point.
- `hometheater.HomeTheaterFacade` coordinates the home theater subsystems.
- `hometheater.HomeTheaterSetup` creates the subsystem objects and returns a configured facade.
- `hometheater.subsystem` contains `Amplifier`, `DvdPlayer`, `Projector`, `Screen`, `TheaterLights`, and `PopcornPopper`.
- `src/test/java/hometheater/HomeTheaterFacadeTest.java` contains the standalone test runner and supporting recording test doubles.

## Subsystems

- **Amplifier** turns the amplifier on or off, selects the DVD input, enables surround sound, and sets the volume.
- **DvdPlayer** turns the DVD player on or off, plays a movie, stops playback, and ejects the disc.
- **Projector** turns the projector on or off and enables widescreen mode.
- **Screen** lowers and raises the screen.
- **TheaterLights** turns the theater lights on and dims them to a specified level.
- **PopcornPopper** turns the popcorn popper on or off and starts popping popcorn.

## How It Works

`HomeTheaterSetup.create()` constructs all six subsystem objects and passes them to a new `HomeTheaterFacade`. In `Main`, the client obtains the facade, calls `watchMovie("Inception")`, and then calls `endMovie()`.

During `watchMovie()`, the facade starts the popcorn popper, dims the lights, lowers the screen, configures the projector and amplifier, starts the DVD player, and begins playback. `endMovie()` stops and ejects the DVD, turns off the amplifier and projector, raises the screen, restores the lights, and turns off the popcorn popper.

## Requirements

- Java Development Kit (JDK) 17
- IntelliJ IDEA

## How to Run

1. Open the project root in IntelliJ IDEA.
2. Ensure the project SDK is set to JDK 17.
3. Open `src/main/java/hometheater/Main.java`.
4. Run `hometheater.Main` using the run icon beside the `main` method or by right-clicking the file and selecting **Run**.

To compile and run from a terminal without a build tool, use commands equivalent to:

```powershell
Remove-Item -Recurse -Force out -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force out | Out-Null
javac -d out (Get-ChildItem -Recurse -File src\main\java -Filter *.java).FullName
java -cp out hometheater.Main
```

## Testing

The project does not use JUnit, Maven, or Gradle. `HomeTheaterFacadeTest` is a plain Java class with a `main` method. It performs assertion-based checks for subsystem ordering, movie-name validation, numeric validation, and cleanup after startup failures.

In IntelliJ IDEA, open `src/test/java/hometheater/HomeTheaterFacadeTest.java` and run the class. From a terminal, compile the main and test sources and then run the test class:

```powershell
Remove-Item -Recurse -Force out -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force out | Out-Null
javac -d out (Get-ChildItem -Recurse -File src\main\java -Filter *.java).FullName (Get-ChildItem -Recurse -File src\test\java -Filter *.java).FullName
java -cp out hometheater.HomeTheaterFacadeTest
```

The test runner reports success with `All HomeTheaterFacade tests passed`.

## UML Diagram

No UML diagram image or UML source file is present in the project.

## Example Output

Running `hometheater.Main` produces output such as:

```text
Popcorn popper on
Popcorn popping
Theater lights dimmed to 10%
Projector widescreen mode enabled
DVD player playing "Inception"
DVD player stopped
Theater lights on
Popcorn popper off
```
