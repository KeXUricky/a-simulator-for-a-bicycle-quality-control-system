import java.util.Random;

public class Robot extends BicycleHandlingThread {
	// the belt from which the consumer takes the bicycles
    protected Belt belt;
    /**
     * Create a new Robot that consumes from a belt
     */
    public Robot(Belt belt) {
        super();
        this.belt = belt;
    }
    /**
     * Loop indefinitely trying to get bicycles from the quality control belt
     */
    public void run() {
        while (!isInterrupted()) {
            try {
            	Thread.sleep(Params.ROBOT_MOVE_TIME);
            	belt.takeOff_goBack();
            } catch (SensorException e) {
                terminate(e);
            } catch (InterruptedException e) {
                this.interrupt();
            }
        }
        System.out.println("Robot terminated");
    }
}
