package sample;

import javafx.scene.control.TreeItem;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.logging.Logger;

/*
    structure which contains representing of resulting file tree
    only files with desired string pattern
 */

public class FileTree {

    private static Logger logger = Logger.getLogger(TextFinder.class.getName());

    //node class for every file or directory in tree
    public class Node  {
        private boolean isRoot = false;
        private boolean isLeaf = true;
        private File file;
        //contains children of unique current node
        private HashSet<Node> children = new HashSet<>();

        Node(File file)   {
            this.file = file;
        }

        public boolean isRoot() {
            return isRoot;
        }

        public boolean isLeaf() {
            return isLeaf;
        }

        public HashSet<Node> getChildren() {
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

    //recursive function for creating Nodes for directories (branches) and files (leafs)
    private Node fromLeafToRoot(File leafFile)   {
        Node leafNode = new Node(leafFile);
        //if root is reached collapsing recursive stack and make connections between nodes in branch
        if (leafFile.getParentFile().equals(root.file)) {
            logger.info("File: " + leafFile.getName() + ", parent " + root.file.getName());
            root.children.add(leafNode);
        }   else {
            //else creating a node for directory or file
            Node parentNode = fromLeafToRoot(leafFile.getParentFile());
            parentNode.isLeaf = false;
            parentNode.children.add(leafNode);
            logger.info("File: " + leafFile.getName() + ", parent: " + parentNode.file.getName());
        }

        return leafNode;
    }

    //constructor of file tree, based on results of search
    public FileTree(LinkedList<SearchResult> results, File rootFile )   {
        logger.info("Building file tree...");

        //hashSet for containing unique files
        HashSet<File> files = new HashSet<>();
        for (SearchResult result : results) {
            files.add(result.getFile());
        }

        logger.info("Total " + files.size() + " files.");

        //root init (contains directory for search)
        root = new Node(rootFile);
        root.isRoot = true;

        //creates a branch from root to leaf for every file
        for (File file : files) {
            fromLeafToRoot(file);
        }
    }

    public Node getRoot()   {
        return root;
    }
}
