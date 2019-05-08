package sample;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TabManager {
    private static Logger logger = Logger.getLogger(TextFinder.class.getName());

    private TabPane pane;
    private Label fileInfo;
    private HashMap<SearchResult, Tab> openedFileTabs = new HashMap<>();

    private SearchResult currentResult;
    private int currentMatching = 0;
    private int totalMatching;

    public TabManager(TabPane pane, Label fileInfo) {
        this.pane = pane;
        this.fileInfo = fileInfo;
    }

    //fill the text area with file content
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

    //add new tab from resulting file
    public void addTab(SearchResult result)  {
        try {
            if (!result.isEmpty())  {
                File file = result.getFile();
                logger.info("Opening tab for file " + file.getName());

                if (!openedFileTabs.containsKey(result)) {
                    Tab newTab = new Tab(file.getName());
                    TextArea textArea = new TextArea();
                    textArea.setEditable(false);

                    addToAreaFromFile(textArea, file);

                    newTab.setContent(textArea);
                    newTab.setOnClosed(new EventHandler<Event>() {
                        @Override
                        public void handle(Event event) {
                            openedFileTabs.remove(result);
                            if (openedFileTabs.isEmpty())   {
                                fileInfo.setText("No file selected");
                            }
                        }
                    });
                    newTab.setOnSelectionChanged(new EventHandler<Event>() {
                        @Override
                        public void handle(Event event) {
                            if (newTab.isSelected())
                            for (SearchResult result : openedFileTabs.keySet())   {
                                if (openedFileTabs.get(result).equals(newTab)) {
                                    currentMatching = 0;
                                    totalMatching = result.size();
                                    currentResult = result;
                                    moveMatchingCursor(0);
                                    showFileInfo(result.getFile().getName(), currentMatching, totalMatching);
                                    break;
                                }
                            }

                        }
                    });
                    pane.getSelectionModel().select(newTab);

                    openedFileTabs.put(result, newTab);
                    pane.getTabs().add(newTab);
                }   else    {
                    pane.getSelectionModel().select(openedFileTabs.get(result));
                }
            }   else    {
                logger.log(Level.WARNING, "Error. SearchResult is empty.");
            }
        } catch (NullPointerException e)    {
            logger.log(Level.WARNING, "Error. SearchResult is null.");
        }
    }

    //select matching in the text
    private void putCursorTo(int pos)  {
        int length = currentResult.getText().length();
        Tab currentTab = pane.getSelectionModel().getSelectedItem();
        TextArea area = (TextArea) currentTab.getContent();
        area.selectRange(pos, pos + length);
    }

    //make file info string and display it
    private void showFileInfo(String fileName, int pos, int size)  {
        String text = "File: " + fileName + ". Matching " + (pos + 1) + " / " + size;
        fileInfo.setText(text);
    }

    //move selection cursor to prev/next matching
    public void moveMatchingCursor(int inc) {
        if (!openedFileTabs.isEmpty())  {
            currentMatching += inc;
            if (currentMatching >= totalMatching)
                currentMatching = 0;
            if (currentMatching < 0)
                currentMatching = totalMatching - 1;

            int matching = currentResult.getPositions().get(currentMatching);
            putCursorTo(matching);
            showFileInfo(currentResult.getFile().getName(), currentMatching, totalMatching);
        }
    }
}
