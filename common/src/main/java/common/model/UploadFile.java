package common.model;

import java.io.Serializable;
import java.util.Arrays;

public class UploadFile implements Serializable {
    private String from;
    private String to;
    private String filename;
    private double size;
    private byte[] bytes;

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

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public String toString() {
        return "UploadFile{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", filename='" + filename + '\'' +
                ", size=" + size +
                ", bytes=" + Arrays.toString(bytes) +
                '}';
    }
}
