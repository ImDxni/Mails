package org.waraccademy.posta.services.impl.mailboxes;

public class Mailbox {
    private int id;
    private final String owner;
    private boolean locked = false;

    private boolean packages = false;

    private final int item;

    public Mailbox(String owner, int item) {
        this.owner = owner;
        this.item = item;
    }

    public Mailbox(String owner, boolean locked,int item) {
        this.owner = owner;
        this.locked = locked;
        this.item = item;
    }

    public String getOwner() {
        return owner;
    }

    public int getId() {
        return id;
    }

    public int getItem() {
        return item;
    }

    public boolean isLocked() {
        return locked;
    }

    public boolean hasPackages() {
        return packages;
    }

    public void setPackages(boolean packages) {
        this.packages = packages;
    }


    public void setId(int id) {
        this.id = id;
    }


}
