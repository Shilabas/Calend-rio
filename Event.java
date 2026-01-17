/**
 * Handles the information concerning a scheduled event in the Calendar.
 */
public class Event {

    private String name;
    private int day; //  Day of the week (1 to 5)
    private int startTime; // // Event start time (8 to 19)
    private int endTime; // Event end time (9 to 20)
    private String[] users;

    /**
     * Constructs a new Event object.
     * @param name The unique name of the event.
     * @param day The day of the week (1=Mon, 5=Fri).
     * @param startTime The start time of the event.
     * @param endTime The end time of the event.
     * @param users The list of participating users (proponent first).
     * @pre name != null && users != null
     * && day >= 1 && day <= 5 && startTime >= 8 && endTime <= 20 && startTime < endTime.
     */
    public Event(String name, int day, int startTime, int endTime, String[] users) {
        this.name = name;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.users = users;
    }

    //--------Accessor methods (Selectors)--------
    /**
     * Returns the unique name of the event.
     * @return The event name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the day of the week when the event occurs (1 to 5).
     * Precondition is guaranteed by the constructor.
     * @return The day of the week.
     */
    public int getDay() {
        return day;
    }

    /**
     * Returns the start time of the event (8 to 19).
     * Precondition is guaranteed by the constructor.
     * @return The beginning time of the event.
     */
    public int getStartTime() {
        return startTime;
    }

    /**
     * Returns the end time of the event (9 to 20).
     * Precondition is guaranteed by the constructor.
     * @return The ending time of the event.
     */
    public int getEndTime() {
        return endTime;
    }

    /**
     * Returns the total number of users participating in the event.
     * @return The number of participants (length of the users array).
     */
    public int getNumUsers() {
        return users.length;
    }

    /**
     * Returns the name of a specific user participating in the event.
     * @param num The index of the user in the internal array (0 <= num < getNumUsers()).
     * @return The user's name.
     * @pre num >= 0 && num < users.length
     */
    public String getUser(int num){
        return users[num];
    }
}