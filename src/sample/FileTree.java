package sample;

import javafx.scene.control.TreeItem;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.logging.Logger;

public class FileTree {

    private static Logger logger = Logger.getLogger(TextFinder.class.getName());

    public class Node  {
        private boolean isRoot = false;
        private boolean isLeaf = true;
        private File file;
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

    private Node fromLeafToRoot(File leafFile)   {
        Node leafNode = new Node(leafFile);
        if (leafFile.getParentFile().equals(root.file)) {
            logger.info("File: " + leafFile.getName() + ", parent " + root.file.getName());
            root.children.add(leafNode);
        }   else {
            Node parentNode = fromLeafToRoot(leafFile.getParentFile());
            parentNode.isLeaf = false;
            parentNode.children.add(leafNode);
            logger.info("File: " + leafFile.getName() + ", parent: " + parentNode.file.getName());
        }

        return leafNode;
    }

    public FileTree(LinkedList<SearchResult> results, File rootFile )   {
        logger.info("Building file tree...");

        HashSet<File> files = new HashSet<>();
        for (SearchResult result : results) {
            files.add(result.getFile());
        }

        logger.info("Total " + files.size() + " files.");

        root = new Node(rootFile);
        root.isRoot = true;

        for (File file : files) {
            fromLeafToRoot(file);
        }
    }

    public Node getRoot()   {
        return root;
    }
}
