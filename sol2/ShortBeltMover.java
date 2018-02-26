/**
 * A belt-mover moves a belt along as often as possible, but only
 * when there is a bicycle on the belt not at the last position.
 */

public class ShortBeltMover extends BicycleHandlingThread {

    // the shortbelt to be handled
    protected ShortBelt shortBelt;

    /**
     * Create a new ShortBeltMover with a belt to move
     */
    public ShortBeltMover(ShortBelt shortBelt) {
        super();
        this.shortBelt = shortBelt;
    }

    /**
     * Move the shortbelt as often as possible, but only if there 
     * is a bicycle on the shortbelt which is not in the last position.
     */
    public void run() {
        while (!isInterrupted()) {
            try {
                // spend BELT_MOVE_TIME milliseconds moving the belt
                Thread.sleep(Params.BELT_MOVE_TIME);
                //System.out.println("shortMover gets right");
                shortBelt.move();
            } catch (OverloadException e) {
                terminate(e);
            } catch (InterruptedException e) {
                this.interrupt();
            }
        }

        System.out.println("ShortBeltMover terminated");
    }
}
