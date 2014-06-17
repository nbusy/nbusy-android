package com.soygul.organizer.enums;

public enum TaskType {
    /**
     * A task that is only kept in memory queue and hence will be lost if app crashed or closes before task is executed or finished.
     */
    IN_MEMORY,

    /**
     * A task that is persisted to disk queue and hence will not be effected from app crashes or app restarts.
     */
    PERSISTED
}
