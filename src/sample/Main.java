package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sun.awt.image.ImageWatched;

import java.io.File;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class Main extends Application {

    private Stage primaryStage;

    //JavaFX init
    @Override
    public void start(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;
        FXMLLoader loader = new FXMLLoader( getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();
        controller.setOwner(this);

        primaryStage.setTitle("Text Finder");
        primaryStage.setScene(new Scene(root, 860, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public File chooseFile()    {
        //opening file choose dialog window and return the choosen file
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Choose directory to find...");
        File dir = dirChooser.showDialog(primaryStage);

        return dir;
    }

    public LinkedList<SearchResult> search(File dir, String text, String ext)    {
        //init of search and getting list of results
        TextFinder finder = new TextFinder();
        LinkedList<SearchResult> results = finder.searchText(dir, text, ext);

        return results;
    }
}
