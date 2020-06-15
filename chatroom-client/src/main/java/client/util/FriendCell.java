package client.util;

import client.ChatStage;
import client.ClientHandler;
import client.controller.MainController;
import common.model.User;
import common.model.UserOption;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FriendCell extends ListCell<User> {
    @Override
    protected void updateItem(User user, boolean empty) {
        super.updateItem(user, empty);
        if (!empty) {
            VBox box = new VBox();
            Label nameLabel = new Label(user.getUsername());
            Label statusLabel = new Label(user.getStatus());
            box.getChildren().addAll(nameLabel, statusLabel);
            box.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getClickCount() == 2 && mouseEvent.getButton() == MouseButton.PRIMARY) {
                    try {
                        new ChatStage(MainController.getUsername(), user.getUsername()).start(new Stage());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            box.setOnContextMenuRequested(contextMenuEvent -> {
                MenuItem menuItem = new MenuItem("删除好友");
                menuItem.setOnAction(actionEvent -> {
                    ClientHandler.getInstance().sendUserOption(new UserOption(MainController.getUsername(), user.getUsername(), -1));
                });
                ContextMenu contextMenu = new ContextMenu(menuItem);
                contextMenu.show(box.getScene().getWindow(), contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY());
            });
            this.setGraphic(box);
        } else {
            setText(null);
            setGraphic(null);
        }
    }
}