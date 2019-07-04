package zbl.fly.commons;

import java.io.Serializable;

public class AjaxResult implements Serializable {
    private final int status;
    private final Object data;

    private AjaxResult(int status, Object data) {
        this.status = status;
        this.data = data;
    }

    public static AjaxResult error(int errorCode, String errorMessage) {
        return new AjaxResult(errorCode, errorMessage);
    }

    public static AjaxResult success() {
        return new AjaxResult(0, null);
    }

    public static AjaxResult success(Object data) {
        return new AjaxResult(0, data);
    }

    public int getStatus() {
        return status;
    }

    public Object getData() {
        return data;
    }
}
