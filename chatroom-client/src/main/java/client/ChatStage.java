package client;

import client.dao.MessageDao;
import client.util.DB;
import common.Message;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.LinkedList;
import java.util.List;

public class ChatStage extends Application {
    private static final List<ChatStage> stageManager = new LinkedList<>();
    private final String me;
    private final String name;
    private VBox vBox;
    private MessageDao messageDao;
    ClientHandler handler = ClientHandler.getInstance();
    private ListView<Message> messageListView;

    public ChatStage(String me, String name) {
        this.me = me;
        this.name = name;
    }

    @Override
    public void start(Stage stage) throws Exception {
        stageManager.add(this);
        layoutChildren();
        stage.setScene(new Scene(vBox, 600, 500));
        stage.setTitle(name);
        stage.setOnCloseRequest(windowEvent -> stageManager.remove(this));
        stage.show();
    }

    private void layoutChildren() {
        vBox = new VBox();
        vBox.setPadding(new Insets(20.0));
        vBox.setSpacing(20.0);
        messageDao = DB.getInstance().getDao(MessageDao.class);
        messageListView = new ListView<>();
        messageListView.setFocusTraversable(false);
        messageListView.getItems().addAll(messageDao.getAll());
        TextArea msgArea = new TextArea();
        Button sendBtn = new Button("发送(回车)");
        vBox.getChildren().addAll(messageListView, msgArea, sendBtn);
        messageListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Message message, boolean empty) {
                super.updateItem(message, empty);
                if (!empty) {
                    VBox box = new VBox();
                    Label name = new Label();
                    if (message.getFrom().equals(ChatStage.this.me)) {
                        box.setAlignment(Pos.CENTER_RIGHT);
                        name.setText("我说：");
                    } else {
                        name.setText(ChatStage.this.name + "说：");
                    }
                    name.setFont(Font.font(11));
                    name.setTextFill(Paint.valueOf("#999999"));
                    Label msg = new Label(message.getMsg());
                    msg.setFont(Font.font(14));
                    msg.setPadding(new Insets(0, 0, 0, 10));
                    box.getChildren().addAll(name, msg);
                    this.setGraphic(box);
                } else {
                    setText(null);
                    setGraphic(null);
                }
            }
        });

        sendBtn.setOnMouseClicked(mouseEvent -> {
            String msg = msgArea.getText();
            if (!msg.isEmpty()) {
                Message message = new Message(me, name, msg);
                messageDao.save(message);
                handler.sendMessage(message);
                messageListView.getItems().add(message);
                msgArea.setText("");
            }
        });
    }

    public static ChatStage get(String friend) {
        for (ChatStage chatStage : stageManager) {
            if (chatStage.name.equals(friend)) {
                return chatStage;
            }
        }
        return null;
    }

    public void addMessage(Message message) {
        messageListView.getItems().add(message);
        messageListView.scrollTo(message);
        messageDao.save(message);
    }
}
