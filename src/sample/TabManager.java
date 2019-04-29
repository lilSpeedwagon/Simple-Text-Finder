package sample;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TabManager {
    private static Logger logger = Logger.getLogger(TextFinder.class.getName());

    private TabPane pane;
    private HashMap<File, Tab> openedFileTabs = new HashMap<>();

    public TabManager() {}
    public TabManager(TabPane pane) {
        this.pane = pane;
    }

    private void addToAreaFromFile(TextArea area, File sourceFile)    {
        try {
            FileReader reader = new FileReader(sourceFile);
            Scanner scanner = new Scanner(reader);

            while (scanner.hasNextLine())   {
                area.appendText(scanner.nextLine());
                area.appendText("\n");
            }

            scanner.close();
            reader.close();
        } catch (FileNotFoundException e)   {
            logger.log(Level.WARNING, "Error. File " + sourceFile.getName() + "not found.");
        } catch (IOException e) {
            logger.log(Level.WARNING, "IO error.");
        }
    }

    public void addTab(SearchResult result)  {
        try {
            if (!result.isEmpty())  {
                File file = result.getFile();
                logger.info("Opening tab for file " + file.getName());

                if (!openedFileTabs.containsKey(file)) {
                    Tab newTab = new Tab(file.getName());
                    TextArea textArea = new TextArea();
                    textArea.setEditable(false);

                    addToAreaFromFile(textArea, file);

                    newTab.setContent(textArea);
                    newTab.setOnClosed(new EventHandler<Event>() {
                        @Override
                        public void handle(Event event) {
                            openedFileTabs.remove(file);
                        }
                    });

                    openedFileTabs.put(file, newTab);
                    pane.getTabs().add(newTab);
                }
            }   else    {
                logger.log(Level.WARNING, "Error. SearchResult is empty.");
            }
        } catch (NullPointerException e)    {
            logger.log(Level.WARNING, "Error. SearchResult is null.");
        }
    }

    public void closeTab()   {

    }
}
