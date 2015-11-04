package com.nbusy.app;

/**
 * {@link Worker} single instance provider.
 */
public class WorkerSingleton {
    private static Worker worker = null;

    public static Worker getWorker() {
        if (worker == null) {
            worker = new Worker();
        }

        return worker;
    }

    public static void destroyWorker() {
        worker = null;
    }
}
