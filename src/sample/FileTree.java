package sample;

import javafx.scene.control.TreeItem;
import sun.awt.image.ImageWatched;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
    structure which contains representing of resulting file tree
    only files with desired string pattern
 */

public class FileTree {

    private static Logger logger = Logger.getLogger(TextFinder.class.getName());
    private LinkedList<SearchResult> results;

    //node class for every file or directory in tree
    public class Node  {
        private boolean isRoot = false;
        private boolean isLeaf = true;
        private File file;
        //contains children of unique current node
        private HashMap<File, Node> children = new HashMap<>();

        Node(File file)   {
            this.file = file;
        }

        public boolean isRoot() {
            return isRoot;
        }

        public boolean isLeaf() {
            return isLeaf;
        }

        public HashMap<File, Node> getChildren() {
            return children;
        }

        public String toString()    {
            return file.getName();
        }

        @Override
        public int hashCode() {
            return file.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return (obj instanceof Node) && file.equals(((Node) obj).file);
        }
    }

    private Node root;

    public LinkedList<SearchResult> getResults()    {
        return results;
    }

    //recursive function for creating Nodes for directories (branches) and files (leafs)
    private Node fromLeafToRoot(File leafFile)   {
        Node parentNode;
        if (leafFile.getParentFile().equals(root.file))  {
            parentNode = root;
        }   else    {
            parentNode = fromLeafToRoot(leafFile.getParentFile());
        }

        parentNode.isLeaf = false;
        if (!parentNode.children.containsKey(leafFile))    {
            Node newNode = new Node(leafFile);
            parentNode.children.put(leafFile, newNode);
            return newNode;
        }

        return parentNode.children.get(leafFile);
    }



    //constructor of file tree, based on results of search
    public FileTree(LinkedList<SearchResult> results, File rootFile )   {
        logger.info("Building file tree...");

        this.results = results;
        logger.info("Total " + results.size() + " files.");

        //root init (contains directory for search)
        root = new Node(rootFile);
        root.isRoot = true;

        //creates a branch from root to leaf for every file
        for (SearchResult result : results) {
            File file = result.getFile();
            fromLeafToRoot(file);
        }
    }

    public Node getRoot()   {
        return root;
    }

    public SearchResult getResultFromNode(Node node) {
        //only for files, not for directories
        if (node.file.isFile()) {
            for (SearchResult result : results) {
                if (result.getFile().equals(node.file)) {
                    return result;
                }
            }
            logger.log(Level.WARNING, "There is no search result for file " + node.file);
        }   else    {
            logger.log(Level.WARNING, node.file + " is not a file.");
        }
        return null;
    }
}
