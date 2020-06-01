package client;

import client.dao.MessageDao;
import client.util.DB;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    private static Stage stage;

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
        DB.getInstance().getDao(MessageDao.class).createTable();
        launch(args);
    }
}
