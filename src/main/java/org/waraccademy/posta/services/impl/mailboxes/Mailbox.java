package org.waraccademy.posta.services.impl.mailboxes;

public class Mailbox {
    private int id;
    private final String owner;
    private boolean locked = false;

    private boolean packages = false;

    public Mailbox(String owner) {
        this.owner = owner;
    }

    public Mailbox(String owner, boolean locked) {
        this.owner = owner;
        this.locked = locked;
    }

    public String getOwner() {
        return owner;
    }

    public int getId() {
        return id;
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
