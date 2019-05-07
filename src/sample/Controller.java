package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.awt.*;
import java.awt.TextArea;
import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiConsumer;
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

    private Main owner;
    private FileTree fileTree;
    private TabManager tabManager;

    public Controller() {

        //init TabManager after init of scene from fxml
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                tabManager = new TabManager(tabView, fileInfo);
            }
        });
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
            File path = new File(pathTextField.getText());
            if (path.exists() && !extTextField.getText().isEmpty()) {
                LinkedList<SearchResult> results = owner.search(path, searchTextField.getText(), extTextField.getText());
                buildFileTree(results, path);
            }
        }
    }

    //recursive function for creating a branches and leafs of file tree (TreeItem)
    private void buildFileTreeLeaf(TreeItem<FileTree.Node> treeNode)    {

        BiConsumer<? super File,? super FileTree.Node> addTreeItem = new BiConsumer<File, FileTree.Node>() {
            @Override
            public void accept(File file, FileTree.Node node) {
                TreeItem<FileTree.Node> leafTreeItem = new TreeItem<>(node);
                if (!node.isLeaf())
                    leafTreeItem.setExpanded(true);

                treeNode.getChildren().add(leafTreeItem);
                buildFileTreeLeaf(leafTreeItem);
            }
        };

        treeNode.getValue().getChildren().forEach(addTreeItem);
    }


    private void buildFileTree(LinkedList<SearchResult> results, File rootFile) {
        fileTree = new FileTree(results, rootFile);

        FileTree.Node root = fileTree.getRoot();
        TreeItem<FileTree.Node> rootItem = new TreeItem<>(root);
        rootItem.setExpanded(true);

        buildFileTreeLeaf(rootItem);

        pathTreeView.setRoot(rootItem);

        //event handler for clicking on file
        pathTreeView.addEventHandler(MouseEvent.MOUSE_CLICKED, new FileClickHandler());
    }

    public void nextMatching(ActionEvent actionEvent) {
        if (tabManager != null)
            tabManager.moveMatchingCursor(1);
    }

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
                TreeItem<FileTree.Node> node = ((TreeCell) graphicNode).getTreeItem();

                SearchResult result = fileTree.getResultFromNode(node.getValue());

                tabManager.addTab(result);
            }

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
