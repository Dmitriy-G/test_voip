package server.service;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

public class TaskService {
    private final ForkJoinPool forkJoinPool;

    public TaskService(ForkJoinPool forkJoinPool) {
        this.forkJoinPool = forkJoinPool;
    }

    public void executeTask(ForkJoinTask<?> task) {
        this.forkJoinPool.execute(task);
    }
}
