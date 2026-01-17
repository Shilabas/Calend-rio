/**
 * Represents an iterator for a collection of Event objects,
 * allowing sequential traversal of the elements.
 * This iterator follows the standard pattern: initialization, checking for
 * the next element, and retrieving the next element.
 */
public class EventIterator {

    private Event[] events; // Array reference to the collection (or its copy)
    private int size;       // Number of valid elements in the array
    private int nextIndex;  // Index of the next event to return

    /**
     * Constructor: Initializes the iterator at the start of the sequence.
     * @param events The array containing the Event objects.
     * @param size The number of valid elements in the array.
     * @pre events != null
     * @pre size >= 0 && size <= events.length
     */
    public EventIterator(Event[] events, int size) {
        this.events = events;
        this.size = size;
        this.nextIndex = 0; // Positioned at the beginning
    }

    /**
     * Checks if there are more elements to visit.
     * @return true if the traversal is not complete (i.e., nextIndex < size), false otherwise.
     * @pre true
     */
    public boolean hasNext() {
        return nextIndex < size;
    }

    /**
     * Produces the next element in the sequence.
     * It retrieves the element and advances the index for the next call.
     * @return The next Event object in the sequence.
     * @pre hasNext().
     */
    public Event next() {
        // We rely on the pre-condition hasNext() being checked externally.
        return events[nextIndex++]; // Returns the element and advances the index
    }
}
