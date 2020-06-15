package common.model;

import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable {
    private String username;
    private String status;
    private int op; //-1 删除，0更新，1添加

    public User() {
    }

    public User(String username) {
        this.username = username;
    }

    public User(String username, String status) {
        this.username = username;
        this.status = status;
    }

    public User(String username, String status, int op) {
        this.username = username;
        this.status = status;
        this.op = op;
    }

    public int getOp() {
        return op;
    }

    public void setOp(int op) {
        this.op = op;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return username != null ? username.hashCode() : 0;
    }
}
