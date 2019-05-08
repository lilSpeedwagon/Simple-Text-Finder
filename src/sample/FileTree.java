package sample;

import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
    structure which contains representing of resulting file tree
    only files with desired string pattern
 */

public class FileTree {

    private static Logger logger = Logger.getLogger(TextFinder.class.getName());
    private TreeItem<TFile> rootItem;
    private LinkedList<SearchResult> results;
    private TreeView treeView;

    public LinkedList<SearchResult> getResults()    {
        return results;
    }

    //recursive build tree and add missed nodes
    private TreeItem<TFile> fromLeafToRoot(TFile leafFile)   {
        TreeItem<TFile> parentItem;
        if (leafFile.getParentFile().equals(rootItem.getValue()))  {
            parentItem = rootItem;
        }   else    {
            parentItem = fromLeafToRoot(leafFile.getParentFile());
            parentItem.setExpanded(true);
        }

        for (TreeItem<TFile> item : parentItem.getChildren())    {
            if (item.getValue().equals(leafFile))   {
                return item;
            }
        }

        TreeItem<TFile> newItem = new TreeItem<>(leafFile);
        parentItem.getChildren().add(newItem);
        return newItem;
    }

    //constructor of file tree, based on results of search
    public FileTree(TreeView treeView, TFile rootFile)   {
        logger.info("Building file tree...");
        results = new LinkedList<>();
        this.treeView = treeView;

        //root init (contains directory for search)i
        rootItem = new TreeItem<>(rootFile);
        rootItem.setExpanded(true);
        Platform.runLater(() -> treeView.setRoot(rootItem));
        logger.info("Done.");
    }

    //add new file
    public void addResult(SearchResult result)  {
        logger.info("Adding " + result.getFile().getName());
        results.add(result);
        fromLeafToRoot(result.getFile());
    }

    //get SearchResult from selected node
    public SearchResult getResultFromNode(TreeItem<TFile> item) {
        //only for files, not for directories
        if (item.getValue().isFile()) {
            for (SearchResult result : results) {
                if (result.getFile().equals(item.getValue())) {
                    return result;
                }
            }
            logger.log(Level.WARNING, "There is no search result for file " + item.getValue());
        }   else    {
            logger.log(Level.WARNING, item.getValue() + " is not a file.");
        }
        return null;
    }
}
