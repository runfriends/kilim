/* Copyright (c) 2006, Sriram Srinivasan
 *
 * You may distribute this software under the terms of the license 
 * specified in the file "License"
 */

package kilim;

import java.util.concurrent.atomic.AtomicInteger;

import me.jor.util.Log4jUtil;

import org.apache.commons.logging.Log;

public class WorkerThread extends Thread {
	private static final Log log=Log4jUtil.getLog(WorkerThread.class);
    volatile Task        runningTask;
    /**
     * A list of tasks that prefer to run only on this thread. This is used by kilim.ReentrantLock and Task to ensure
     * that lock.release() is done on the same thread as lock.acquire()
     */
    RingQueue<Task>      tasks      = new RingQueue<Task>(10);
    Scheduler            scheduler;
    static AtomicInteger gid        = new AtomicInteger();
    public int           numResumes = 0;

    public WorkerThread(Scheduler ascheduler) {
        this("KilimWorker-" + gid.incrementAndGet(),ascheduler);
    }
    public WorkerThread(String name, Scheduler ascheduler){
    	super(name);
    	scheduler=ascheduler;
    }

    public synchronized RingQueue<Task> swapRunnables(RingQueue<Task> emptyRunnables) {
        RingQueue<Task> ret = this.tasks;
        this.tasks = emptyRunnables;
        return ret;
    }
    
    public void run() {
        try {
            while (true) {
                Task t = getNextTask(this); // blocks until task available
                runningTask = t;
                t._runExecute(this);
                runningTask = null;
            }
        } catch (ShutdownException se) {
            // nothing to do.
        } catch (OutOfMemoryError ex) {
            log.error("Out of memory");
            System.exit(1);
        } catch (Throwable ex) {
            ex.printStackTrace();
            log.error(runningTask);
        }
        runningTask = null;
    }

    protected Task getNextTask(WorkerThread workerThread) throws ShutdownException {
        Task t = null;
        while (true) {
            if (scheduler.isShutdown())
                throw new ShutdownException();

            t = getNextTask();
            if (t != null)
                break;

            // try loading from scheduler
            scheduler.loadNextTask(this);
            synchronized (this) { // ///////////////////////////////////////
                // Wait if still no task to execute.
                t = tasks.get();
                if (t != null)
                    break;

                scheduler.addWaitingThread(this);
                try {
                    wait();
                } catch (InterruptedException ignore) {
                } // shutdown indicator checked above
            } // //////////////////////////////////////////////////////////
        }
        assert t != null : "Returning null task";
        return t;
    }

    public Task getCurrentTask() {
        return runningTask;
    }

    public synchronized void addRunnableTask(Task t) {
        assert t.preferredResumeThread == null || t.preferredResumeThread == this : "Task given to wrong thread";
        tasks.put(t);
        notify();
    }

    public synchronized boolean hasTasks() {
        return tasks.size() > 0;
    }

    public synchronized Task getNextTask() {
        return tasks.get();
    }

    public synchronized void waitForMsgOrSignal() {
        try {
            if (tasks.size() == 0) {
                wait();
            }
        } catch (InterruptedException ignore) {
        }
    }
    public boolean put(Task task){
    	return this.tasks.put(task);
    }
}
