module chatroom.server {
    requires javafx.controls;
    requires javafx.fxml;
    requires io.netty.all;
    requires chatroom.common;

    opens server to javafx.fxml;
    exports server;
}