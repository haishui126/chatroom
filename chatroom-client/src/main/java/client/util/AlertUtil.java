package client.util;

import javafx.scene.control.Alert;

public class AlertUtil {
    public static void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.show();
    }
}
