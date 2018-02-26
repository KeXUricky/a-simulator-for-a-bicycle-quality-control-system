import java.util.Random;

public class Robot extends BicycleHandlingThread {
	// the belt from which the robot operateds the bicycles
    protected Belt belt;
    protected ShortBelt shortBelt;
    Bicycle bicycle=null;
    /**
     * Create a new Robot that operates bicycles on belt and shortbelt
     */
    public Robot(Belt belt,ShortBelt shortBelt) {
        super();
        this.belt = belt;
        this.shortBelt=shortBelt;
        
    }
    /**
     * Loop indefinitely trying to get bicycles from the quality control belt
     */
    public void run() {
        while (!isInterrupted()) {
            try {
            	Thread.sleep(Params.ROBOT_MOVE_TIME);

            	// the third bicycle on the belt is tagged and there is no bicycle in the inspector 
            	if(belt.getTagged()&&!belt.getGoBackBicycle()){
            		belt.takeOffBicycle();
            	
            	//there is one inspected bicycle in the inspector and the first positon of the shortbelt is vacant
            	}else if(belt.getGoBackBicycle()&&shortBelt.peek(0)==null){
            		bicycle=belt.goBackBicycle;
            		belt.resetGoBackBicycle();
            		shortBelt.put(bicycle, 0);
            		
            	}
            	//other situations, just wait
            	else{
            		belt.haveToWait();
            	}
            } catch (SensorException e) {
                terminate(e);
            } catch (InterruptedException e) {
                this.interrupt();
            }
        }
        System.out.println("Robot terminated");
    }
  }
