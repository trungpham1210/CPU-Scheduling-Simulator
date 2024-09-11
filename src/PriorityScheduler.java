package edu.utdallas.cs4348;

import com.sun.source.tree.Tree;

import java.util.*;

public class PriorityScheduler implements CPUScheduler {
    private TreeMap<Integer, Deque<CPUBurst>> priorityQueue = new TreeMap<>(Collections.reverseOrder());
    private CPUBurst currentTask = null;

    @Override
    public void addTask(CPUBurst burst) {
        // TODO: fill in
        int priority = burst.getPriority();
        if(!priorityQueue.containsKey(priority)){
            priorityQueue.put(priority, new LinkedList<>());
        }
        if(currentTask == null || priority > currentTask.getPriority()){
            if(currentTask != null && currentTask.isRunning()) {
                currentTask.stop();
                priorityQueue.get(currentTask.getPriority()).addFirst(currentTask);
            }
            currentTask = burst;
            currentTask.start(this);
        } else {
            priorityQueue.get(priority).add(burst);
        }
    }

    @Override
    public void done(CPUBurst burst) {
        // TODO: fill in
        if(currentTask!= null && currentTask == burst){
            currentTask = null;
        }
        if(!priorityQueue.isEmpty()){
            Map.Entry<Integer, Deque<CPUBurst>> highestPriority = priorityQueue.firstEntry();
            currentTask = highestPriority.getValue().poll();
            while(currentTask == null && highestPriority !=null) {
                currentTask = highestPriority.getValue().poll();
                if(highestPriority.getValue().isEmpty()){
                    priorityQueue.remove(highestPriority.getKey());
                }
                highestPriority =priorityQueue.firstEntry();
            }
            if (currentTask!=null){
                currentTask.start(this);
            }
        }
    }


    @Override
    public boolean hasTasks() {
        // TODO: fill in
        return currentTask !=null || !priorityQueue.isEmpty();
    }
}
