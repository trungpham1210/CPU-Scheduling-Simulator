package edu.utdallas.cs4348;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class FirstComeFirstServedScheduler implements CPUScheduler {
    private Queue<CPUBurst> taskQueue = new LinkedList<>();
    private CPUBurst currentTask  = null;

    @Override
    public void addTask(CPUBurst burst) {
        // TODO: fill in
        if (currentTask == null) {
            currentTask = burst;
            currentTask.start(this);
        } else{
            taskQueue.add(burst);
        }
    }

    @Override
    public void done(CPUBurst burst) {
        // TODO: fill in
        if (!taskQueue.isEmpty()){
            currentTask = taskQueue.poll();
            currentTask.start(this);
        } else {
            currentTask = null;
        }
    }

    @Override
    public boolean hasTasks() {
        // TODO: fill in
        return currentTask != null || !taskQueue.isEmpty();
    }
}
