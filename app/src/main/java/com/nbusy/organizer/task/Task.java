package com.nbusy.organizer.task;

import com.nbusy.organizer.enums.TaskState;
import com.nbusy.organizer.enums.TaskType;

public abstract class Task {
    private String groupId;
    private TaskState state;
    private TaskType type;

    public Task(TaskType type) {
        this.type = type;
    }

    public void run() {
    }

    public abstract void onQueued();

    public abstract void onRunning();
}
