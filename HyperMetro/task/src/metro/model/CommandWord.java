package metro.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum CommandWord {
    APPEND ("/append"),
    ADD_HEAD ("/add-head"),
    REMOVE ("/remove"),
    OUTPUT ("/output"),
    CONNECT ("/connect"),
    ROUTE ("/route"),
    EXIT ("/exit");

    private final String input;

    public static CommandWord parseCommand(String userInput) {
        return Arrays.stream(CommandWord.values())
                .filter(v -> v.getInput().equalsIgnoreCase(userInput))
                .findAny()
                .orElseThrow();
    }

}
