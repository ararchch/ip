package Yapchit;

import Yapchit.Tasks.Deadline;
import Yapchit.Tasks.Event;
import Yapchit.Tasks.Task;
import Yapchit.Tasks.ToDo;
import Yapchit.YapchitExceptions.FileListParseException;
import Yapchit.YapchitExceptions.YapchitException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Class that handles interactions with the external storage file that maintains the tasks outside
 * the yapchit program.
 */
public class Storage {

    private String filePath;

    /**
     * Creates new storage object.
     *
     * @param filePath FilePath of the external file used to store tasks.
     */
    public Storage(String filePath){
        this.filePath = filePath;
    }

    /**
     * Imports existing tasks from file at specified filepath and returns list as a TaskList.
     *
     * @param filePath Path where data is stored.
     * @param ui User interface object to handle outputs
     * @param handler Handler object to handle tasks.
     * @param parser Parser object to parse input
     * @return TaskList containing all tasks in file
     * @throws YapchitException if file is not found or corrupted
     */
    public TaskList importFromFile(String filePath, Ui ui, Handler handler, Parser parser) throws YapchitException {
        File f = new File(filePath);
        Scanner s;
        try {
            s = new Scanner(f);
        } catch (FileNotFoundException e){
            throw new FileListParseException("Could not locate existing file list");
        }
        TaskList tasks = new TaskList();

        while (s.hasNext()) {
            String input = s.nextLine();
            String[] parts = parser.parseInputParts(input);

            try {
                Yapchit.Operations k = Yapchit.Operations.valueOf(parts[0].toUpperCase());
                handler.handleUpdateListFromFile(input, k, tasks, ui, parser);
            } catch (YapchitException e){
                throw new FileListParseException("Error in parsing file. Some of the contents may be corrupted");
            }
        }

        return tasks;
    }

    /**
     * Updates file at specified path with list of tasks for permanent storage
     *
     * @param filePath Path of file to update
     * @param tasks List of tasks to update the file with
     */
    public void updateFile(String filePath, TaskList tasks){
        String toWrite = "";
        for(int i = 0; i < tasks.getListSize(); i++){
            Task t = tasks.getItem(i);
            if(t instanceof ToDo){
                toWrite = toWrite + "todo "+ t.getName() + (t.getTag() == true ? "1" : "0") + "\n";
            }

            if(t instanceof Event){
                toWrite = toWrite
                        + "event "+ t.getName()
                        + " /from " + ((Event) t).getFrom()
                        + " /to " + ((Event) t).getTo()
                        +(t.getTag() == true ? "1" : "0") + "\n";
            }

            if(t instanceof Deadline){
                toWrite = toWrite
                        + "deadline "+ t.getName()
                        + " /by " + ((Deadline) t).getBy()
                        +(t.getTag() == true ? "1" : "0") + "\n";
            }
        }
        File f = new File(filePath);


        File dirCheck = f.getParentFile();
        if(!dirCheck.exists()){
            dirCheck.mkdirs();
        }

        try {
            if (!f.exists()) {
                f.createNewFile();
            }
        } catch (IOException e){
            Ui.print("Error in creating file. " + e.getMessage());
        }

        try{
            this.writeToFile(filePath, toWrite);
        } catch (IOException e){
            Ui.print(e.getMessage());
        }

    }

    private static void writeToFile(String filePath, String textToAdd) throws IOException {
        FileWriter fw = new FileWriter(filePath);
        fw.write(textToAdd);
        fw.close();
    }
}
