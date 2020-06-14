package client;

import common.DB;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    private static Stage stage;
    private static DB db;

    @Override
    public void start(Stage stage) throws Exception {
        App.stage = stage;
        setRoot("login");
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        stage.setScene(new Scene(loadFXML(fxml)));
    }

    public static void setRoot(String fxml, double w, double h) throws IOException {
        stage.setScene(new Scene(loadFXML(fxml), 600, 550));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void initDB(String name) {
        db = new DB(name);
    }

    public static DB getDB() {
        return App.db;
    }
}
