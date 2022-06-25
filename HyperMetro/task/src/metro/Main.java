package metro;

import metro.ui.UserInterface;

public class Main {
    public static void main(String[] args) {
        UserInterface userInterface = new UserInterface(args);
        userInterface.start();
    }
}
