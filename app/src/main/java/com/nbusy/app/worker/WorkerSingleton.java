package com.nbusy.app.worker;

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
        worker.destroy();
        worker = null;
    }
}
