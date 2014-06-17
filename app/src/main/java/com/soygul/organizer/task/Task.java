package com.soygul.organizer.task;

import com.soygul.organizer.enums.TaskState;
import com.soygul.organizer.enums.TaskType;

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
