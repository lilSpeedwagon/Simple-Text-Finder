package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;

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

    public ResultQueue search(File dir, String text, String ext)    {
        ResultQueue results = new ResultQueue();

        Thread searchingThread = new Thread(() -> {
            synchronized (results)  {
                TextFinder finder = new TextFinder(results);
                finder.searchText(dir, text, ext);
            }
        });
        searchingThread.start();

        return results;
    }

    public void showModal()    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("modal.fxml"));
        try {
            AnchorPane page = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Error");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page, 200, 80);
            dialogStage.setScene(scene);
            dialogStage.setResizable(false);
            Modal modal = loader.getController();
            modal.setStage(dialogStage);
            dialogStage.show();
        }   catch (IOException e)   {
            System.out.println("IO e");
        }   catch (IllegalStateException e) {
            System.out.println("illegal state");
        }
    }
}
