package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import java.io.File;

public class Controller {
    public TextField pathTextField;
    public Button pathDialogButton;
    public TextField searchTextField;
    public TreeView pathTreeView;
    public TabPane tabView;
    public TextField extTextField;
    public Button searchButton;
    public Label fileInfo;
    public Button prevButton;
    public Button nextButton;

    private String searchStr = "Search";
    private String searchingStr = "Searching";

    private Main owner;
    private FileTree fileTree;
    private TabManager tabManager;

    public Controller() {
        //init TabManager after init of scene from fxml
        Platform.runLater(() -> tabManager = new TabManager(tabView, fileInfo));
    }

    //connects Main function with GUI controller
    public void setOwner(Main owner)    {
        this.owner = owner;
    }

    //invoke chooseFile() in Main and put it in path textField
    public void pathSelect(ActionEvent actionEvent) {
        File path = owner.chooseFile();
        if (path != null){
            pathTextField.setText(path.getAbsolutePath());
        } else  {
            pathTextField.setText(null);
        }
    }

    //invoke search() if it has correct path, extension and text pattern
    public void search(ActionEvent actionEvent) {
        if (!pathTextField.getText().isEmpty()) {
            TFile path = new TFile(pathTextField.getText());
            if (path.exists() && !extTextField.getText().isEmpty() && !searchTextField.getText().isEmpty()) {
                ResultQueue results = owner.search(path, searchTextField.getText(), extTextField.getText());

                //search matching in new thread
                TreeBuilder builder = new TreeBuilder(results, path);
                Thread treeBuildingThread = new Thread(builder);
                treeBuildingThread.start();

                Platform.runLater(() -> {
                    searchButton.setDisable(true);
                    searchButton.setText(searchingStr);
                });
            }   else    {
                owner.showModal();
            }
        }
    }



    //asynch builder of file tree
    class TreeBuilder implements Runnable {
        private final ResultQueue results;
        private final TFile path;

        public TreeBuilder(ResultQueue results, TFile path) {
            this.results = results;
            this.path = path;
        }

        @Override
        public void run() {
            synchronized (results) {
                pathTreeView.setRoot(null);
                fileTree = new FileTree(pathTreeView, path);

                while (!results.isSearchFinished() || !results.isEmpty()) {
                    fileTree.addResult(results.pop());
                }

                //event handler for clicking on file
                pathTreeView.addEventHandler(MouseEvent.MOUSE_CLICKED, new FileClickHandler());

                Platform.runLater(() -> {
                    searchButton.setDisable(false);
                    searchButton.setText(searchStr);
                });
            }
        }
    }

    //button click controller
    public void nextMatching(ActionEvent actionEvent) {
        if (tabManager != null)
            tabManager.moveMatchingCursor(1);
    }

    //button click controller
    public void prevMatching(ActionEvent actionEvent) {
        if (tabManager != null)
            tabManager.moveMatchingCursor(-1);
    }

    //handler for clicking on file in FileTree
    private class FileClickHandler implements EventHandler<MouseEvent>    {
        @Override
        public void handle(MouseEvent event) {
            Node graphicNode = event.getPickResult().getIntersectedNode();

            // Accept clicks only on node cells, and not on empty spaces of the TreeView
            if (graphicNode instanceof TreeCell && ((TreeCell) graphicNode).getText() != null) {
                TreeItem<TFile> node = ((TreeCell) graphicNode).getTreeItem();

                SearchResult result = fileTree.getResultFromNode(node);

                tabManager.addTab(result);
            }

            // Accept clicks on text
            /*
                here I've got some trouble:
                files with the same name can't to be recognized because of the same Text objects on their nodes
             */
            if (graphicNode instanceof  Text)   {
                Text text = (Text) graphicNode;
                //if click on text label - looking for file with this name
                for (SearchResult result : fileTree.getResults())   {
                    if (text.getText().equals(result.getFile().getName()))    {
                        tabManager.addTab(result);
                        break;
                    }
                }
            }
        }
    }


}
