import java.io.*;
import java.util.Scanner;

/**
 * @author: 74256_74029
 * @author of discussion: Afonso Silva 74256 
 */

/**
 * The Main class serves as the application entry point and command interpreter.
 * It is solely responsible for handling user input (I/O) and delegating business logic
 * operations to the Calendar system class.
 * It defines all necessary command and output message constants.
 */
public class Main {

    // Commands: Defining static final string constants for all possible system commands.
    private static final String CMD_CREATE = "create";
    private static final String CMD_SCHEDULE = "schedule";
    private static final String CMD_CANCEL = "cancel";
    private static final String CMD_SHOW = "show";
    private static final String CMD_TOP = "top";
    private static final String CMD_EXIT = "exit";

    // User interaction messages (Output format definitions):
    // Defining all fixed output messages as constants ensures the application strictly.
    private static final String ERROR_MSG="File Not Found";
    private static final String MSG_EXIT = "Application exited.";
    private static final String MSG_INVALID_CMD = "Invalid command.";
    private static final String MSG_USER_ALREADY_REGISTERED = "User already registered.";
    private static final String MSG_USER_CREATED_SUCCESS = "User successfully created.";
    private static final String MSG_SOME_USER_NOT_REGISTERED = "Some user not registered.";
    private static final String MSG_EVENT_ALREADY_EXISTS = "Event already exists.";
    private static final String MSG_PROPOSER_NOT_AVAILABLE = "Proposer not available.";
    private static final String MSG_SOME_USER_NOT_AVAILABLE = "Some user not available.";
    private static final String MSG_EVENT_SCHEDULED_SUCCESS = "Event successfully created.";
    private static final String MSG_USER_NOT_REGISTERED = "User not registered.";
    private static final String MSG_EVENT_NOT_FOUND = "Event not found in calendar of %s.\n";
    private static final String MSG_NOT_PROPOSER = "User %s did not create event %s.\n";
    private static final String MSG_EVENT_CANCELED_SUCCESS = "Event successfully canceled.";
    private static final String MSG_NO_EVENTS = "User %s has no events.\n";
    private static final String MSG_SHOW_EVENT="%s, day %d, %d-%d, %d participants.\n";
    private static final String MSG_NO_GLOBAL_EVENTS = "No events registered.";

    // Constants used for command processing and validation ranges:
    // Index of the event creator/proponent in the users array.
    private static final int PROPOSER_NUMBER=0;
    // Used to check if a user has zero events.
    private static final int NO_EVENTS=0;
    // Minimum valid day of the week (Monday).

    /**
     * Processes the 'create' command by reading a user name and attempting to register it.
     * Handles the specific output messages based on whether the user already exists.
     * @param scanner Scanner object for reading input.
     * @param calendar The system object managing users and events.
     * @pre scanner != null && calendar != null
     */
    private static void processCreate(Scanner scanner,Calendar calendar){
        String user=scanner.next();

        // Checks constraint: User already registered.
        if(calendar.doesUserExist(user)){
            System.out.println(MSG_USER_ALREADY_REGISTERED);
        }
        else{
            calendar.addUser(user);
            System.out.println(MSG_USER_CREATED_SUCCESS);
        }
    }

    /**
     * Processes the 'schedule' command, reading all event and participant details,
     * and applying the necessary validation constraints in strict priority order.
     * @param scanner Scanner object for reading input
     * (reads across multiple lines for participants).
     * @param calendar The system object managing users and events.
     * @pre scanner != null && calendar != null
     */
    private static void processSchedule(Scanner scanner,Calendar calendar){
        String event= scanner.next();  // 1. Read event parameters.
        int day=scanner.nextInt();
        int startTime=scanner.nextInt();
        int endTime=scanner.nextInt();
        String[] eventUsers= new String[scanner.nextInt()]; // 2. Read participant list.
        for(int i=0;i<eventUsers.length;i++){
            eventUsers[i]=scanner.next();
        }  // Validation sequence starts
        if(calendar.doesAllUserExist(eventUsers)){
            if(!calendar.doesEventExist(event)){
                if(!calendar.isUserOccupied(day, startTime,endTime,eventUsers[PROPOSER_NUMBER])){
                    if(!calendar.areAllUserOccupied(day, startTime,endTime,eventUsers)){
                        calendar.addEvent(event,day,startTime,endTime,eventUsers);
                        System.out.println(MSG_EVENT_SCHEDULED_SUCCESS);
                    }
                    else{System.out.println(MSG_SOME_USER_NOT_AVAILABLE);}
                }
                else{System.out.println(MSG_PROPOSER_NOT_AVAILABLE);}
            }
            else{System.out.println(MSG_EVENT_ALREADY_EXISTS);}
        }
        else{System.out.println(MSG_SOME_USER_NOT_REGISTERED);}
    }

    /**
     * Processes the 'cancel' command, reading the event name and the proposer's name.
     * It enforces the required validation constraints
     * in priority order before cancelling the event.
     * @param scanner Scanner object for reading input.
     * @param calendar The system object managing events and users.
     * @pre scanner != null && calendar != null
     */
    private static void processCancel(Scanner scanner,Calendar calendar){
        // Read command parameters
        String event= scanner.next();
        String proposer=scanner.next();
        if(calendar.doesUserExist(proposer)){
            if(calendar.isEventInUserCalendar(event,proposer)){
                if(calendar.didUserCreateEvent(event,proposer,PROPOSER_NUMBER)){
                    calendar.cancelEvent(event);
                    System.out.println(MSG_EVENT_CANCELED_SUCCESS);
                }
                else{System.out.printf(MSG_NOT_PROPOSER,proposer,event);}

            }
            else{System.out.printf(MSG_EVENT_NOT_FOUND,proposer);}
        }
        else{System.out.println(MSG_USER_NOT_REGISTERED);}
    }

    /**
     * Lists all events associated with a specific user, displaying details in chronological order.
     * This implementation traverses the events collection using the Iterator pattern.
     * @param user The name of the user whose calendar is to be displayed.
     * @param calendar The system object managing events.
     * @pre user != null && calendar != null
     */
    private static void showUserEvents(String user,Calendar calendar){
        // Obtain the iterator from the system class (collection pattern).
        EventIterator it=calendar.iterator();

        // Iterate through all events stored in the system.
        while(it.hasNext()){
            Event j=it.next();

            // Filter: Check if the current user is a participant in this specific event.
            if(calendar.isUserInEvent(j,user)){
                // Extract event details using accessor methods (selectors).
                String event=j.getName();
                int day=j.getDay();
                int startTime=j.getStartTime();
                int endTime=j.getEndTime();
                int numUsers=j.getNumUsers();
                System.out.printf(MSG_SHOW_EVENT,event,day,startTime,endTime,numUsers);
            }
        }
    }

    /**
     * Processes the 'show' command. Reads the target user name, verifies user existence,
     * checks for events in the user's calendar, and displays them chronologically.
     * @param scanner Scanner object for reading input.
     * @param calendar The system object managing events and users.
     * @pre scanner != null && calendar != null
     */
    private static void processShow(Scanner scanner,Calendar calendar){
        String user=scanner.next();
        if(calendar.doesUserExist(user)){
            if(calendar.doesUserHaveEvents(user)){
                calendar.eventSort();

                // Display only the events involving this specific user.
                showUserEvents(user,calendar);
            }
            else{System.out.printf(MSG_NO_EVENTS,user);}
        }
        else{System.out.println(MSG_USER_NOT_REGISTERED);}
    }

    /**
     * Auxiliary method (private selector) used by processTop to traverse the (already sorted)
     * event collection and display only those events matching the given number of participants.
     * @param num The target number of participants (usually the maximum found globally).
     * @param calendar The system object containing the event collection.
     * @pre calendar != null
     */
    private static void showTop(int num,Calendar calendar){
        // Obtain a new iterator for traversal.
        EventIterator it=calendar.iterator();

        // Filter: Only process events with the required number of users.
        while(it.hasNext()){
            Event j=it.next();
            if(j.getNumUsers()==num){
                String event=j.getName();
                int day=j.getDay();
                int startTime=j.getStartTime();
                int endTime=j.getEndTime();
                int numUsers=j.getNumUsers();

                // Output is formatted according to specification
                // (chronological order guaranteed by prior sort).
                System.out.printf(MSG_SHOW_EVENT,event,day,startTime,endTime,numUsers);
            }
        }
    }

    /**
     * Processes the 'top' command, which lists all events with the maximum number of participants.
     * Ensures output is sorted chronologically (Day, StartTime).
     * @param calendar The system object managing events.
     * @pre calendar != null
     */
    private static void processTop(Calendar calendar){
        if(calendar.getEventNumber()!=NO_EVENTS){

            // Sort the global collection chronologically. This must happen before
            // identifying the maximum and displaying the events to meet the output requirement.
            calendar.eventSort();

            // Find the maximum number of participants and then display
            // all events matching that count.
            showTop(calendar.findMaxNumberOfUsers(),calendar);
        }
        else{System.out.println(MSG_NO_GLOBAL_EVENTS);}
    }

    /**
     * Processes the 'exit' command, terminating the application and printing the required message.
     */
    private static void processExit(){
        System.out.println(MSG_EXIT);
    }

    /**
     * Processes an unknown or invalid command input.
     * It consumes the remaining input on the line and outputs the invalid command message.
     * @param scanner Scanner object for reading input.
     * @pre scanner != null
     */
    private static void showUnknownCommand(Scanner scanner){
        // Consume the rest of the line associated with the invalid command
        // to prepare for the next command read.
        scanner.nextLine();
        System.out.println(MSG_INVALID_CMD);
    }

    /**
     * Executes the main command interpreter loop, reading commands from the input
     * source (standard input or file after initialization) and delegating execution
     * to the appropriate processing methods until the exit command is received.
     * @param scanner The Scanner object reading the input stream (System.in).
     * @param calendar The system class responsible for managing events and users.
     * @pre scanner != null && calendar != null
     */
    private static void executeOperations(Scanner scanner, Calendar calendar){
        String command;
        do {
            // Reads the next token, which is expected to be the command string.
            command = scanner.next();

            // Uses a switch statement to dispatch commands to auxiliary methods.
            switch (command) {
                case CMD_CREATE -> processCreate(scanner, calendar);
                case CMD_SCHEDULE -> processSchedule(scanner, calendar);
                case CMD_CANCEL -> processCancel(scanner, calendar);
                case CMD_SHOW -> processShow(scanner, calendar);
                case CMD_TOP -> processTop(calendar);
                case CMD_EXIT -> processExit();
                default -> showUnknownCommand(scanner);
            }
        } while (!command.equals(CMD_EXIT));
    }

    /**
     * Reads the initial list of users from the configuration file (via the provided Scanner)
     * and registers them in the system.
     * Assumes the file structure follows the specified format (number of users followed by names).
     * @param file The Scanner object linked to the input file stream.
     * @param calendar The system object managing user registration.
     * @pre file != null && calendar != null
     */
    private static void fileUser(Scanner file,Calendar calendar){
        // Reads the number of users to follow
        int num=file.nextInt();
        for(int i=0;i<num;i++){

            // Adds each user to the calendar system
            calendar.addUser(file.next());
        }
    }

    /**
     * Reads the initial list of events from the configuration file (via the provided Scanner)
     * and adds them to the calendar system.
     * Assumes the file content respects all domain constraints (e.g., non-conflicting schedules).
     * @param file The Scanner object linked to the input file stream.
     * @param calendar The system object managing events.
     * @pre file != null && calendar != null
     */
    private static void fileAddEvents(Scanner file,Calendar calendar){
        // Reads the number of events to follow
        int num=file.nextInt();
        for(int i=0;i<num;i++){
            // 1. Read event basic details
            String event= file.next();
            int day=file.nextInt();
            int startTime=file.nextInt();
            int endTime=file.nextInt();
            // 2. Read participant count and names
            String[] eventUsers= new String[file.nextInt()];
            for(int j=0;j<eventUsers.length;j++){
                eventUsers[j]=file.next();
            }
            // 3. Add event to the collection (bypassing the complex conflict checks,
            // as data from the initial file is typically assumed valid).
            calendar.addEvent(event,day,startTime,endTime,eventUsers);

        }
    }

    /**
     * Auxiliary method to read initial user and event data from a configuration file.
     * This method reads the file name from the standard input (Scanner scanner),
     * opens a stream to the file, and delegates the population of users and events
     * to auxiliary file methods. It explicitly declares {@code throws FileNotFoundException}
     * to delegate the exception handling to the caller, as recommended for methods
     * outside the main application entry point that handle file access.
     * @param scanner The Scanner used to read the file name from the console/input stream.
     * @param calendar The system object (Calendar class) to store the data.
     * @pre scanner != null && calendar != null
     * @throws FileNotFoundException If the file specified by filename
     * does not exist or is inaccessible.
     */
    private static void readFile(Scanner scanner, Calendar calendar)throws FileNotFoundException{
        // Reads the file name from the standard input stream.
        String filename=scanner.nextLine();
        // Creates a new Scanner linked to the FileReader, which can throw FileNotFoundException.
        Scanner fileStream=new Scanner(new FileReader(filename));
        // Delegates the reading of initial users from the file stream.
        fileUser(fileStream,calendar);
        // Delegates the reading of initial events from the file stream.
        fileAddEvents(fileStream,calendar);
        fileStream.close();
    }

    public static void main(String[] args)throws FileNotFoundException{
        Scanner reader = new Scanner(System.in);
        Calendar calendar = new Calendar();
        readFile(reader,calendar);
        executeOperations(reader, calendar);
        reader.close();
    }
}