import java.util.Random;

/**
 * A producer continually tries, at varying time intervals, 
 * to put a bicycle onto a belt
 */
public class Sensor extends BicycleHandlingThread {

    // the belt to which the producer puts the bicycles
    protected Belt belt;
    /**
     * Create a new producer to feed a given belt
     */
    Sensor(Belt belt) {
        super();
        this.belt = belt;
    }

    /**
     * The thread's main method. 
     * Continually tries to place bicycles on the belt at random intervals.
     */
    public void run() {
        while (!isInterrupted()) {
            try {
                // sensor identify tagged bicycles on the belt
            	belt.identifyTagged();	
            }catch (InterruptedException e) {
                this.interrupt();
            }
        }
        System.out.println("Sensor terminated");
    }
}
