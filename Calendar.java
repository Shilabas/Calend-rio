/**
 * The Calendar class serves as the main system class (or domain class)
 * responsible for managing the collection of registered users and the overall
 * collection of scheduled events within the shared calendar system.
 * It manages array capacity dynamically for events and holds user data statically.
 */
public class Calendar {

    // Instance Variables (State)
    private String[] users;
    private int numUsers; // Number of currently registered users
    private int size;  // Number of valid Event objects stored in the events array
    private Event[] events;   // Array reference to the collection of events

    // Constant
    private final int MAX_USERS = 100;// Constraint given for maximum number of users

    /**
     * Constructor: Initializes the collections (users array and events array)
     * and initializes size counters to zero.
     */
    public Calendar() {
        // Initializes the users array with the fixed maximum capacity.
        users = new String[MAX_USERS];
        numUsers = 0;
        // Initializes the events array.
        size=0;
        events = new Event[size];
    }

    /**
     * Checks if a specific user exists within the stored collection of users.
     * This method performs a linear search through the array up to numUsers.
     * @param user The username (String) to search for.
     * @return true if the user is found, false otherwise.
     */
    public boolean doesUserExist(String user){
        boolean result=false;
        int i=0;
        while (i<numUsers&&!result) {
            if(users[i].equals(user)){
                result=true;
            }
            i++;
        }
        return result;
    }

    /**
     * Adds a new user name to the system.
     * @param user The name of the user to be registered.
     * @pre numUsers < MAX_USERS (Space must be available to add a user).
     * @pre !doesUserExist(user)
     * The user must not already be registered,
     * although this check is typically handled by the client/
     * Main class before calling this method,
     * as per event constraints.
     */
    public void addUser(String user){
        // Adds the user at the current 'numUsers' position and then increments the counter.
        users[numUsers++]=user;
    }

    /**
     * Auxiliary method (private selector) to check if the internal event array
     * needs to be resized to accommodate a new element.
     * @return true if the array capacity (events.length) is equal to the number
     * of valid elements (size), false otherwise.
     */
    private boolean isFull(){
        return size==events.length;
    }

    /**
     * Auxiliary method (private mutator) to dynamically increase the capacity
     * of the internal event array.
     * @pre events.lenhght != null
     */
    private void grow(){
        // Creates a new, larger array (current size + 1)
        Event[] temporary=new Event[events.length+1];

        // Copies all existing elements to the new array
        for (int i=0; i<size;i++){
            temporary[i]=events[i];
        }
        // Updates the reference to point to the new, larger array
        events=temporary;
    }

    /**
     * Adds a new event to the collection. If the internal array is full, it is
     * resized before insertion.
     * @param event The name of the event (unique identifier).
     * @param day The day of the week (1-5).
     * @param startTime The start hour (8-19).
     * @param endTime The end hour (9-20).
     * @param eventUsers Array containing the names of the participants (proponent first).
     * @pre All required parameters must be valid
     * (checked outside/before this method, typically in the Main class,
     * according to the schedule constraints).
     * @pre The event name must be unique (Event already exists check).
     * @pre All participating users must be available
     * (Proposer not available/Some user not available checks).
     */
    public void addEvent(String event,int day,int startTime,int endTime,String[] eventUsers){
        if (isFull()){
            grow(); // Resizes the array capacity
        }
        // Creates a new Event object and inserts it at the first available position (size)
        // Then increments size
        events[size++]=new Event(event, day, startTime,endTime,eventUsers);
    }

    /**
     * Checks if all users provided in the array are currently registered in the system.
     * This validation is crucial before scheduling a new event
     * (e.g., check for "Some user not registered" constraint).
     * @param eventUsers An array containing the names of the users to be checked.
     * @return true if all users exist in the system, false otherwise.
     * @pre eventUsers != null
     */
    public boolean doesAllUserExist(String[] eventUsers){
        int result=0;

        // Iterates through the input array to check each user individually.
        for(int i=0;i<eventUsers.length;i++){
            // Assumes doesUserExist is a previously defined method in the Calendar class.
            if(doesUserExist(eventUsers[i])){
                result++;
            }
        }
        // Returns true only if the count of existing users matches the total expected users.
        // This leverages linear search functionality.
        return (result==eventUsers.length);
    }

    /**
     * Checks if an event with the specified name exists within the stored collection of events.
     * This method performs a linear search (sequential search) up to 'size' elements.
     * @param name The name (String) of the event to search for.
     * @return true if an event with the matching name is found; false otherwise.
     * @pre name != null
     */
    public boolean doesEventExist(String name){
        boolean result=false;
        int i=0;
        while (i<size&&!result) {
            if(name.equals(events[i].getName())){
                result=true;
            }
            i++;
        }
        return result;
    }

    /**
     * Auxiliary method (private selector) to determine if two time intervals overlap.
     * The method checks for temporal conflict between an existing slot [sT1, eT1)
     * and a proposed slot [sT2, eT2).
     * @param sT1 Start time of the first interval (Existing Event Start Time).
     * @param eT1 End time of the first interval (Existing Event End Time).
     * @param sT2 Start time of the second interval (Proposed Slot Start Time).
     * @param eT2 End time of the second interval (Proposed Slot End Time).
     * @return true if the intervals overlap, false otherwise.
     * @pre sT1 < eT1 && sT2 < eT2 (Time intervals must be valid).
     */
    private boolean isWithinTime(int sT1,int eT1, int sT2,int eT2){
        return (sT1<=sT2&&eT1>sT2||sT1<eT2&&eT1>=eT2||sT1>sT2&&eT1<eT2);
    }

    /**
     * Checks if a specific user is currently participating in the given event.
     * This method performs a linear search over the event's participant list,
     * using methods provided by the Event object (getNumUsers and getUser).
     * @param event The Event object whose participants are to be checked.
     * @param user The username (String) to search for in the event's participants.
     * @return true if the user is found in the event's participant list; false otherwise.
     * @pre event != null && user != null
     */
    public boolean isUserInEvent(Event event, String user){
        boolean result=false;
        int i=0;
        while (i<event.getNumUsers()&&!result) {
            if(user.equals(event.getUser(i))){
                result=true;
            }
            i++;
        }
        return result;
    }

    /**
     * Checks if a specific user is occupied (has an event scheduled) during a given time slot.
     * This method performs a linear search across all registered events.
     * @param day The day of the week (integer, typically 1 to 5).
     * @param startTime The proposed start time (integer, typically 8 to 19).
     * @param endTime The proposed end time (integer, typically 9 to 20).
     * @param user The username (String) to check for availability.
     * @return true if the user is a participant in an event
     * that overlaps the specified time slot on the given day; false otherwise.
     * @pre day >= 1 && day <= 5 && startTime >= 8 && endTime <= 20
     * && startTime < endTime && user != null
     */
    public boolean isUserOccupied(int day, int startTime, int endTime,String user){
        boolean result=false;
        int i=0;
        while (i<size&&!result) {
            if(events[i].getDay()==day&&isWithinTime(events[i].getStartTime(),
                    events[i].getEndTime(),startTime,endTime)&&isUserInEvent(events[i],user)){
                result=true;
            }
            i++;
        }
        return result;
    }

    /**
     * Checks if any of the specified users (starting from index 1) are occupied
     * during the proposed time slot. This method implements a linear search
     * to detect the first conflict among the potential participants (guests).
     * @param day The day of the week (integer, 1 to 5).
     * @param startTime The proposed start time (integer, 8 to 19).
     * @param endTime The proposed end time (integer, 9 to 20).
     * @param eventUsers An array of usernames (Strings). The search starts from index 1,
     * typically excluding the event proposer (index 0), as per
     * the collective event scheduling logic.
     * @return true if at least one user
     * (from index 1 onward) is occupied (conflict detected), false otherwise.
     * @pre eventUsers != null
     */
    public boolean areAllUserOccupied(int day, int startTime, int endTime,String[] eventUsers){
        boolean result=false;
        int i=1;
        while (i<eventUsers.length&&!result) {
            if(isUserOccupied(day, startTime,endTime,eventUsers[i])){
                result=true;
            }
            i++;
        }
        return result;
    }

    /**
     * Checks if a specific named event includes a specific user as a participant.
     * @param event The name of the event to check.
     * @param user The name of the user whose calendar is being inspected.
     * @return true if the event exists and the user is a participant, false otherwise.
     * @pre event != null && user != null
     * @pre The event must exist in the system (i.e., doesEventExist(event) is true).
     */
    public boolean isEventInUserCalendar(String event, String user) {
        boolean result = false;
        // Finds the internal storage index of the event.
        Event eventMatched = events[searchIndex(event)];

        // Secondary check ensuring the retrieved event matches the name
        // (necessary if searchIndex returns 0 when not found).
        if (event.equals(eventMatched.getName())) {
            if (isUserInEvent(eventMatched, user)) {
                result = true;
            }
        }
        return result;
    }

    /**
     * Checks if a given user is the creator (proponent) of a specific event.
     * The proponent is defined as the user at the specified participant index
     * (conventionally index 0).
     * @param event The name of the event.
     * @param user The name of the user to check.
     * @param num The index position of the proponent (expected to be 0).
     * @return true if the user matches the proponent of the event, false otherwise.
     * @pre event != null && user != null
     * @pre The event must exist in the system (i.e., doesEventExist(event) is true).
     * @pre num is a valid index for the user array of the event (typically num=0).
     */
    public boolean didUserCreateEvent(String event,String user,int num){
        boolean result=false;
        Event eventMatched=events[searchIndex(event)];
        if(event.equals(eventMatched.getName())){
            if(user.equals(eventMatched.getUser(num))){
                result=true;
            }
        }
        return result;
    }

    /**
     * Searches for the array index corresponding to an event with the given name.
     * This is a private auxiliary method, typically used internally by a system
     * or collection class.
     * It performs a Linear Search up to 'size' elements.
     * @param event The name (String) of the event to search for.
     * @return The index (int) where the event is located.
     * If the event is not found, the function returns 0 (based on the initial
     * assignment of 'num', potentially conflicting with the standard NOT_FOUND = -1).
     * @pre event != null
     */
    private int searchIndex(String event){
        boolean result=false;
        int i=0;
        int num=0;
        while (i<size&&!result) {
            if(event.equals(events[i].getName())){
                result=true;
                num=i;
            }
            i++;
        }
        return num;
    }

    /**
     * Removes an event from the collection using the array shift/swap technique
     * (the element at the found index is replaced by the last element in the collection).
     * This method is a mutator, reducing the size of the event collection.
     * @param event The name of the event to be cancelled.
     * @pre The event must exist in the collection
     * (checked by the caller before invoking this method).
     */
    public void cancelEvent(String event){
        // Replace the event to be cancelled with the last element of the array
        events[searchIndex(event)]=events[size-1];
        // Decrement the size counter, effectively removing the event
        size--;
    }

    /**
     * Checks if the specified user is participating in any event within the current collection.
     * This method performs a linear search (sequential search) through all registered events.
     * @param user The username (String) to check for event participation.
     * @return true if the user is found in the participant list of at least one event;
     * false otherwise.
     * @pre user != null.
     */
    public boolean doesUserHaveEvents(String user){
        boolean result=false;
        int i=0;
        while (i<size&&!result) {
            if(isUserInEvent(events[i],user)){
                result=true;
            }
            i++;
        }
        return result;
    }

    /**
     * Returns the total number of events currently registered in the system.
     * This corresponds to the number of valid elements ('size') in the internal events array.
     * @return The number of events (size).
     */
    public int getEventNumber(){
        return size;
    }

    /**
     * Auxiliary method (private selector) to compare two events chronologically.
     * The sorting criteria is based on: 1) Day (ascending), and 2) Start Time (ascending).
     * This logic defines the chronological order required for output.
     * @param event1 The first event to compare.
     * @param event2 The second event to compare.
     * @return true if event1 is scheduled chronologically earlier than event2, false otherwise.
     * @pre event1 != null && event2 != null
     */
    private boolean isTheFirstEventEarlierThanTheSecond(Event event1,Event event2){
        return(event1.getDay()<event2.getDay()||event1.getDay()==event2.getDay()&&
                event1.getStartTime()<event2.getStartTime());
    }

    /**
     * Auxiliary method (private selector) used by the Selection Sort algorithm.
     * Finds the index of the chronologically earliest event (minimum)
     * within a specified unsorted range [index, size).
     * @param index The starting index of the sub-array to search (inclusive).
     * @param size The exclusive upper bound of the search range (the end of the valid elements).
     * @return The index of the earliest event found in the range.
     * @pre 0 <= index < size
     */
    private int findEarliestEventIndex(int index,int size){
        int minIndex=index;
        // Search the unsorted sub-array
        for (int i=index+1; i < size; i++){
            if (isTheFirstEventEarlierThanTheSecond(events[i],events[minIndex])){
                minIndex = i;
            }
        }
        return minIndex;
    }

    /**
     * Auxiliary method (private mutator) to swap two event objects in the internal events array.
     * @param index1 The index of the first event.
     * @param index2 The index of the second event.
     * @pre index1 >= 0 && index1 < this.size && index2 >= 0 && index2 < this.size
     */
    private void swap(int index1, int index2) {
        Event tmp = events[index1];
        events[index1] =events[index2];
        events[index2] = tmp;
    }

    /**
     * Sorts the collection of events chronologically (by day, then by start time)
     * using the **Selection Sort** algorithm.
     * This sorting is necessary to fulfill the ordering requirement
     * for the 'show' and 'top' commands.
     */
    public void eventSort(){
        // The outer loop iterates size-1 times, gradually expanding the sorted portion.
        for (int i=0;i<size-1;i++){
            // Find the minimum element's index in the unsorted remainder.
            // Swap the minimum element with the current starting position of the unsorted portion.
            swap(i,findEarliestEventIndex(i,size));
        }
    }

    /**
     * Finds the largest number of participants associated with any event registered in the system.
     * This is used by the 'top' command to identify events with the maximum number of users.
     * @return The maximum number of participants found across all events.
     */
    public int findMaxNumberOfUsers(){
        int num=0;
        // Linear traversal to find the maximum value
        for (int i=0;i<size;i++){
            num=Math.max(num,events[i].getNumUsers());
        }
        return num;
    }

    /**
     * Provides an iterator object allowing sequential access (traversal)
     * to the collection of events without exposing the internal array structure,
     * following the Iterator Pattern.
     * @return A new instance of EventIterator, initialized to traverse the stored events.
     * @pre true (Always runnable).
     */
    public EventIterator iterator(){
        // Creates a new iterator, providing it with the underlying array and the size.
        return new EventIterator(events, size);
    }
}