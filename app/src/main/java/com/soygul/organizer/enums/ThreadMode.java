package com.soygul.organizer.enums;

public enum ThreadMode {
    /**
     * Subscriber will be called in the same thread, which is posting the event. This is the default.
     */
    SAME_THREAD,

    /**
     * Subscriber will be called in UI (main) thread .
     */
    UI_THREAD,

    /**
     * Subscriber will be called in a background thread.
     */
    BACKGROUND_THREAD,
}
