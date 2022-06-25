package metro.ui;

import io.vavr.collection.List;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.Getter;
import metro.model.CommandWord;

@Getter
@AllArgsConstructor
public class Command {
    private final CommandWord commandWord;
    private final List<String> parameters;

    public static Try<Command> parseCommand(String userInput) {
        String[] splitUserInput = userInput.split("\\s");

        if (splitUserInput.length == 1) {
            return Try.success(new Command(CommandWord.parseCommand(splitUserInput[0]), List.empty()));
        } else {
            try {
                CommandWord commandWord = CommandWord.parseCommand(splitUserInput[0]);
                List<String> paramList = parseParams(splitUserInput);
                return Try.success(new Command(commandWord, paramList));
            } catch (Exception ex) {
                return Try.failure(ex);
            }
        }

    }

    private static List<String> parseParams(String[] splitUserInput) {
        StringBuilder param = new StringBuilder();
        List<String> paramList = List.empty();
        boolean withinQuotes = false;

        for (int i = 1; i < splitUserInput.length; i++) {
            if (splitUserInput[i].startsWith("\"") && splitUserInput[i].endsWith("\"")){
                param.append(splitUserInput[i]);
                paramList = paramList.append(removeSorrundingQuotes(param.toString().trim()));
                param = new StringBuilder();
            }
            else if (splitUserInput[i].startsWith("\"")) {
                param.append(splitUserInput[i]);
                param.append(" ");
                withinQuotes = true;
            } else if (splitUserInput[i].endsWith("\"")) {
                param.append(splitUserInput[i]);
                withinQuotes = false;
                paramList = paramList.append(removeSorrundingQuotes(param.toString().trim()));
                param = new StringBuilder();
            } else if (withinQuotes) {
                param.append(splitUserInput[i]);
                param.append(" ");
            } else {
                param.append(splitUserInput[i]);
                paramList = paramList.append(removeSorrundingQuotes(param.toString().trim()));
                param = new StringBuilder();
            }
        }
        return paramList;
    }

    private static String removeSorrundingQuotes (String str) {
        if (str.startsWith("\"") && str.endsWith("\"")){
            return str.substring(1, str.length()-1);
        }
        return str;
    }

}
