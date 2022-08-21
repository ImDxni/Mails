package org.waraccademy.posta.services.impl.packages;

public class Package {
    private int id;
    private final String sender,target;
    private STATUS status = STATUS.STOP;

    public Package(String sender, String target) {
        this.sender = sender;
        this.target = target;
    }

    public String getSender() {
        return sender;
    }

    public String getTarget() {
        return target;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public enum STATUS{
        STOP,DELIVERING,DELIVERED
    }
}
