package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class Controller {
    public TextField pathTextField;
    public Button pathDialogButton;
    public TextField searchTextField;
    public TreeView pathTreeView;
    public TabPane tabView;
    public ScrollPane treeScrollPane;
    public Button searchBurron;
    public TextField extTextField;
    public Button searchButton;

    private Main owner;
    private File choosenPath;

    public void setOwner(Main owner)    {
        this.owner = owner;
    }

    public void pathSelect(ActionEvent actionEvent) {
        System.out.println("dir selecting...");
        choosenPath = owner.chooseFile();
        if (choosenPath != null){
            pathTextField.setText(choosenPath.getAbsolutePath());
        } else  {
            pathTextField.setText(null);
        }
    }

    public void search(ActionEvent actionEvent) {
        if (choosenPath.exists() && !searchTextField.getText().isEmpty() && !extTextField.getText().isEmpty()) {
            LinkedList<SearchResult> results = owner.search(choosenPath, searchTextField.getText(), extTextField.getText());
            buildFileTree(results, choosenPath);
        }
    }

    private void buildFileTreeLeaf(TreeItem<FileTree.Node> node)    {
        for (FileTree.Node leafNode : node.getValue().getChildren())    {
            TreeItem<FileTree.Node> leafTreeItem = new TreeItem<>(leafNode);
            if (!leafNode.isLeaf())
                leafTreeItem.setExpanded(true);
            node.getChildren().add(leafTreeItem);
            buildFileTreeLeaf(leafTreeItem);
        }
    }

    private void buildFileTree(LinkedList<SearchResult> results, File rootFile) {
        FileTree fileTree = new FileTree(results, rootFile);

        FileTree.Node root = fileTree.getRoot();
        TreeItem<FileTree.Node> rootItem = new TreeItem<>(root);
        rootItem.setExpanded(true);

        buildFileTreeLeaf(rootItem);

        pathTreeView.setRoot(rootItem);
    }
}
