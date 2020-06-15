package common.model;

import java.io.Serializable;
import java.util.List;

public class Response implements Serializable {
    private boolean success;
    private String msg;
    private List<User> friends;

    public Response() {
    }

    public Response(boolean success, String msg) {
        this.success = success;
        this.msg = msg;
    }

    public Response(boolean success, String msg, List<User> friends) {
        this.success = success;
        this.msg = msg;
        this.friends = friends;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<User> getFriends() {
        return friends;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "success=" + success +
                ", msg='" + msg + '\'' +
                '}';
    }
}
