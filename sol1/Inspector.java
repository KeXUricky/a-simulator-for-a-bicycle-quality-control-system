import java.util.Random;

public class Inspector extends BicycleHandlingThread{
	 // the belt to which the producer puts the bicycles

    protected Belt belt;
    /**
     * Create a new inspector to inspect a given bicycle from a given belt
     */
    public Inspector(Belt belt) {
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
                belt.inspectBicycle();
                // let some time pass ...
                Random random = new Random();
                int sleepTime = random.nextInt(Params.INSPECT_TIME);
                sleep(sleepTime);
            } catch (InterruptedException e) {
                this.interrupt();
            }
        }
        System.out.println("Inspector terminated");
    }

}
