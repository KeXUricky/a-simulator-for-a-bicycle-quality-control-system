/**
 * An exception that a not-tagged bicycle is found by sensor and taken off by Robot:
 */
public class SensorException extends HandlingException {
    /**
     * Create a new OverloadException with a specified message.
     */
    public SensorException(String message) {
        super(message);
    }
}