package org.geoint.canon.stream;

/**
 * Resource handle to manage the asynchronous notification of events on a 
 * stream.
 * 
 * @see EventHandler
 * @see EventStream
 * @author steve_siebert
 */
public interface HandlerNotifier {
    
    /**
     * Pause the event notifier.
     * <p>
     * If the notifier is already paused, do nothing.
     * 
     * @throws IllegalStateException thrown if the notifier is stopped
     */
    void pause() throws IllegalStateException;
    
    /**
     * Restart an event notifier that has previously been paused.
     * <p>
     * If the notifier is running, do nothing.
     * 
     * @throws IllegalStateException thrown if the notifier is stopped
     */
    void restart() throws IllegalStateException;
    
    /**
     * Stop the event notifier.
     * <p>
     * If the notified is already stopped, do nothing.
     */
    void stop();
    
    /**
     * Determine if the notifier is currently running.
     * 
     * @return true if notifier is not paused or stopped, otherwise false
     */
    boolean isRunning();
    
    /**
     * Determine if the notifier has been stopped.
     * 
     * @return true if the notifier is stopped, otherwise false
     */
    boolean isStopped ();
    
    /**
     * Determin if the notifier is paused.
     * 
     * @return true if the notifier is paused, otherwise false
     */
    boolean isPaused();
}
