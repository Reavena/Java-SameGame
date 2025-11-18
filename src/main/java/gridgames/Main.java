package gridgames;

import java.util.Scanner;

/**
 * A small class to try the {@link gridgames} package.
 */
public class Main {

    /**
     * Method to try the {@link gridgames} package.
     * 
     * @param args Additionnal arguments as a {@code String[]}. Are not processed.
     */
    public static void main(String[] args) {

        int choice;
        // Please choose any option to try the package!
        Scanner scanner = new Scanner(System.in);
        
        while (true) {

            // NOTE: this is not part of any application.
            // This dialog is just handy to try different configurations
            // but is not part of the gridgames package.
            System.out.print("\033[H\033[2J");
            System.out.println("Game Modes for Demonstration:");
            System.out.println("1. Mouse + GUI");
            System.out.println("2. Mouse + GUI + Console");
            System.out.println("3. CLI + Console");
            System.out.println("4. CLI + GUI");
            System.out.println("5. CLI + GUI + Console");
            
            String input = scanner.next().trim().toLowerCase(); 
            try {
                choice = Integer.parseInt(input);
                if (choice < 1 || choice > 5)
                    continue;
                break;
            } catch (NumberFormatException e) {
                continue;
            }

        }

        // Creates a SameGame instance.
        SameGame game = new SameGame();

        // Creates a GameController.
        GameController controller = new GameController(game);

        // Common setup.
        SoundPlayer soundPlayer = new SoundPlayer();
        FileManager fileManager = new FileManager();
        fileManager.setController(controller);
        game.addListener(soundPlayer);
        game.addListener(fileManager);

        switch (choice) {

            // Mouse + GUI
            case 1:

                GraphicalView graphicalView = new GraphicalView();
                controller.setInput(new MouseInput());
                graphicalView.setController(controller);
                game.addListener(graphicalView);

                graphicalView.setVisible(true);

                break;

            // Mouse + GUI + Console
            case 2:

                graphicalView = new GraphicalView();
                controller.setInput(new MouseInput());
                graphicalView.setController(controller);
                game.addListener(graphicalView);

                ConsoleView consoleView = new ConsoleView();
                consoleView.setController(controller);          
                game.addListener(consoleView);

                graphicalView.setVisible(true);

                break;

            // CLI + Console
            case 3:

                consoleView = new ConsoleView();
                consoleView.setController(controller);          
                game.addListener(consoleView);

                break;

            // CLI + GUI
            case 4:

                graphicalView = new GraphicalView();
                graphicalView.setController(controller);
                game.addListener(graphicalView);

                graphicalView.setVisible(true);

                break;

            // CLI + GUI + Console
            case 5:
                graphicalView = new GraphicalView();
                graphicalView.setController(controller);
                game.addListener(graphicalView);

                consoleView = new ConsoleView();
                consoleView.setController(controller);          
                game.addListener(consoleView);

                graphicalView.setVisible(true);

                break;


            default:
                break;

        }

        // Starts the GameController.
        controller.run();

    }
}