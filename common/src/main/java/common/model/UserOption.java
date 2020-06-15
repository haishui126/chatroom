package common.model;

import java.io.Serializable;

public class UserOption implements Serializable {
    private String from;
    private String to;
    private int op;//-1删除好友，0添加好友，1确认添加好友

    public UserOption(String from, String to, int op) {
        this.from = from;
        this.to = to;
        this.op = op;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public int getOp() {
        return op;
    }

    public void setOp(int op) {
        this.op = op;
    }
}
