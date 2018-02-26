import java.util.Random;

/**
 * A consumer continually tries to take bicycles from the end of a quality control belt
 */

public class ShortBeltConsumer extends BicycleHandlingThread {

    // the shortbelt from which the consumer takes the bicycles
    protected ShortBelt shortBelt;

    /**
     * Create a new ShortBeltConsumer that consumes from a shortbelt
     */
    public ShortBeltConsumer(ShortBelt shortBelt) {
        super();
        this.shortBelt = shortBelt;
    }

    /**
     * Loop indefinitely trying to get bicycles from the quality control shortbelt
     */
    public void run() {
        while (!isInterrupted()) {
            try {
            	//System.out.println("shortBeltConsumer gets right");
            	shortBelt.getEndBelt();

                // let some time pass ...
                Random random = new Random();
                int sleepTime = Params.CONSUMER_MIN_SLEEP + 
                		random.nextInt(Params.CONSUMER_MAX_SLEEP - 
                				Params.CONSUMER_MIN_SLEEP);
                sleep(sleepTime);
            }catch (DefException e) {
            	terminate(e);
            }catch (InterruptedException e) {
                this.interrupt();
            }
        }
        System.out.println("ShortBeltConsumer terminated");
    }
}
