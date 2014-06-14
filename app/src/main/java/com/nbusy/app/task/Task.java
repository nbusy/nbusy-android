package com.nbusy.app.task;

import com.nbusy.app.task.enums.TaskState;
import com.nbusy.app.task.enums.TaskType;

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
