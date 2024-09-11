package edu.utdallas.cs4348;

import java.util.LinkedList;
import java.util.Queue;

public class RRScheduler implements CPUScheduler {
    private Queue<CPUBurst> tasks;
    private int timeQuantum;
    private Timer timer;

    public RRScheduler(int timeQuantum) {
        this.timeQuantum = timeQuantum;
        this.tasks = new LinkedList<>();
    }

    @Override
    public void addTask(CPUBurst burst) {
        tasks.add(burst);
        if (tasks.size() == 1) {
            startNextTask();
        }
    }

    @Override
    public void done(CPUBurst burst) {
        tasks.remove();
        TimedTasks.clearTimer();
        if (hasTasks()) {
            startNextTask();
        }
    }

    @Override
    public boolean hasTasks() {
        return !tasks.isEmpty();
    }

    private void startNextTask() {
        CPUBurst burst = tasks.peek();
        timer = new Timer(timeQuantum) {
            @Override
            void timerExpired() {
                burst.stop();
                tasks.add(tasks.remove());
                startNextTask();
            }
        };
        TimedTasks.addTimer(timer);
        burst.start(this);
    }
}
