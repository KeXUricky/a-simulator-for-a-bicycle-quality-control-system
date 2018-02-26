/**
 * The bicycle quality control short belt
 */
public class ShortBelt {

	// the items in the belt segments
	protected Bicycle[] segment;

	// the length of this belt
	protected int beltLength = 2;
	
	// to help format output trace
	final private static String indentation = "                  ";
	/**
	 * Create a new, empty short belt, initialised as empty
	 */
	public ShortBelt() {
		segment = new Bicycle[beltLength];
		for (int i = 0; i < segment.length; i++) {
			segment[i] = null;
		}
	}

	/**
	 * Put a bicycle on the short belt.
	 * 
	 * @param bicycle
	 *            the bicycle to put onto the short belt.
	 * @param index
	 *            the place to put the bicycle
	 * @throws InterruptedException
	 *             if the thread executing is interrupted.
	 */
	public synchronized void put(Bicycle bicycle, int index) 
			throws InterruptedException {
		// insert the element at the specified location
		segment[index] = bicycle;
		
		// make a note of the event in output trace
		System.out.println(indentation+"Robot: robot puts "+bicycle.id+" on the shortbelt");
		System.out.println("ShortBelt: "+bicycle + " arrived");
		notifyAll();
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
		// if there is something at the end of the shortbelt,
		// or the shortbelt is empty, do not move the shortbelt
		while (isEmpty() || segment[segment.length - 1] != null) {
			wait();
		}

		// double check that a bicycle cannot fall of the end
		if (segment[segment.length - 1] != null) {
			String message = "Bicycle fell off end of " + " short belt";
			throw new OverloadException(message);
		}

		// move the elements along, making position 0 null
		for (int i = segment.length - 1; i > 0; i--) {
			if (this.segment[i - 1] != null) {
				System.out.println(indentation + "shortBelt move: "+ this.segment[i - 1] + " [ s" + (i) + " -> s" + (i + 1) + " ]");
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
	 * Take a bicycle off the end of the shortbelt
	 * 
	 * @return the removed bicycle
	 * @throws DefException
	 *             if a defective bicycle makes it to the end of the shortbelt without being inspected
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
			System.out.println("shortBelt: "+bicycle + " departed");

			// notify any waiting threads that the belt has changed
			notifyAll();
			return bicycle;
		}
	}
}
