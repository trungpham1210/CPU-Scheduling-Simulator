package edu.utdallas.cs4348;

import java.util.LinkedList;
import java.util.Queue;

public class MultilevelFeedbackQueueScheduler implements CPUScheduler {

    private static final int FIRST_QUEUE_QUANTUM = 3;
    private static final int SECOND_QUEUE_QUANTUM = 6;
    private Queue<CPUBurst> firstQueue;
    private Queue<CPUBurst> secondQueue;
    private Queue<CPUBurst> thirdQueue;
    private CPUBurst currentTask;
    private Timer timer;
    public MultilevelFeedbackQueueScheduler() {
        this.firstQueue = new LinkedList<>();
        this.secondQueue = new LinkedList<>();
        this.thirdQueue = new LinkedList<>();
    }

    @Override
    public void addTask(CPUBurst burst) {
        firstQueue.add(burst);
        if (currentTask == null) {
            scheduleNextTask();
        }
    }

    @Override
    public void done(CPUBurst burst) {
        scheduleNextTask();
    }

    @Override
    public boolean hasTasks() {
        return currentTask != null || !firstQueue.isEmpty() || !secondQueue.isEmpty() || !thirdQueue.isEmpty();
    }

    private void scheduleNextTask() {
        if (!firstQueue.isEmpty()) {
            startTask(firstQueue.poll(), FIRST_QUEUE_QUANTUM);
        } else if (!secondQueue.isEmpty()) {
            startTask(secondQueue.poll(), SECOND_QUEUE_QUANTUM);
        } else if (!thirdQueue.isEmpty()) {
            startTask(thirdQueue.poll(), Integer.MAX_VALUE);
        } else {
            currentTask = null;
        }
    }

    private void startTask(CPUBurst burst, int quantum) {
        currentTask = burst;
        timer = new Timer(quantum) {
            @Override
            void timerExpired() {
                if (burst.isRunning()) {
                    burst.stop();
                    if (quantum == FIRST_QUEUE_QUANTUM) {
                        secondQueue.add(burst);
                    } else if (quantum == SECOND_QUEUE_QUANTUM) {
                        thirdQueue.add(burst);
                    }
                    scheduleNextTask();
                }
            }
        };
        TimedTasks.addTimer(timer);
        burst.start(this);
    }
}
