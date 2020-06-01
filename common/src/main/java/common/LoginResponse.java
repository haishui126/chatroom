package common;

import java.io.Serializable;

public class LoginResponse implements Serializable {
    private boolean success;
    private String msg;

    public LoginResponse() {
    }

    public LoginResponse(boolean success, String msg) {
        this.success = success;
        this.msg = msg;
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

    @Override
    public String toString() {
        return "LoginResponse{" +
                "success=" + success +
                ", msg='" + msg + '\'' +
                '}';
    }
}
