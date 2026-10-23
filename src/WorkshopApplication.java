import java.util.Scanner;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.time.format.DateTimeParseException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class WorkshopApplication {

    // Stores the SQLite database connection address
    private static final String DB_URL = "jdbc:sqlite:skills_workshop.db";

    public static void main(String[] args) {

        // Creates a Scanner object to read input from the user
        Scanner scanner = new Scanner(System.in);

        try (Connection connection = getDatabaseConnection()) {
            System.out.println("Database connection successful.");
        } catch (SQLException e) {
            System.out.println("Database connection failed.");
        }

        createDatabaseTables();

        // Stores workshop records in a generic list
        ArrayList<String> workshopRecords = new ArrayList<>();

        // Retrieves workshop records using the workshop ID
        HashMap<String, String> workshopMap = new HashMap<>();

        // Prevents duplicate workshop IDs
        HashSet<String> workshopIdSet = new HashSet<>();

        // Prevents duplicate participant IDs and email addresses
        HashSet<String> participantIdSet = new HashSet<>();
        HashSet<String> participantEmailSet = new HashSet<>();

        // Prevents duplicate participant-and-workshop registrations
        HashSet<String> registrationSet = new HashSet<>();

        // Stores participant records in generic collections
        ArrayList<String> participantRecords = new ArrayList<>();
        HashMap<String, String> participantMap = new HashMap<>();

        // Stores the fixed maximum number of standard workshops
        final int MAX_STANDARD_WORKSHOPS = 5;

        // Stores a fixed collection of standard workshop titles
        String[] workshopTitles = {
                "Introduction to Java",
                "Web Development Fundamentals",
                "Database Design",
                "Networking Basics",
                "Cybersecurity Awareness"
        };

        // Stores the category that belongs to each standard workshop
        WorkshopCategory[] workshopCategories = {
                WorkshopCategory.PROGRAMMING,
                WorkshopCategory.PROGRAMMING,
                WorkshopCategory.DATABASE,
                WorkshopCategory.NETWORKING,
                WorkshopCategory.CYBERSECURITY
        };

        // Stores workshop details needed for registration validation
        String[] workshopIds = new String[MAX_STANDARD_WORKSHOPS];
        boolean[] workshopActive = new boolean[MAX_STANDARD_WORKSHOPS];
        int[] workshopCapacities = new int[MAX_STANDARD_WORKSHOPS];
        int[] workshopRegistrationCounts = new int[MAX_STANDARD_WORKSHOPS];

        // Stores additional workshop details needed for reports
        String[] storedWorkshopTitles = new String[MAX_STANDARD_WORKSHOPS];
        LocalDate[] storedWorkshopDates = new LocalDate[MAX_STANDARD_WORKSHOPS];
        double[] storedWorkshopFees = new double[MAX_STANDARD_WORKSHOPS];

        int workshopCount = 0;

        // Stores participant IDs needed for registration validation
        String[] participantIds = new String[50];

        // Stores participant names needed for reports
        String[] participantNames = new String[50];

        int participantCount = 0;

        // Stores completed registrations to prevent duplicates
        String[] registeredParticipantIds = new String[100];
        String[] registeredWorkshopIds = new String[100];
        int registrationCount = 0;

        // Tracks whether information has changed and has not yet been saved
        boolean unsavedChanges = false;

        // Displays the application heading and basic startup information
        System.out.println("SKILLS WORKSHOP MANAGEMENT SYSTEM");
        System.out.println("Developed by: Charlean du Plessis");
        System.out.println("Version: 1.0");
        System.out.println("Date: 30 September 2026");

        // Adds a blank line to make the output easier to read
        System.out.println();

        // Displays a welcome message when the application starts
        System.out.println("Welcome to the Skills Workshop Management System.");

        // Stores the user's menu selection for the repeating menu
        int menuChoice;

        do {

        // Displays the main menu options
        System.out.println();
        System.out.println("MAIN MENU");
        System.out.println("1. Manage workshops");
        System.out.println("2. Manage participants");
        System.out.println("3. Register participant for workshop");
        System.out.println("4. Search records");
        System.out.println("5. Display registration summary");
        System.out.println("6. Export report");
        System.out.println("7. Load saved data");
        System.out.println("8. Database operations");
        System.out.println("9. Exit");

            // Handles invalid menu input using standard exception handling
            try {
                System.out.print("Enter your menu choice: ");
                menuChoice = scanner.nextInt();

            } catch (InputMismatchException e) {
                System.out.println("Invalid menu input. Please enter a number from 1 to 9.");

                // Removes the invalid input from the Scanner
                scanner.nextLine();

                menuChoice = 0;

            } finally {
                System.out.println();
            }

        // Displays the selected menu option
        System.out.println("You selected option: " + menuChoice);

            // Processes the selected main menu option
            switch (menuChoice) {
                case 1: {
                    System.out.println("Manage workshops selected.");

                    // Prevents more workshops from being stored than the array can hold
                    if (workshopCount >= MAX_STANDARD_WORKSHOPS) {
                        System.out.println("Maximum number of workshops reached.");
                        break;
                    }

                    // Clears the remaining newline after the menu number is entered
                    // Moved into switch - where it should live
                    scanner.nextLine();

                    // Captures workshop information from the user
                    System.out.println();
                    System.out.println("ENTER WORKSHOP INFORMATION");

                    System.out.print("Enter workshop ID: ");
                    String workshopId = scanner.nextLine();

                    // Prevents duplicate workshop IDs
                    if (workshopIdSet.contains(workshopId.toLowerCase())) {
                        System.out.println("A workshop with this ID already exists.");
                        break;
                    }

                    // Stores the workshop ID for duplicate checking
                    workshopIdSet.add(workshopId.toLowerCase());

                    System.out.print("Enter workshop title: ");
                    String workshopTitle = scanner.nextLine();

                    System.out.print("Enter facilitator name: ");
                    String facilitatorName = scanner.nextLine();

                    // Validates the workshop date and handles invalid date input
                    LocalDate workshopDate = null;
                    boolean validDate;

                    do {
                        validDate = true;

                        try {
                            System.out.print("Enter workshop date (YYYY-MM-DD): ");
                            workshopDate = LocalDate.parse(scanner.nextLine());

                            if (workshopDate.isBefore(LocalDate.now())) {
                                System.out.println("Invalid workshop date. The date may not be in the past.");
                                validDate = false;
                            }

                        } catch (DateTimeParseException e) {
                            System.out.println("Invalid date input. Please use the format YYYY-MM-DD.");
                            validDate = false;
                        }

                    } while (!validDate);


                    // Validates the workshop fee and handles non-numeric input
                    double workshopFee = 0.0;
                    boolean validFee;

                    do {
                        validFee = true;

                        try {
                            System.out.print("Enter workshop fee: R");
                            String workshopFeeInput = scanner.nextLine();

                            workshopFee =
                                    Double.parseDouble(workshopFeeInput.replace(',', '.'));

                            if (workshopFee < 0) {
                                System.out.println("Invalid workshop fee. The fee may not be negative.");
                                validFee = false;

                            } else if (workshopFee == 0) {
                                System.out.println("This workshop is free of charge.");

                            } else {
                                System.out.println("Workshop fee accepted.");
                            }

                        } catch (NumberFormatException e) {
                            System.out.println("Invalid fee input. Please enter a numeric value.");
                            validFee = false;
                        }

                    } while (!validFee);


                    // Validates workshop capacity and handles non-numeric input
                    int maximumCapacity = 0;
                    boolean validCapacity;

                    do {
                        validCapacity = true;

                        try {
                            System.out.print("Enter maximum capacity: ");
                            maximumCapacity = Integer.parseInt(scanner.nextLine());

                            if (maximumCapacity <= 0) {
                                System.out.println("Invalid capacity. Workshop capacity must be greater than zero.");
                                validCapacity = false;
                            }

                        } catch (NumberFormatException e) {
                            System.out.println("Invalid capacity input. Please enter a whole number.");
                            validCapacity = false;
                        }

                    } while (!validCapacity);


                    // Validates the registration quantity and handles non-numeric input
                    int numberOfRegistrations = 0;
                    boolean validRegistrationQuantity;

                    do {
                        validRegistrationQuantity = true;

                        try {
                            System.out.print("Enter number of registrations: ");
                            numberOfRegistrations = Integer.parseInt(scanner.nextLine());

                            if (numberOfRegistrations < 0) {
                                System.out.println("Invalid registration quantity. Registrations may not be negative.");
                                validRegistrationQuantity = false;

                            } else if (numberOfRegistrations > maximumCapacity) {
                                System.out.println("Invalid registration quantity. Registrations may not exceed workshop capacity.");
                                validRegistrationQuantity = false;
                            }

                        } catch (NumberFormatException e) {
                            System.out.println("Invalid registration quantity. Please enter a whole number.");
                            validRegistrationQuantity = false;
                        }

                    } while (!validRegistrationQuantity);

                    // Stores whether the workshop is active
                    System.out.print("Is the workshop active? (yes/no): ");
                    String activeInput = scanner.nextLine();

                    boolean isWorkshopActive = activeInput.equalsIgnoreCase("yes");

                    // Stores workshop information for registration validation
                    workshopIds[workshopCount] = workshopId;
                    workshopActive[workshopCount] = isWorkshopActive;
                    workshopCapacities[workshopCount] = maximumCapacity;
                    workshopRegistrationCounts[workshopCount] = numberOfRegistrations;

                    storedWorkshopTitles[workshopCount] = workshopTitle;
                    storedWorkshopDates[workshopCount] = workshopDate;
                    storedWorkshopFees[workshopCount] = workshopFee;

                    workshopCount++;

                    // Marks the application data as changed
                    unsavedChanges = true;

                    // Calculates the remaining spaces using the entered values
                    int availableSpaces = maximumCapacity - numberOfRegistrations;

                    // Displays the workshop information entered by the user
                    // Moved inside switch to remedy the scope dependency created
                    System.out.println();
                    System.out.println("WORKSHOP INFORMATION");
                    System.out.println("Workshop ID: " + workshopId);
                    System.out.println("Workshop Title: " + workshopTitle);
                    System.out.println("Facilitator: " + facilitatorName);
                    System.out.println("Workshop Date: " + workshopDate);
                    System.out.println("Workshop Fee: R" + workshopFee);
                    System.out.println("Maximum Capacity: " + maximumCapacity);
                    System.out.println("Number of Registrations: " + numberOfRegistrations);
                    System.out.println("Available Spaces: " + availableSpaces);

                    // Displays the constant used by the application
                    // Workshop list must also be inside the switch
                    System.out.println("Maximum Standard Workshops: " + MAX_STANDARD_WORKSHOPS);

                    // Displays all standard workshop titles using a loop
                    System.out.println();
                    System.out.println("STANDARD WORKSHOPS");

                    // Displays each workshop record using the title and category
                    for (int i = 0; i < workshopTitles.length; i++) {
                        System.out.println((i + 1) + ". "
                                + workshopTitles[i]
                                + " - "
                                + workshopCategories[i]);
                    }

                    // Requests a valid workshop number and repeats after invalid input
                    int workshopSelection;

                    do {
                        System.out.print("Select a workshop number from 1 to " + workshopTitles.length + ": ");
                        workshopSelection = scanner.nextInt();

                        if (workshopSelection < 1 || workshopSelection > workshopTitles.length) {
                            System.out.println("Invalid workshop selection. Please try again.");
                        }

                    } while (workshopSelection < 1 || workshopSelection > workshopTitles.length);

                    // Displays the valid workshop selection
                    System.out.println("Selected Workshop: " + workshopTitles[workshopSelection - 1]);
                    System.out.println("Workshop Category: " + workshopCategories[workshopSelection - 1]);

                    // Displays all available workshop categories
                    System.out.println();
                    System.out.println("WORKSHOP CATEGORIES");

                    WorkshopCategory[] categories = WorkshopCategory.values();

                    for (int i = 0; i < categories.length; i++) {
                        System.out.println((i + 1) + ". " + categories[i]);
                    }

                    // Allows the user to select a workshop category
                    System.out.print("Select a workshop category from 1 to " + categories.length + ": ");
                    int categorySelection = scanner.nextInt();

                    // Stores the selected workshop category if the number is valid
                    WorkshopCategory selectedCategory = null;

                    if (categorySelection >= 1 && categorySelection <= categories.length) {
                        selectedCategory = categories[categorySelection - 1];
                        System.out.println("Selected Category: " + selectedCategory);
                    } else {
                        System.out.println("Invalid workshop category selection.");
                    }

                    // Stores the complete workshop record in generic collections
                    if (selectedCategory != null) {

                        String workshopRecord =
                                "Workshop ID: " + workshopId
                                        + ", Title: " + workshopTitle
                                        + ", Category: " + selectedCategory
                                        + ", Facilitator: " + facilitatorName
                                        + ", Date: " + workshopDate
                                        + ", Fee: R" + workshopFee
                                        + ", Capacity: " + maximumCapacity
                                        + ", Registrations: " + numberOfRegistrations
                                        + ", Active: " + isWorkshopActive;

                        // Stores the workshop record in the ArrayList
                        workshopRecords.add(workshopRecord);

                        // Stores the workshop record using its ID as the key
                        workshopMap.put(workshopId, workshopRecord);

                        // Saves the workshop to the relational database
                        insertWorkshop(
                                workshopId,
                                workshopTitle,
                                selectedCategory.toString(),
                                facilitatorName,
                                workshopDate.toString(),
                                workshopFee,
                                maximumCapacity,
                                isWorkshopActive
                        );

                    }

                    // Filters and displays workshops that match the selected category
                    if (selectedCategory != null) {
                        System.out.println();
                        System.out.println("WORKSHOPS IN SELECTED CATEGORY");

                        boolean categoryMatchFound = false;

                        for (int i = 0; i < workshopTitles.length; i++) {
                            if (workshopCategories[i] == selectedCategory) {
                                System.out.println(workshopTitles[i]);
                                categoryMatchFound = true;
                            }
                        }

                        if (!categoryMatchFound) {
                            System.out.println("No workshops found in the selected category.");
                        }
                    }

                    break;
                }

                case 2: {
                    System.out.println("Manage participants selected.");

                    // Clears the remaining newline before text input continues
                    scanner.nextLine();

                    // Captures participant information from the user
                    System.out.println();
                    System.out.println("ENTER PARTICIPANT INFORMATION");

                    System.out.print("Enter participant ID: ");
                    String participantId = scanner.nextLine();

                    // Prevents duplicate participant IDs
                    if (participantIdSet.contains(participantId.toLowerCase())) {
                        System.out.println("A participant with this ID already exists.");
                        break;
                    }

                    // Ensures that the participant name is not blank
                    String participantName;

                    do {
                        System.out.print("Enter participant name: ");
                        participantName = scanner.nextLine();

                        if (participantName.trim().isEmpty()) {
                            System.out.println("Invalid participant name. The name may not be blank.");
                        }

                    } while (participantName.trim().isEmpty());

                    // Ensures that the participant email address is valid
                    String participantEmail;

                    do {
                        System.out.print("Enter participant email address: ");
                        participantEmail = scanner.nextLine();

                        if (!isValidEmail(formatEmail(participantEmail))) {
                            System.out.println("Invalid email address. Please enter a valid email address.");
                        }

                    } while (!isValidEmail(formatEmail(participantEmail)));

                    // Prevents duplicate participant email addresses
                    String duplicateCheckEmail = formatEmail(participantEmail);

                    if (participantEmailSet.contains(duplicateCheckEmail)) {
                        System.out.println("A participant with this email address already exists.");
                        break;
                    }

                    // Captures additional participant information
                    System.out.print("Enter telephone number: ");
                    String telephoneNumber = scanner.nextLine();

                    System.out.print("Enter participant type: ");
                    String participantType = scanner.nextLine();

                    System.out.print("Is the participant active? (yes/no): ");
                    String participantActiveInput = scanner.nextLine();

                    boolean participantActive =
                            participantActiveInput.equalsIgnoreCase("yes");

                    System.out.print("Enter registration status: ");
                    String registrationStatus = scanner.nextLine();

                    // Stores participant ID for registration validation
                    participantIds[participantCount] = participantId;

                    participantCount++;

                    // Marks the application data as changed
                    unsavedChanges = true;

                    // Processes the participant information using reusable string methods
                    // Moved inside switch to remedy the scope dependency created
                    String cleanedParticipantName = cleanText(participantName);
                    String formattedParticipantName = formatParticipantName(cleanedParticipantName);

                    // Stores the participant name for report generation
                    participantNames[participantCount - 1] = formattedParticipantName;

                    String formattedEmail = formatEmail(participantEmail);
                    boolean emailValid = isValidEmail(formattedEmail);

                    // Separates the formatted participant name into first name and surname
                    String[] participantNameParts = formattedParticipantName.split(" ", 2);

                    String firstName = participantNameParts[0];

                    String surname = participantNameParts.length > 1
                            ? participantNameParts[1]
                            : "";

                    // Creates the complete participant record
                    String participantCollectionRecord =
                            "Participant ID: " + participantId
                                    + ", First Name: " + firstName
                                    + ", Surname: " + surname
                                    + ", Email: " + formattedEmail
                                    + ", Telephone: " + telephoneNumber
                                    + ", Participant Type: " + participantType
                                    + ", Active: " + participantActive;

                    // Requests text to search for within the participant name
                    System.out.print("Enter part of the participant name to search for: ");
                    String participantSearch = scanner.nextLine();

                    boolean participantFound =
                            participantNameContains(formattedParticipantName, participantSearch);

                    // Requests a registration status for case-insensitive comparison
                    System.out.print("Enter a registration status to compare: ");
                    String statusComparison = scanner.nextLine();

                    boolean statusMatches =
                            compareText(registrationStatus, statusComparison);

                    // Stores the labels used when displaying the participant record
                    String[] participantLabels = {
                            "Participant ID",
                            "Participant Name",
                            "Participant Email",
                            "Registration Status"
                    };

                    // Stores the participant record fields for display
                    String[] participantRecord = {
                            participantId,
                            formattedParticipantName,
                            formattedEmail,
                            registrationStatus
                    };

                    // Displays the participant record using a loop
                    System.out.println();
                    System.out.println("PARTICIPANT RECORD");

                    for (int i = 0; i < participantRecord.length; i++) {
                        System.out.println(participantLabels[i] + ": " + participantRecord[i]);
                    }

                    // Displays the results of the string-processing operations
                    System.out.println();
                    System.out.println("STRING PROCESSING RESULTS");
                    System.out.println("Cleaned Name: " + cleanedParticipantName);
                    System.out.println("Formatted Name: " + formattedParticipantName);
                    System.out.println("Lowercase Email: " + formattedEmail);
                    System.out.println("Email Valid: " + emailValid);
                    System.out.println("Participant Search Match: " + participantFound);
                    System.out.println("Status Match: " + statusMatches);

                    // Confirms before deleting the participant record
                    System.out.print("Do you want to delete this participant record? (yes/no): ");
                    String deleteConfirmation = scanner.nextLine();

                    if (deleteConfirmation.equalsIgnoreCase("yes")) {

                        // Removes the participant record that was just stored
                        participantCount--;
                        participantIds[participantCount] = null;
                        participantNames[participantCount] = null;

                        // Marks the deletion as an unsaved change
                        unsavedChanges = true;

                        System.out.println("Participant record deleted successfully.");

                    } else {

                        // Stores the participant record in generic collections
                        participantRecords.add(participantCollectionRecord);
                        participantMap.put(participantId, participantCollectionRecord);

                        // Stores unique participant values for duplicate checking
                        participantIdSet.add(participantId.toLowerCase());
                        participantEmailSet.add(formattedEmail);

                        // Saves the participant to the relational database
                        insertParticipant(
                                participantId,
                                firstName,
                                surname,
                                formattedEmail,
                                telephoneNumber,
                                participantType,
                                participantActive
                        );

                        System.out.println("Participant record kept.");
                    }

                    break;
                }

                case 3: {
                    System.out.println("Register participant for workshop selected.");

                    // Clears the remaining newline after the menu number is entered
                    scanner.nextLine();

                    // Validates participant ID input before registration continues
                    String registrationParticipantId;

                    do {
                        System.out.print("Enter participant ID: ");
                        registrationParticipantId = scanner.nextLine().trim();

                        if (registrationParticipantId.isEmpty()) {
                            System.out.println("Invalid participant ID. The ID may not be blank.");
                        }

                    } while (registrationParticipantId.isEmpty());

                    boolean participantExists = false;

                    // Searches stored participant IDs
                    for (int i = 0; i < participantCount; i++) {
                        if (participantIds[i].equalsIgnoreCase(registrationParticipantId)) {
                            participantExists = true;
                            break;
                        }
                    }

                    // Stops registration if the participant ID does not exist
                    if (!participantExists) {
                        System.out.println("Registration cannot continue. Participant ID does not exist.");
                        break;
                    }

                    // Validates workshop ID input before registration continues
                    String registrationWorkshopId;

                    do {
                        System.out.print("Enter workshop ID: ");
                        registrationWorkshopId = scanner.nextLine().trim();

                        if (registrationWorkshopId.isEmpty()) {
                            System.out.println("Invalid workshop ID. The ID may not be blank.");
                        }

                    } while (registrationWorkshopId.isEmpty());

                    boolean workshopExists = false;
                    int workshopIndex = -1;

                    // Searches stored workshop IDs
                    for (int i = 0; i < workshopCount; i++) {
                        if (workshopIds[i].equalsIgnoreCase(registrationWorkshopId)) {
                            workshopExists = true;
                            workshopIndex = i;
                            break;
                        }
                    }

                    // Stops registration if the workshop ID does not exist
                    if (!workshopExists) {
                        System.out.println("Registration cannot continue. Workshop ID does not exist.");
                        break;
                    }

                    // Stops registration if the workshop is inactive
                    if (!workshopActive[workshopIndex]) {
                        System.out.println("Registration cannot continue. The workshop is inactive.");
                        break;
                    }

                    // Stops registration if the workshop is already full
                    if (workshopRegistrationCounts[workshopIndex]
                            >= workshopCapacities[workshopIndex]) {

                        System.out.println("Registration cannot continue. The workshop is full.");
                        break;
                    }

                    // Creates a unique participant-and-workshop registration key
                    String registrationKey =
                            registrationParticipantId.toLowerCase()
                                    + "|"
                                    + registrationWorkshopId.toLowerCase();

                    // Prevents duplicate participant-and-workshop registrations
                    if (registrationSet.contains(registrationKey)) {
                        System.out.println("Registration cannot continue. The participant is already registered for this workshop.");
                        break;
                    }

                    // Stores the successful registration
                    registeredParticipantIds[registrationCount] = registrationParticipantId;
                    registeredWorkshopIds[registrationCount] = registrationWorkshopId;

                    registrationCount++;

                    // Stores the unique registration combination
                    registrationSet.add(registrationKey);

                    // Marks the application data as changed
                    unsavedChanges = true;

                    // Updates the workshop registration count
                    workshopRegistrationCounts[workshopIndex]++;



                    // Displays confirmation of the successful registration
                    System.out.println("Registration completed successfully.");
                    System.out.println("Participant ID: " + registrationParticipantId);
                    System.out.println("Workshop ID: " + registrationWorkshopId);
                    System.out.println("Available Spaces: "
                            + (workshopCapacities[workshopIndex]
                            - workshopRegistrationCounts[workshopIndex]));

                    // Confirms before cancelling the registration
                    System.out.print("Do you want to cancel this registration? (yes/no): ");
                    String cancelConfirmation = scanner.nextLine();

                    if (cancelConfirmation.equalsIgnoreCase("yes")) {

                        // Removes the registration that was just created
                        registrationCount--;

                        // Removes the registration combination when the registration is cancelled
                        registrationSet.remove(registrationKey);

                        registeredParticipantIds[registrationCount] = null;
                        registeredWorkshopIds[registrationCount] = null;

                        // Restores the workshop registration count
                        workshopRegistrationCounts[workshopIndex]--;

                        // Marks the cancellation as an unsaved change
                        unsavedChanges = true;

                        System.out.println("Registration cancelled successfully.");

                    } else {

                        // Saves the registration to the relational database
                        createRegistration(
                                registrationParticipantId,
                                registrationWorkshopId,
                                LocalDate.now().toString(),
                                storedWorkshopFees[workshopIndex],
                                "Pending",
                                "Registered"
                        );

                        System.out.println("Registration kept.");
                    }

                    break;
                }

                case 4: {
                    System.out.println("Search records selected.");

                    // Clears the remaining newline before text input continues
                    scanner.nextLine();

                    // Retrieves a workshop from the HashMap using its ID
                    System.out.print("Enter workshop ID to retrieve: ");
                    String workshopIdSearch = scanner.nextLine();

                    if (workshopMap.containsKey(workshopIdSearch)) {
                        System.out.println("Workshop Record Found:");
                        System.out.println(workshopMap.get(workshopIdSearch));
                    } else {
                        System.out.println("No workshop found with ID: " + workshopIdSearch);
                    }

                    System.out.println();

                    // Allows the user to search for a workshop title
                    System.out.print("Enter a workshop title or part of a title to search for: ");
                    String workshopSearch = scanner.nextLine();

                    boolean workshopFound = false;

                    // Searches the workshop array for an exact or partial match
                    for (String title : workshopTitles) {
                        if (title.toLowerCase().contains(workshopSearch.toLowerCase())) {
                            System.out.println("Workshop Match Found: " + title);
                            workshopFound = true;
                        }
                    }

                    if (!workshopFound) {
                        System.out.println("No matching workshop found.");
                    }

                    break;
                }

                case 5: {
                    System.out.println("Display registration summary selected.");

                    // Sorts the standard workshops alphabetically by title
                    ArrayList<String> sortedWorkshopTitles = new ArrayList<>();

                    for (String title : workshopTitles) {
                        sortedWorkshopTitles.add(title);
                    }

                    sortedWorkshopTitles.sort(String.CASE_INSENSITIVE_ORDER);

                    System.out.println();
                    System.out.println("WORKSHOPS SORTED BY TITLE");

                    // Uses the reusable generic method to display the sorted workshops
                    displayItems(sortedWorkshopTitles);

                    // Counts and displays registrations for each stored workshop
                    System.out.println();
                    System.out.println("REGISTRATIONS BY WORKSHOP");

                    if (workshopCount == 0) {
                        System.out.println("No workshop registration information is available.");
                    } else {
                        for (int i = 0; i < workshopCount; i++) {
                            System.out.println(
                                    workshopIds[i]
                                            + ": "
                                            + workshopRegistrationCounts[i]
                                            + " registration(s)"
                            );
                        }
                    }

                    // Stores totals calculated across all workshops
                    int totalRegistrations = 0;
                    int totalAvailableSpaces = 0;
                    double totalExpectedIncome = 0.0;

                    System.out.println();
                    System.out.println("REGISTRATION SUMMARY");

                    // Loops through each standard workshop
                    for (int i = 0; i < workshopTitles.length; i++) {

                        System.out.println();
                        System.out.println("Workshop: " + workshopTitles[i]);

                        System.out.print("Enter maximum capacity: ");
                        int capacity = scanner.nextInt();

                        System.out.print("Enter number of registrations: ");
                        int registrations = scanner.nextInt();

                        System.out.print("Enter workshop fee: R");
                        double fee = scanner.nextDouble();

                        // Calculates the values for the current workshop
                        int spacesAvailable = capacity - registrations;
                        double expectedIncome = registrations * fee;

                        // Adds the current workshop values to the overall totals
                        totalRegistrations += registrations;
                        totalAvailableSpaces += spacesAvailable;
                        totalExpectedIncome += expectedIncome;

                        // Displays the registration count and calculations for the workshop
                        System.out.println("Registrations: " + registrations);
                        System.out.println("Available Spaces: " + spacesAvailable);
                        System.out.println("Expected Income: R" + expectedIncome);
                    }

                    // Displays the totals calculated by the loop
                    System.out.println();
                    System.out.println("OVERALL REGISTRATION TOTALS");
                    System.out.println("Total Registrations: " + totalRegistrations);
                    System.out.println("Total Available Spaces: " + totalAvailableSpaces);
                    System.out.println("Total Expected Income: R" + totalExpectedIncome);

                    // Clears the remaining newline before text input continues
                    scanner.nextLine();

                    // Confirms before clearing stored information
                    System.out.print("Do you want to clear all stored information? (yes/no): ");
                    String clearConfirmation = scanner.nextLine();

                    if (clearConfirmation.equalsIgnoreCase("yes")) {

                        // Clears stored workshop, participant and registration information
                        workshopCount = 0;
                        participantCount = 0;
                        registrationCount = 0;

                        // Clears the generic collections and duplicate-checking sets
                        workshopRecords.clear();
                        workshopMap.clear();
                        workshopIdSet.clear();

                        participantRecords.clear();
                        participantMap.clear();
                        participantIdSet.clear();
                        participantEmailSet.clear();

                        registrationSet.clear();

                        // Marks the cleared data as an unsaved change
                        unsavedChanges = true;

                        System.out.println("Stored information cleared successfully.");

                    } else {
                        System.out.println("Stored information kept.");
                    }

                    break;
                }

                case 6: {
                    System.out.println("Export report selected.");

                    // Creates and exports the workshop registration report
                    ArrayList<String> registrationReport =
                            createRegistrationReport(
                                    registeredParticipantIds,
                                    registeredWorkshopIds,
                                    registrationCount,
                                    participantIds,
                                    participantNames,
                                    participantCount,
                                    workshopIds,
                                    storedWorkshopTitles,
                                    storedWorkshopDates,
                                    storedWorkshopFees,
                                    workshopCapacities,
                                    workshopRegistrationCounts,
                                    workshopCount
                            );

                    exportReport("registration_report.csv", registrationReport);

                    break;
                }

                case 7: {
                    System.out.println("Load saved data selected.");

                    // Clears the remaining newline before text input continues
                    scanner.nextLine();

                    // Allows the user to choose a file containing saved information
                    System.out.print("Enter the file name to load: ");
                    String fileName = scanner.nextLine();

                    // Reads the saved information using the reusable file method
                    ArrayList<String> loadedRecords =
                            readInformationFromFile(fileName);

                    if (!loadedRecords.isEmpty()) {
                        System.out.println();
                        System.out.println("LOADED INFORMATION");

                        displayItems(loadedRecords);
                    }

                    break;
                }

                case 8: {
                    System.out.println();
                    System.out.println("DATABASE OPERATIONS");
                    System.out.println("1. Add participant");
                    System.out.println("2. Add workshop");
                    System.out.println("3. Create registration");
                    System.out.println("4. Display all workshops");
                    System.out.println("5. Display all participants");
                    System.out.println("6. Search participant");
                    System.out.println("7. Search workshop");
                    System.out.println("8. Update participant email");
                    System.out.println("9. Cancel registration");
                    System.out.println("10. Display participants for workshop");
                    System.out.println("11. Display registration counts");
                    System.out.println("12. Return to main menu");

                    System.out.print("Enter your database menu choice: ");
                    int databaseChoice = scanner.nextInt();
                    scanner.nextLine();

                    switch (databaseChoice) {

                        case 1: {
                            System.out.print("Enter participant ID: ");
                            String dbParticipantId = scanner.nextLine();

                            System.out.print("Enter first name: ");
                            String dbFirstName = scanner.nextLine();

                            System.out.print("Enter surname: ");
                            String dbSurname = scanner.nextLine();

                            System.out.print("Enter email: ");
                            String dbEmail = scanner.nextLine();

                            System.out.print("Enter telephone: ");
                            String dbTelephone = scanner.nextLine();

                            System.out.print("Enter participant type: ");
                            String dbParticipantType = scanner.nextLine();

                            System.out.print("Is the participant active? (yes/no): ");
                            String dbActiveInput = scanner.nextLine();
                            boolean dbParticipantActive =
                                    dbActiveInput.equalsIgnoreCase("yes");

                            insertParticipant(
                                    dbParticipantId,
                                    dbFirstName,
                                    dbSurname,
                                    dbEmail,
                                    dbTelephone,
                                    dbParticipantType,
                                    dbParticipantActive
                            );

                            break;
                        }

                        case 2: {
                            System.out.print("Enter workshop ID: ");
                            String dbWorkshopId = scanner.nextLine();

                            System.out.print("Enter workshop title: ");
                            String dbWorkshopTitle = scanner.nextLine();

                            System.out.print("Enter category: ");
                            String dbCategory = scanner.nextLine();

                            System.out.print("Enter facilitator name: ");
                            String dbFacilitator = scanner.nextLine();

                            System.out.print("Enter workshop date (YYYY-MM-DD): ");
                            String dbWorkshopDate = scanner.nextLine();

                            System.out.print("Enter workshop fee: ");
                            double dbWorkshopFee = scanner.nextDouble();

                            System.out.print("Enter maximum capacity: ");
                            int dbMaximumCapacity = scanner.nextInt();
                            scanner.nextLine();

                            System.out.print("Is the workshop active? (yes/no): ");
                            String dbWorkshopActiveInput = scanner.nextLine();
                            boolean dbWorkshopActive =
                                    dbWorkshopActiveInput.equalsIgnoreCase("yes");

                            insertWorkshop(
                                    dbWorkshopId,
                                    dbWorkshopTitle,
                                    dbCategory,
                                    dbFacilitator,
                                    dbWorkshopDate,
                                    dbWorkshopFee,
                                    dbMaximumCapacity,
                                    dbWorkshopActive
                            );

                            break;
                        }

                        case 3: {
                            System.out.print("Enter participant ID: ");
                            String dbRegistrationParticipantId = scanner.nextLine();

                            System.out.print("Enter workshop ID: ");
                            String dbRegistrationWorkshopId = scanner.nextLine();

                            System.out.print("Enter registration date (YYYY-MM-DD): ");
                            String dbRegistrationDate = scanner.nextLine();

                            System.out.print("Enter amount payable: ");
                            double dbAmountPayable = scanner.nextDouble();
                            scanner.nextLine();

                            System.out.print("Enter payment status: ");
                            String dbPaymentStatus = scanner.nextLine();

                            System.out.print("Enter registration status: ");
                            String dbRegistrationStatus = scanner.nextLine();

                            createRegistration(
                                    dbRegistrationParticipantId,
                                    dbRegistrationWorkshopId,
                                    dbRegistrationDate,
                                    dbAmountPayable,
                                    dbPaymentStatus,
                                    dbRegistrationStatus
                            );

                            break;
                        }

                        case 4: {
                            displayAllWorkshops();
                            break;
                        }

                        case 5: {
                            displayAllParticipants();
                            break;
                        }

                        case 6: {
                            System.out.print("Enter participant ID or email: ");
                            String dbParticipantSearch = scanner.nextLine();

                            searchParticipant(dbParticipantSearch);

                            break;
                        }

                        case 7: {
                            System.out.print("Enter workshop ID or title: ");
                            String dbWorkshopSearch = scanner.nextLine();

                            searchWorkshop(dbWorkshopSearch);

                            break;
                        }

                        case 8: {
                            System.out.print("Enter participant ID: ");
                            String dbUpdateParticipantId = scanner.nextLine();

                            System.out.print("Enter new email address: ");
                            String dbNewEmail = scanner.nextLine();

                            updateParticipantEmail(
                                    dbUpdateParticipantId,
                                    dbNewEmail
                            );

                            break;
                        }

                        case 9: {
                            System.out.print("Enter participant ID: ");
                            String dbDeleteParticipantId = scanner.nextLine();

                            System.out.print("Enter workshop ID: ");
                            String dbDeleteWorkshopId = scanner.nextLine();

                            deleteRegistration(
                                    dbDeleteParticipantId,
                                    dbDeleteWorkshopId
                            );

                            break;
                        }

                        case 10: {
                            System.out.print("Enter workshop ID: ");
                            String dbParticipantsWorkshopId = scanner.nextLine();

                            displayParticipantsForWorkshop(
                                    dbParticipantsWorkshopId
                            );

                            break;
                        }

                        case 11: {
                            displayRegistrationCounts();
                            break;
                        }

                        case 12: {
                            System.out.println("Returning to main menu.");
                            break;
                        }

                        default:
                            System.out.println("Invalid database menu choice.");

                    }

                    break;
                }

                case 9: {
                    System.out.println("Exit selected.");

                    // Confirms exit when unsaved information exists
                    if (unsavedChanges) {
                        scanner.nextLine();

                        System.out.print("Unsaved information exists. Are you sure you want to exit? (yes/no): ");
                        String exitConfirmation = scanner.nextLine();

                        if (!exitConfirmation.equalsIgnoreCase("yes")) {
                            System.out.println("Exit cancelled.");
                            menuChoice = 0;
                        } else {
                            System.out.println("Exit confirmed.");
                        }
                    }

                    break;
                }

                default:
                    System.out.println("Invalid menu option. Please select a number from 1 to 9.");
            }

        } while (menuChoice != 9);

        // Closes the Scanner when input is no longer needed
        scanner.close();
    }

    // Removes unnecessary leading and trailing spaces
    public static String cleanText(String text) {
        return text.trim();
    }

    // Converts a participant name into a consistent format
    public static String formatParticipantName(String name) {
        name = name.trim().toLowerCase();

        String[] nameParts = name.split(" ");
        String formattedName = "";

        for (String part : nameParts) {
            if (!part.isEmpty()) {
                formattedName += part.substring(0, 1).toUpperCase()
                        + part.substring(1) + " ";
            }
        }

        return formattedName.trim();
    }

    // Converts an email address to lowercase
    public static String formatEmail(String email) {
        return email.trim().toLowerCase();
    }

    // Checks whether an email address contains a basic valid structure
    public static boolean isValidEmail(String email) {
        email = email.trim();

        int atPosition = email.indexOf("@");

        return atPosition > 0
                && atPosition < email.length() - 1
                && email.substring(atPosition + 1).contains(".");
    }

    // Searches for part of a participant name without case sensitivity
    public static boolean participantNameContains(String participantName, String searchText) {
        return participantName.toLowerCase()
                .contains(searchText.toLowerCase());
    }

    // Compares two text values without case sensitivity
    public static boolean compareText(String firstValue, String secondValue) {
        return firstValue.equalsIgnoreCase(secondValue);
    }

    // Displays all items stored in a generic collection
    public static <T> void displayItems(ArrayList<T> items) {
        for (T item : items) {
            System.out.println(item);
        }
    }

    // Writes registration information to a file
    public static void writeRegistrationToFile(String fileName, String registrationInfo) {

        try (BufferedWriter writer =
                     new BufferedWriter(new FileWriter(fileName, true))) {

            writer.write(registrationInfo);
            writer.newLine();
            System.out.println("Registration information updated successfully: " + fileName);

        } catch (IOException e) {
            handleFileError("The registration information could not be written to the file.");
        }
    }

    // Reads workshop or participant information from a file
    public static ArrayList<String> readInformationFromFile(String fileName) {

        ArrayList<String> fileRecords = new ArrayList<>();

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(fileName))) {

            String line;

            while ((line = reader.readLine()) != null) {
                fileRecords.add(line);
            }

            System.out.println("File loaded successfully: " + fileName);

        } catch (FileNotFoundException e) {
            handleFileError("The file could not be found. Please check the file name and try again.");

        } catch (IOException e) {
            handleFileError("The file could not be read. Please check that the file is available and readable.");
        }

        return fileRecords;
    }

    // Exports report information to a file
    public static void exportReport(String fileName, ArrayList<String> reportLines) {

        try (BufferedWriter writer =
                     new BufferedWriter(new FileWriter(fileName))) {

            for (String line : reportLines) {
                writer.write(line);
                writer.newLine();
            }

            System.out.println("Registration report created and exported successfully: " + fileName);

        } catch (IOException e) {
            handleFileError("The report could not be exported.");
        }
    }

    // Displays a helpful message when a file operation fails
    public static void handleFileError(String message) {
        System.out.println(message);
    }

    // Creates the workshop registration report for export
    // Creates the complete workshop registration report for export
    public static ArrayList<String> createRegistrationReport(
            String[] registeredParticipantIds,
            String[] registeredWorkshopIds,
            int registrationCount,
            String[] participantIds,
            String[] participantNames,
            int participantCount,
            String[] workshopIds,
            String[] workshopTitles,
            LocalDate[] workshopDates,
            double[] workshopFees,
            int[] workshopCapacities,
            int[] workshopRegistrationCounts,
            int workshopCount){

        ArrayList<String> reportLines = new ArrayList<>();

        // Adds the report heading and generation date and time
        reportLines.add("WORKSHOP REGISTRATION REPORT");

        DateTimeFormatter dateTimeFormat =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        reportLines.add(
                "Generated: "
                        + LocalDateTime.now().format(dateTimeFormat)
        );

        reportLines.add("");

        // Adds the CSV column headings
        reportLines.add(
                "Participant ID,Participant Name,Workshop Title,"
                        + "Workshop Date,Amount Payable,Registration Status"
        );

        for (int i = 0; i < registrationCount; i++) {

            String participantName = "";
            String workshopTitle = "";
            LocalDate workshopDate = null;
            double amountPayable = 0.0;

            // Finds the participant name using the participant ID
            for (int j = 0; j < participantCount; j++) {
                if (participantIds[j].equalsIgnoreCase(
                        registeredParticipantIds[i])) {

                    participantName = participantNames[j];
                    break;
                }
            }

            // Finds the workshop details using the workshop ID
            for (int j = 0; j < workshopCount; j++) {
                if (workshopIds[j].equalsIgnoreCase(
                        registeredWorkshopIds[i])) {

                    workshopTitle = workshopTitles[j];
                    workshopDate = workshopDates[j];
                    amountPayable = workshopFees[j];
                    break;
                }
            }

            reportLines.add(
                    registeredParticipantIds[i] + ","
                            + participantName + ","
                            + workshopTitle + ","
                            + workshopDate + ","
                            + amountPayable + ","
                            + "Registered"
            );
        }

        // Calculates the report summary
        int totalAvailableSpaces = 0;
        double totalExpectedIncome = 0.0;

        for (int i = 0; i < workshopCount; i++) {

            int availableSpaces =
                    workshopCapacities[i] - workshopRegistrationCounts[i];

            totalAvailableSpaces += availableSpaces;

            totalExpectedIncome +=
                    workshopRegistrationCounts[i] * workshopFees[i];
        }

        // Adds the summary to the exported report
        reportLines.add("");
        reportLines.add("SUMMARY");
        reportLines.add("Total Workshops," + workshopCount);
        reportLines.add("Total Participants," + participantCount);
        reportLines.add("Total Registrations," + registrationCount);
        reportLines.add("Total Available Spaces," + totalAvailableSpaces);
        reportLines.add("Total Expected Income,R" + totalExpectedIncome);

        return reportLines;
    }

    // Inserts a new participant into the database
    public static void insertParticipant(
            String participantId,
            String firstName,
            String surname,
            String email,
            String telephone,
            String participantType,
            boolean active) {

        String sql =
                "INSERT INTO participants "
                        + "(participant_id, first_name, surname, email, telephone, participant_type, active) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = getDatabaseConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, participantId);
            statement.setString(2, firstName);
            statement.setString(3, surname);
            statement.setString(4, email);
            statement.setString(5, telephone);
            statement.setString(6, participantType);
            statement.setInt(7, active ? 1 : 0);

            statement.executeUpdate();

            System.out.println("Participant added to database successfully.");

        } catch (SQLException e) {
            System.out.println("Participant could not be added to the database.");
        }
    }

    // Inserts a new workshop into the database
    public static void insertWorkshop(
            String workshopId,
            String title,
            String category,
            String facilitator,
            String workshopDate,
            double fee,
            int maximumCapacity,
            boolean active) {

        String sql =
                "INSERT INTO workshops "
                        + "(workshop_id, title, category, facilitator, workshop_date, fee, maximum_capacity, active) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = getDatabaseConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, workshopId);
            statement.setString(2, title);
            statement.setString(3, category);
            statement.setString(4, facilitator);
            statement.setString(5, workshopDate);
            statement.setDouble(6, fee);
            statement.setInt(7, maximumCapacity);
            statement.setInt(8, active ? 1 : 0);

            statement.executeUpdate();

            System.out.println("Workshop added to database successfully.");

        } catch (SQLException e) {
            System.out.println("Workshop could not be added to the database.");
        }
    }

    // Creates a new workshop registration in the database
    public static void createRegistration(
            String participantId,
            String workshopId,
            String registrationDate,
            double amountPayable,
            String paymentStatus,
            String registrationStatus) {

        String sql =
                "INSERT INTO registrations "
                        + "(participant_id, workshop_id, registration_date, amount_payable, payment_status, registration_status) "
                        + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = getDatabaseConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, participantId);
            statement.setString(2, workshopId);
            statement.setString(3, registrationDate);
            statement.setDouble(4, amountPayable);
            statement.setString(5, paymentStatus);
            statement.setString(6, registrationStatus);

            statement.executeUpdate();

            System.out.println("Registration created successfully.");

        } catch (SQLException e) {
            System.out.println("Registration could not be created.");
        }
    }

    // Displays all workshops stored in the database
    public static void displayAllWorkshops() {

        String sql =
                "SELECT workshop_id, title, category, facilitator, workshop_date, "
                        + "fee, maximum_capacity, active "
                        + "FROM workshops";

        try (Connection connection = getDatabaseConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            System.out.println();
            System.out.println("ALL WORKSHOPS");

            while (resultSet.next()) {
                System.out.println(
                        "ID: " + resultSet.getString("workshop_id")
                                + " | Title: " + resultSet.getString("title")
                                + " | Category: " + resultSet.getString("category")
                                + " | Facilitator: " + resultSet.getString("facilitator")
                                + " | Date: " + resultSet.getString("workshop_date")
                                + " | Fee: R" + resultSet.getDouble("fee")
                                + " | Capacity: " + resultSet.getInt("maximum_capacity")
                                + " | Active: " + resultSet.getInt("active")
                );
            }

        } catch (SQLException e) {
            System.out.println("Workshops could not be retrieved from the database.");
        }
    }

    // Displays all participants stored in the database
    public static void displayAllParticipants() {

        String sql =
                "SELECT participant_id, first_name, surname, email, telephone, "
                        + "participant_type, active "
                        + "FROM participants";

        try (Connection connection = getDatabaseConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            System.out.println();
            System.out.println("ALL PARTICIPANTS");

            while (resultSet.next()) {
                System.out.println(
                        "ID: " + resultSet.getString("participant_id")
                                + " | Name: " + resultSet.getString("first_name")
                                + " " + resultSet.getString("surname")
                                + " | Email: " + resultSet.getString("email")
                                + " | Telephone: " + resultSet.getString("telephone")
                                + " | Type: " + resultSet.getString("participant_type")
                                + " | Active: " + resultSet.getInt("active")
                );
            }

        } catch (SQLException e) {
            System.out.println("Participants could not be retrieved from the database.");
        }
    }

    // Searches for a participant by ID or email
    public static void searchParticipant(String searchValue) {

        String sql =
                "SELECT participant_id, first_name, surname, email, telephone, "
                        + "participant_type, active "
                        + "FROM participants "
                        + "WHERE participant_id = ? OR email = ?";

        try (Connection connection = getDatabaseConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, searchValue);
            statement.setString(2, searchValue);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    System.out.println(
                            "ID: " + resultSet.getString("participant_id")
                                    + " | Name: " + resultSet.getString("first_name")
                                    + " " + resultSet.getString("surname")
                                    + " | Email: " + resultSet.getString("email")
                                    + " | Telephone: " + resultSet.getString("telephone")
                                    + " | Type: " + resultSet.getString("participant_type")
                                    + " | Active: " + resultSet.getInt("active")
                    );
                } else {
                    System.out.println("Participant not found.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Participant search failed.");
        }
    }

    // Searches for a workshop by ID or title
    public static void searchWorkshop(String searchValue) {

        String sql =
                "SELECT workshop_id, title, category, facilitator, workshop_date, "
                        + "fee, maximum_capacity, active "
                        + "FROM workshops "
                        + "WHERE workshop_id = ? OR title = ?";

        try (Connection connection = getDatabaseConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, searchValue);
            statement.setString(2, searchValue);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    System.out.println(
                            "ID: " + resultSet.getString("workshop_id")
                                    + " | Title: " + resultSet.getString("title")
                                    + " | Category: " + resultSet.getString("category")
                                    + " | Facilitator: " + resultSet.getString("facilitator")
                                    + " | Date: " + resultSet.getString("workshop_date")
                                    + " | Fee: R" + resultSet.getDouble("fee")
                                    + " | Capacity: " + resultSet.getInt("maximum_capacity")
                                    + " | Active: " + resultSet.getInt("active")
                    );
                } else {
                    System.out.println("Workshop not found.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Workshop search failed.");
        }
    }

    // Updates a participant's email address
    public static void updateParticipantEmail(
            String participantId,
            String newEmail) {

        String sql =
                "UPDATE participants "
                        + "SET email = ? "
                        + "WHERE participant_id = ?";

        try (Connection connection = getDatabaseConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, newEmail);
            statement.setString(2, participantId);

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Participant updated successfully.");
            } else {
                System.out.println("Participant not found.");
            }

        } catch (SQLException e) {
            System.out.println("Participant could not be updated.");
        }
    }

    // Cancels a participant's workshop registration
    public static void deleteRegistration(
            String participantId,
            String workshopId) {

        String sql =
                "DELETE FROM registrations "
                        + "WHERE participant_id = ? AND workshop_id = ?";

        try (Connection connection = getDatabaseConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, participantId);
            statement.setString(2, workshopId);

            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Registration cancelled successfully.");
            } else {
                System.out.println("Registration not found.");
            }

        } catch (SQLException e) {
            System.out.println("Registration could not be cancelled.");
        }
    }

    // Displays all participants registered for a selected workshop
    public static void displayParticipantsForWorkshop(String workshopId) {

        String sql =
                "SELECT p.participant_id, p.first_name, p.surname, p.email "
                        + "FROM participants p "
                        + "JOIN registrations r "
                        + "ON p.participant_id = r.participant_id "
                        + "WHERE r.workshop_id = ?";

        try (Connection connection = getDatabaseConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, workshopId);

            try (ResultSet resultSet = statement.executeQuery()) {

                System.out.println();
                System.out.println("PARTICIPANTS FOR WORKSHOP " + workshopId);

                boolean found = false;

                while (resultSet.next()) {
                    found = true;

                    System.out.println(
                            "ID: " + resultSet.getString("participant_id")
                                    + " | Name: " + resultSet.getString("first_name")
                                    + " " + resultSet.getString("surname")
                                    + " | Email: " + resultSet.getString("email")
                    );
                }

                if (!found) {
                    System.out.println("No participants found for this workshop.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Workshop participants could not be retrieved.");
        }
    }

    // Displays the number of registrations for each workshop
    public static void displayRegistrationCounts() {

        String sql =
                "SELECT w.workshop_id, w.title, COUNT(r.registration_id) AS registration_count "
                        + "FROM workshops w "
                        + "LEFT JOIN registrations r "
                        + "ON w.workshop_id = r.workshop_id "
                        + "GROUP BY w.workshop_id, w.title";

        try (Connection connection = getDatabaseConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            System.out.println();
            System.out.println("REGISTRATION COUNT PER WORKSHOP");

            while (resultSet.next()) {
                System.out.println(
                        "Workshop ID: " + resultSet.getString("workshop_id")
                                + " | Title: " + resultSet.getString("title")
                                + " | Registrations: " + resultSet.getInt("registration_count")
                );
            }

        } catch (SQLException e) {
            System.out.println("Registration counts could not be retrieved.");
        }
    }

    // Creates the database tables if they do not already exist
    public static void createDatabaseTables() {

        String participantsTable =
                "CREATE TABLE IF NOT EXISTS participants ("
                        + "participant_id TEXT PRIMARY KEY, "
                        + "first_name TEXT NOT NULL, "
                        + "surname TEXT NOT NULL, "
                        + "email TEXT NOT NULL UNIQUE, "
                        + "telephone TEXT, "
                        + "participant_type TEXT NOT NULL, "
                        + "active INTEGER NOT NULL"
                        + ")";

        String workshopsTable =
                "CREATE TABLE IF NOT EXISTS workshops ("
                        + "workshop_id TEXT PRIMARY KEY, "
                        + "title TEXT NOT NULL, "
                        + "category TEXT NOT NULL, "
                        + "facilitator TEXT NOT NULL, "
                        + "workshop_date TEXT NOT NULL, "
                        + "fee REAL NOT NULL, "
                        + "maximum_capacity INTEGER NOT NULL, "
                        + "active INTEGER NOT NULL"
                        + ")";

        String registrationsTable =
                "CREATE TABLE IF NOT EXISTS registrations ("
                        + "registration_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "participant_id TEXT NOT NULL, "
                        + "workshop_id TEXT NOT NULL, "
                        + "registration_date TEXT NOT NULL, "
                        + "amount_payable REAL NOT NULL, "
                        + "payment_status TEXT NOT NULL, "
                        + "registration_status TEXT NOT NULL, "
                        + "FOREIGN KEY (participant_id) REFERENCES participants(participant_id), "
                        + "FOREIGN KEY (workshop_id) REFERENCES workshops(workshop_id), "
                        + "UNIQUE (participant_id, workshop_id)"
                        + ")";

        try (Connection connection = getDatabaseConnection();
             Statement statement = connection.createStatement()) {

            statement.execute(participantsTable);
            statement.execute(workshopsTable);
            statement.execute(registrationsTable);

            System.out.println("Database tables created successfully.");

        } catch (SQLException e) {
            System.out.println("Database table creation failed.");
        }
    }

    // Creates and returns a connection to the SQLite database
    public static Connection getDatabaseConnection() throws SQLException {

        Connection connection = DriverManager.getConnection(DB_URL);

        // Enables SQLite foreign key enforcement for this connection
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }

        return connection;
    }

}