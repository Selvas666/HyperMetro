package metro.ui;

import io.vavr.collection.List;
import io.vavr.control.Try;
import metro.model.CommandWord;
import metro.model.MetroLine;
import metro.utils.Parser;
import metro.utils.RouteFinder;

import java.util.Scanner;

public class UserInterface {

    private final List<MetroLine> metroLineList;

    public UserInterface(String[] args) {
        this.metroLineList = Parser.parse(args);
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            if (scanner.hasNext()) {
                Try<Command> currentCommand = Command.parseCommand(scanner.nextLine());
                if (currentCommand.isSuccess()) {
                    if (currentCommand.get().getCommandWord().equals(CommandWord.EXIT)) {
                        System.out.println("---GOODBYE!---");
                        break;
                    }
                    execute(currentCommand.get());
                } else {
                    System.out.println("Invalid command.");
                }
            }
        }
    }

    private void execute(Command command) {
        switch (command.getCommandWord()) {
            case OUTPUT:
                output(command);
                break;
            case APPEND:
                append(command);
                break;
            case ADD_HEAD:
                addHead(command);
                break;
            case REMOVE:
                remove(command);
                break;
            case CONNECT:
                connect (command);
                break;
            case ROUTE:
                route(command);
                break;

        }
    }

    private void route (Command command){
        System.out.println(RouteFinder.findRoute(metroLineList, command.getParameters().get(0), command.getParameters().get(1), command.getParameters().get(2), command.getParameters().get(3)));
    }

    private void output (Command command){
        metroLineList
                .find(n -> n.getName().equals(command.getParameters().get(0)))
                .forEach(MetroLine::print);
    }

    private void append (Command command){
        metroLineList
                .filter(n -> n.getName().equals(command.getParameters().get(0)))
                .forEach(n -> n.append(command.getParameters()));
    }

    private void addHead (Command command) {
        metroLineList
                .filter(n -> n.getName().equals(command.getParameters().get(0)))
                .forEach(n -> n.addHead(command.getParameters()));
    }

    private void remove (Command command){
        metroLineList
                .filter(n -> n.getName().equals(command.getParameters().get(0)))
                .forEach(n -> n.remove(command.getParameters()));
    }

    private void connect (Command command){
        metroLineList.filter(n -> n.getName().equalsIgnoreCase(command.getParameters().get(0))
                || n.getName().equalsIgnoreCase(command.getParameters().get(2)))
                .forEach(n -> n.connect(command.getParameters()));
    }

}
