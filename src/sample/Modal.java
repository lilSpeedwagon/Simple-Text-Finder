package sample;

import javafx.event.ActionEvent;
import javafx.stage.Stage;

public class Modal {
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void close(ActionEvent actionEvent) {
        stage.close();
    }
}
