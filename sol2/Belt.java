/**
 * The bicycle quality control belt
 */
public class Belt {

	// the items in the belt segments
	protected Bicycle[] segment;

	// the length of this belt
	protected int beltLength = 5;

	// to help format output trace
	final private static String indentation = "                  ";

	protected boolean robotTakeOff = false;

	protected Bicycle takeOffBicycle = null;
	protected Bicycle goBackBicycle = null;

	/**
	 * Create a new, empty belt, initialised as empty
	 */
	public Belt() {
		segment = new Bicycle[beltLength];
		for (int i = 0; i < segment.length; i++) {
			segment[i] = null;
		}
	}

	/**
	 * Put a bicycle on the belt.
	 * 
	 * @param bicycle
	 *            the bicycle to put onto the belt.
	 * @param index
	 *            the place to put the bicycle
	 * @throws InterruptedException
	 *             if the thread executing is interrupted.
	 */
	public synchronized void put(Bicycle bicycle, int index) throws InterruptedException {

		// while there is another bicycle in the way, block this thread
		while (segment[index] != null) {
			//System.out.println("first segement is taken up");
			wait();
		}

		// insert the element at the specified location
		segment[index] = bicycle;

		// make a note of the event in output trace
		System.out.println(bicycle + " arrived");

		// notify any waiting threads that the belt has changed
		notifyAll();
	}

	/**
	 * Take a bicycle off the end of the belt
	 * 
	 * @return the removed bicycle
	 * @throws DefException
	 *             if a defective bicycle makes it to the end of the belt without being inspected
	 * @throws InterruptedException
	 *             if the thread executing is interrupted
	 */
	public synchronized Bicycle getEndBelt() 
			throws InterruptedException,DefException {

		Bicycle bicycle;

		// while there is no bicycle at the end of the belt, block this thread
		while (segment[segment.length - 1] == null) {
			wait();
		}

		// get the next item
		bicycle = segment[segment.length - 1];
		// An exception that occurs when a defective bicycle makes it to the end of
		// the belt without being inspected
		if(bicycle.isDefective()&&!bicycle.hasInspected){
			String message=bicycle.id + "is defective but it is to the end of the belt without being inspected";
			throw new DefException(message);
		}
		else{
			segment[segment.length - 1] = null;

			// make a note of the event in output trace
			System.out.print(indentation + indentation);
			System.out.println(bicycle + " departed");

			// notify any waiting threads that the belt has changed
			notifyAll();
			return bicycle;
		}
	}

	/**
	 * Move the belt along one segment
	 * 
	 * @throws OverloadException
	 *             if there is a bicycle at position beltLength.
	 * @throws InterruptedException
	 *             if the thread executing is interrupted.
	 */
	public synchronized void move() 
			throws InterruptedException, OverloadException {
		// if there is something at the end of the belt,
		// or the belt is empty, do not move the belt
		// or there is a tagged bicycle, which is waiting for being taken off
		// from the belt
		while (isEmpty() || segment[segment.length - 1] != null || robotTakeOff) {
			wait();
		}

		// double check that a bicycle cannot fall of the end
		if (segment[segment.length - 1] != null) {
			String message = "Bicycle fell off end of " + " belt";
			throw new OverloadException(message);
		}

		// move the elements along, making position 0 null
		for (int i = segment.length - 1; i > 0; i--) {
			if (this.segment[i - 1] != null) {
				System.out.println(indentation + this.segment[i - 1] + " [ s" + (i) + " -> s" + (i + 1) + " ]");
			}
			segment[i] = segment[i - 1];
		}
		segment[0] = null;
		// System.out.println(indentation + this);

		// notify any waiting threads that the belt has changed
		notifyAll();
	}

	/**
	 * @return the maximum size of this belt
	 */
	public int length() {
		return beltLength;
	}

	/**
	 * Peek at what is at a specified segment
	 * 
	 * @param index
	 *            the index at which to peek
	 * @return the bicycle in the segment (or null if the segment is empty)
	 */
	public Bicycle peek(int index) {
		Bicycle result = null;
		if (index >= 0 && index < beltLength) {
			result = segment[index];
		}
		return result;
	}

	/**
	 * Check whether the belt is currently empty
	 * 
	 * @return true if the belt is currently empty, otherwise false
	 */
	private boolean isEmpty() {
		for (int i = 0; i < segment.length; i++) {
			if (segment[i] != null) {
				return false;
			}
		}
		return true;
	}

	public String toString() {
		return java.util.Arrays.toString(segment);
	}

	/*
	 * @return the final position on the belt
	 */
	public int getEndPos() {
		return beltLength - 1;
	}

	/**
	 * sensor identifies tagged bicycles
	 * 
	 * @throws InterruptedException
	 *             if the thread executing is interrupted.
	 */
	public synchronized void identifyTagged() 
			throws InterruptedException {
		// while there is no bicycle at the third segment,wait
		while (segment[Params.SENSOR_POSITION] == null||segment[Params.SENSOR_POSITION].hasInspected||robotTakeOff) {
			wait();
		}
		// sensor finds the tagged bicycle on the third segement
		if (segment[2].isTagged() && !robotTakeOff ) {
			System.out.println(indentation+"Sensor: Bicycle "+segment[Params.SENSOR_POSITION].id + " is tagged");
			robotTakeOff = true;
		}
		// notify any waiting threads that the belt has changed
		notifyAll();
	}
	/**
	 * robot takes off a taggged bicycle from the belt.
	 * 
	 * @throws SensorException
	 *             if one bicycle is taken off while actually it is not tagged.
	 * @throws InterruptedException
	 *             if the thread executing is interrupted.
	 */
	public synchronized void takeOffBicycle() 
			throws InterruptedException, SensorException {	
		// if a not-tagged bicycle is identified by the sensor
		if(!robotTakeOff || goBackBicycle!=null){
			return ;
		}
		if (!segment[Params.SENSOR_POSITION].isTagged()) {
			
			String message = "Sensor makes a mistake,  " + segment[Params.SENSOR_POSITION].id + " is not tagged";
			throw new SensorException(message);
		}
		//take off a tagged bicycle from the belt
		else {
			
			System.out.println(indentation+"Robot: robot takes off bicycle " + segment[Params.SENSOR_POSITION].id+" from the belt");
			takeOffBicycle = segment[Params.SENSOR_POSITION];
			segment[Params.SENSOR_POSITION] = null;
		}
		//tell that the robot is free again
		robotTakeOff = false;
		notifyAll();
	}
	/**
	 * inspector inspects the bicycle taken off by the robot.
	 * 
	 * @throws InterruptedException
	 *             if the thread executing is interrupted.
	 */
	public synchronized void inspectBicycle() 
			throws InterruptedException {
		// there is no bicycle waiting for inspection
		while (takeOffBicycle == null) {
			wait();
		}
		//move bicycle from robot to inspector
		goBackBicycle = takeOffBicycle;
		
		// remove 'tagged' if this bicycle is not defective
		if (!goBackBicycle.isDefective()) {
			goBackBicycle.setNotTagged();
		}
		// make inspectedBicycle with "hasInspected" to avoid a repeating inspection
		goBackBicycle.hasInspected = true;

		// release the robot
		takeOffBicycle = null;
	
        System.out.println(indentation+ "Inspector:"+goBackBicycle +" has been inspected and is waiting for putting back");
		// notify any waiting threads that the belt has changed
		notifyAll();

	}
	public synchronized boolean getTagged() {
		// TODO Auto-generated method stub
		return robotTakeOff;
	}

	public synchronized boolean getGoBackBicycle() {
		// if there is a bicycle staying in the inspector
		if(goBackBicycle==null){
			return false;
		}
		return true;
	}

	public synchronized void haveToWait()
			throws InterruptedException, SensorException {
		// TODO Auto-generated method stub
		wait();
	}

	public synchronized void resetGoBackBicycle() {
		// TODO Auto-generated method stub
		goBackBicycle=null;
		
	}
}
