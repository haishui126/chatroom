package common.model;

import java.io.Serializable;

public class GroupMessage implements Serializable {
    private String from;
    private String group;
    private String msg;
    private int flag;//1添加到群聊，-1退出群聊

    public GroupMessage(String from, String group, String msg) {
        this.from = from;
        this.group = group;
        this.msg = msg;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
