import Tasks.Deadline;
import Tasks.Event;
import Tasks.Task;
import Tasks.ToDo;
import YapchitExceptions.InvalidDetailException;
import YapchitExceptions.InvalidKeywordException;
import YapchitExceptions.YapchitException;

import java.time.LocalDate;

public class Handler {

    public Handler(){}

    public void handleOperation(
            String input,
            Yapchit.Operations op,
            TaskList tasks,
            Ui ui,
            Parser parser) throws YapchitException {
        String[] parts = parser.parseInputParts(input);

        switch(op){
            case LIST:
                handleList(parts, tasks, ui);
                break;

            case MARK:
                handleMark(parts, tasks, ui);
                break;

            case UNMARK:
                handleUnmark(parts, tasks, ui);
                break;

            case DELETE:
                handleDelete(parts, tasks, ui);
                break;

            case DEADLINE:
                handleDeadline(input, true, tasks, ui, parser);
                break;

            case EVENT:
                handleEvent(input, true, tasks, ui);
                break;

            case TODO:
                handleTodo(input, true, tasks, ui);
                break;

            default:
                throw new InvalidKeywordException("You have entered an invalid keyword. " +
                        "Valid keywords are ['mark', 'unmark', 'deadline', 'todo', 'event', 'bye', 'list', 'delete']");
        }
    }

    private void handleEvent(String input, boolean newTask, TaskList tasks, Ui ui) throws InvalidDetailException {
        int fromStart = input.indexOf("/from");
        int toStart = input.indexOf("/to");

        char done = '0';

        if(!newTask){
            done = input.charAt(input.length() - 1);
            input = input.substring(0, input.length() - 1);
        }

        if(fromStart == -1 || toStart == -1 || fromStart >= toStart){
            throw new InvalidDetailException("invalid /from and /to parameters. Please retry");
        } else {
            if(6 == fromStart || fromStart + 6 == toStart || toStart + 4 >= input.length()){
                throw new InvalidDetailException("Tasks.Event description and/or to/from parameters cannot be empty");
            }
            String desc = input.substring(6, fromStart).strip();
            String from = input.substring(fromStart + 6, toStart).strip();
            String to = input.substring(toStart + 4).strip();

            if(desc.length() == 0 || from.length() == 0 || to.length() == 0){
                throw new InvalidDetailException("Tasks.Event description and/or to/from parameters cannot be empty");
            } else {
                Task t = new Event(desc, from, to);
                tasks.addTask(t);
                if(newTask){
                    ui.printTaskAdd(t, tasks.getListSize());
                } else {
                    t.updateTag(done == '1' ? true : false);
                }
            }
        }
    }

    private void handleDeadline(String input, boolean newTask, TaskList tasks, Ui ui, Parser parser) throws InvalidDetailException{

        int byStart = input.indexOf("/by");

        char done = '0';

        if(!newTask){
            done = input.charAt(input.length() - 1);
            input = input.substring(0, input.length() - 1);
        }

        if(byStart == -1){
            throw new InvalidDetailException("Missing 'by' parameter in deadline detail");
        } else {
            if(9 == byStart || byStart + 4 >= input.length()){
                throw new InvalidDetailException("Tasks.Deadline description and/or by parameter cannot be empty");
            }
            String desc = input.substring(9, byStart).strip();
            LocalDate by = parser.parseTimestamp(input.substring(byStart + 4).strip());

            if(desc.length() == 0 || by == null){
                throw new InvalidDetailException("Invalid or empty Tasks.Deadline description and/or by parameter");
            } else {
                Task t = new Deadline(desc, by);
                tasks.addTask(t);
                if(newTask){
                    ui.printTaskAdd(t, tasks.getListSize());
                } else {
                    t.updateTag(done == '1' ? true : false);
                }
            }
        }
    }

    private void handleTodo(String input, boolean newTask, TaskList tasks, Ui ui) throws  InvalidDetailException{

        char done = '0';

        if(!newTask){
            done = input.charAt(input.length() - 1);
            input = input.substring(0, input.length() - 1);
        }

        if(5 >= input.length()){
            throw new InvalidDetailException("todo description cannot be an empty string. Please retry");
        }
        String desc = input.substring(5).strip();

        if(desc.length() == 0){
            throw new InvalidDetailException("todo description cannot be an empty string. Please retry");
        } else {
            Task t = new ToDo(desc);
            tasks.addTask(t);

            if(newTask){
                ui.printTaskAdd(t, tasks.getListSize());
            } else {
                t.updateTag(done == '1' ? true : false);
            }
        }
    }

    private void handleList(String[] parts, TaskList tasks, Ui ui) throws InvalidDetailException{
        if(parts.length != 1){
            throw new InvalidDetailException("Invalid detail after keyword. Please retry");
        } else {
            ui.printList(tasks);
        }
    }

    private void handleDelete(String[] parts, TaskList tasks, Ui ui) throws InvalidDetailException{
        if(parts.length != 2){
            throw new InvalidDetailException("Invalid detail after delete. Please retry");
        } else {
            try {
                int num = Integer.parseInt(parts[1]);
                tasks.delete(num - 1);
                ui.printTaskDelete(tasks.getItem(num - 1), tasks.getListSize());
            } catch (Exception e){
                throw new InvalidDetailException("Invalid detail after delete. Please retry");
            }
        }
    }

    private void handleMark(String[] parts, TaskList tasks, Ui ui) throws InvalidDetailException{
        if(parts.length != 2){
            throw new InvalidDetailException("Invalid detail after mark. Please retry");
        } else {
            try {
                int idx = Integer.parseInt(parts[1]);
                tasks.mark(idx - 1, true);
                ui.printTaskMark(tasks.getItem(idx - 1), true);
            } catch (Exception e){
                throw new InvalidDetailException("Invalid detail after mark. Please retry");
            }
        }
    }

    private void handleUnmark(String[] parts, TaskList tasks, Ui ui) throws InvalidDetailException{
        if(parts.length != 2){
            throw new InvalidDetailException("Invalid detail after unmark. Please retry");
        } else {
            try {
                int idx = Integer.parseInt(parts[1]);
                tasks.mark(idx - 1, false);
                ui.printTaskMark(tasks.getItem(idx - 1), false);
            } catch (Exception e){
                throw new InvalidDetailException("Invalid detail after unmark. Please retry");
            }
        }
    }
}
