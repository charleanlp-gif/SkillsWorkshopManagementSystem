# Skills Workshop Management System

## Application Purpose

The Skills Workshop Management System is a Java console application 
used to manage workshops, participants and workshop registrations. 
The application allows information to be captured, searched, displayed, 
exported to files and stored in a relational SQLite database.

## Major Application Functions

The application provides the following main functions:

- Manage workshops.
- Manage participants.
- Register participants for workshops.
- Search workshop and participant information.
- Display registration summaries.
- Export registration reports.
- Load information from files.
- Perform relational database operations.
- Exit the application safely.

The database functions allow users to:

- Add participants.
- Add workshops.
- Create registrations.
- Display workshops and participants.
- Search participants by ID or email.
- Search workshops by ID or title.
- Update participant information.
- Cancel registrations.
- Display participants registered for a selected workshop.
- Display the number of registrations for each workshop.

## Required Software

The following software is required:

- Java Development Kit (JDK).
- IntelliJ IDEA or another Java IDE.
- SQLite JDBC driver.

## Java Version

The application was developed and tested using:

Java JDK 21

## Database Software

The application uses SQLite as the relational database.

The SQLite JDBC driver used by the project is stored in the `lib` folder.

Database file:

`skills_workshop.db`

## Database Setup

The database structure is defined in:

`database.sql`

The database contains the following tables:

- participants
- workshops
- registrations

Primary keys, foreign keys, required fields and unique constraints are used to 
maintain data integrity.

The combination of participant ID and workshop ID is unique in the registrations 
table to prevent a participant from being registered for the same workshop more than once.

SQLite foreign-key enforcement is enabled when a database connection is created.

## Compiling the Project

When using IntelliJ IDEA:

1. Open the Skills Workshop Management System project.
2. Confirm that JDK 21 is selected as the project SDK.
3. Confirm that the SQLite JDBC JAR file in the `lib` folder is included as a project dependency.
4. Open `WorkshopApplication.java`.
5. Build the project using IntelliJ IDEA.

The project must compile without errors before it is run.

## Running the Application

To run the application in IntelliJ IDEA:

1. Open `WorkshopApplication.java`.
2. Run the `main()` method.
3. The Skills Workshop Management System heading and main menu will be displayed in the console.
4. Enter the required menu number and follow the prompts.

## Database Configuration

The SQLite database connection is configured in `WorkshopApplication.java`.

The connection uses:

`jdbc:sqlite:skills_workshop.db`

The reusable database connection method opens the SQLite database and enables foreign-key enforcement.

No personal passwords or database credentials are required because SQLite uses a local database file.

## Input and Output File Locations

The application uses files in the project working directory.

The exported registration report is created as:

`registration_report.csv`

When the Load Saved Data option is used, the user enters the name of the file that must be loaded.

The SQLite database is stored as:

`skills_workshop.db`

The SQL database creation script is stored as:

`database.sql`

## Known Limitations

- The application uses a console-based interface and does not include a graphical user interface.
- Some in-memory workshop and participant information is stored using fixed-size arrays.
- The program must be run from the project working directory so that local database and file paths are resolved correctly.
- File loading displays saved information but does not reconstruct every in-memory collection from the file.
- The application is intended for a small workshop-management scenario rather than a large multi-user system.

## Project Structure

The project uses the following structure:

- `src` - contains the Java source files.
- `lib` - contains the SQLite JDBC driver.
- `data` - contains sample participant and workshop input files.
- `database.sql` - contains the SQL used to create the database tables.
- `skills_workshop.db` - contains the SQLite database.
- `registration_report.csv` - contains the generated registration report.
- `SODF_FA1_Test_Table.xlsx` - contains the completed application test table.
- `Testing_Data.md` - contains additional testing information.
- `README.md` - contains the project documentation.

## Assumptions

The application assumes that:

- the user enters information using the formats requested by the program;
- workshop and participant IDs are unique;
- workshop dates are entered using the required date format;
- the SQLite JDBC driver remains available in the `lib` folder;
- the application has permission to create and access files in the project directory;
- the SQLite database file is available to the application;
- users select the appropriate menu option for the operation they want to perform.