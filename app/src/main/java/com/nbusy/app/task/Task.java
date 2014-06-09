package com.nbusy.app.task;

public abstract class Task {
    //
    private boolean ephemeral;

    public void run() {
    }

    public abstract void onQueued();

    public enum states {QUEUED, RUNNING, DONE}
}
