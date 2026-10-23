
# Software Development Reflection & Improvement Plan

## Skills Workshop Management System

**Project:** Skills Workshop Management System  
**Module:** Software Development Fundamentals (SODF102)  
**Language:** Java  
**Database:** SQLite with JDBC  
**Development Environment:** IntelliJ IDEA  
**Assessment Result:** 88.13% (141/160)  
**Project Status:** Original assessed version preserved; improvements proposed

---

## 1. Introduction

I developed the Skills Workshop Management System as part of my Software Development Fundamentals (SODF102) formative assessment.

The objective was to design, implement, test and document a Java console application capable of managing workshops, participants and registrations.

The application demonstrates several fundamental programming concepts, including:

- Variables, constants and arithmetic operations
- String processing and enumerations
- Arrays and control structures
- Java Collections Framework
- Generic methods and wrapper classes
- Input validation and exception handling
- File input and output
- Relational database design
- JDBC database operations
- Application testing and troubleshooting

The completed assessment received **141 out of 160 marks (88.13%)**.

After reviewing the assessment feedback, I identified opportunities to improve the application's reliability, data consistency, validation and overall software design.

This document records my understanding of those limitations and explains how I would approach improving the application in a future development iteration.

The original submitted implementation has been preserved to demonstrate my work at the time of assessment.

---

## 2. Assessment Performance

| Question | Topic | Marks |
|---|---|---|
| Q1 | Project Setup and Execution | 11/12 |
| Q2 | Variables, Strings, Arrays and Enumerations | 15/16 |
| Q3 | Application Control Logic | 15/18 |
| Q4 | Wrapper Classes, Generics and Collections | 29/32 |
| Q5 | Input Validation and Exception Handling | 15/18 |
| Q6 | File Input and Output | 20/24 |
| Q7 | JDBC Database Implementation | 22/22 |
| Q8 | Integration, Testing and Delivery | 14/18 |
| **Total** | | **141/160** |

### 2.1 Strengths Identified

The assessment feedback highlighted several areas where the implementation successfully demonstrated the required programming techniques.

These include:

- Correct implementation of the main Java application structure
- String formatting, validation and searching
- Array processing and workshop category enumerations
- Use of ArrayList, HashMap and HashSet
- Duplicate participant and registration prevention
- Generic collection processing
- Exception-handling structures
- Buffered file input and output
- Try-with-resources for file and database resources
- SQLite relational database design
- JDBC operations using PreparedStatement
- Testing documentation and project packaging

### 2.2 Database Implementation

I achieved **22/22 for Question 7**, which assessed the relational database and JDBC implementation.

This section demonstrated my ability to:

1. Design relational tables with appropriate primary and foreign keys.
2. Apply required fields and unique constraints.
3. Establish reusable JDBC database connections.
4. Insert and retrieve records.
5. Search and update existing records.
6. Cancel database registrations.
7. Use PreparedStatement for parameterised SQL operations.
8. Handle SQLException and close database resources safely.

This was one of the strongest parts of my assessment.

However, successful implementation of individual database operations does not automatically guarantee that the entire application maintains consistent information.

This distinction became an important lesson during my review.

---

## 3. Identified Areas for Improvement

The assessment feedback identified several areas where the application could be improved.

I have grouped these findings according to their underlying technical causes rather than treating each assessment question as an isolated issue.

### 3.1 Data Consistency Across Collections

**Related feedback:** Q2(d), Q3(d), Q4(b), Q4(g), Q6(d) and Q8(b)

#### Identified limitation

The application uses multiple structures to maintain workshop, participant and registration information.

These include:

- Arrays
- ArrayList collections
- HashMap collections
- HashSet collections
- Registration counters
- SQLite database tables

Although these structures demonstrate the required Java concepts, some values may become inconsistent when information is added, updated or deleted.

For example, a workshop registration count stored in one structure may differ from the actual registrations stored elsewhere.

#### Root-cause analysis

I introduced different data structures while progressing through the assessment requirements.

Some information was stored as a formatted string or snapshot of a record.

When related information changed, not every corresponding representation was necessarily updated.

This created the possibility of outdated registration counts and inconsistent reporting.

#### Proposed improvement

I would introduce structured model classes to represent the main application entities.

Examples include:

- `Workshop`
- `Participant`
- `Registration`

Each class would contain fields relevant to that entity.

I would also define a clear source of truth for operational records and use centralised methods to manage changes.

If SQLite is selected as the authoritative data store, the application should retrieve or update records through consistent database operations.

Collections could then be used as temporary working data or carefully managed caches rather than independent sources of conflicting information.

#### Expected outcome

The application should maintain consistent information across registration, searching, reporting and database operations.

---

### 3.2 Registration and Booking Validation

**Related feedback:** Q3(e) and Q5(c)

#### Identified limitation

The application validates workshop dates when workshops are created.

However, the registration process does not consistently recheck whether the workshop date has passed.

A workshop may have a valid future date when first created but become unavailable for new bookings later.

The feedback also identified inconsistent validation between local registration processing and direct SQL operations.

#### Root-cause analysis

Some validation rules were implemented at the point where information was first captured rather than every point where the information could be used.

This means that later operations could rely on previously valid information without checking whether it remained valid.

#### Proposed improvement

I would create a reusable registration validation method that checks:

1. Whether the participant exists and is eligible to register.
2. Whether the workshop exists.
3. Whether the workshop is active.
4. Whether the workshop date is still in the future.
5. Whether available capacity remains.
6. Whether the participant is already registered.

All registration routes should use the same business rules.

I would also ensure that capacity and duplicate-registration rules are enforced safely during database transactions.

#### Expected outcome

The system should prevent invalid registrations regardless of which application function initiates the registration.

---

### 3.3 Input Validation and Error Recovery

**Related feedback:** Q5(b) and Q5(e)

#### Identified limitation

The application handles several invalid input conditions successfully.

However, some numeric inputs do not have adequate error recovery.

The assessment feedback identified a reproducible runtime error when non-numeric text was entered into the registration summary capacity prompt.

For example:

```text
Enter workshop capacity: abc
```

This could cause an uncaught `InputMismatchException` and terminate the application.

#### Root-cause analysis

Not all numeric input operations were processed using the same validation approach.

Some inputs were protected by exception handling, while other sections still depended on direct Scanner numeric-reading methods.

#### Proposed improvement

I would create reusable input methods such as:

```java
readValidInteger()
readValidDouble()
readValidDate()
```

These methods would:

1. Read input as text.
2. Attempt the appropriate conversion.
3. Catch invalid input errors.
4. Display a helpful message.
5. Request corrected input.
6. Return only an accepted value.

Additional range validation would be applied where required.

This approach would reduce repeated validation code and encourage consistent behaviour throughout the application.

#### Expected outcome

Recoverable input errors should not unexpectedly terminate the program.

---

### 3.4 File Loading and Data Restoration

**Related feedback:** Q6(e), Q6(f), Q8(a) and Q8(b)

#### Identified limitation

The application can read information from a text file and display its contents.

However, reading file contents is not equivalent to restoring operational application records.

The feedback identified that loaded information was not reconstructed into the application's workshop, participant and registration collections.

#### Root-cause analysis

The file-reading implementation focused on demonstrating file access and displaying the stored information.

It did not fully implement the process of converting that information back into structured application records.

#### Proposed improvement

I would expand the loading functionality to perform the following operations:

1. Open the selected file using buffered input.
2. Read each stored record.
3. Parse the record into its individual fields.
4. Validate the field values and data types.
5. Reconstruct the appropriate Java objects.
6. Check for duplicate records.
7. Restore the required collections and indexes.
8. Reconcile or rebuild registration counts.
9. Display a meaningful loading summary.

The application should distinguish between displaying file contents and successfully restoring operational records.

I would also ensure that malformed files do not leave the application in a partially restored state.

#### Expected outcome

Previously saved information should be available for normal application operations after loading.

---

### 3.5 Registration Summaries and Financial Calculations

**Related feedback:** Q3(d), Q4(b), Q6(d) and Q8(a)

#### Identified limitation

Some registration summaries use values entered separately rather than calculating all results directly from maintained application records.

This creates a possibility that reported registrations, available spaces and expected income do not reflect the actual stored information.

#### Root-cause analysis

The summary calculations were developed while the application still used separate arrays, counters and record representations.

Some calculations therefore depend on values that are not automatically reconciled with actual registration records.

#### Proposed improvement

I would centralise summary calculations so they operate on authoritative workshop and registration records.

The calculations should determine:

- Total workshops
- Total participants
- Total registrations
- Registrations per workshop
- Available spaces per workshop
- Total available spaces
- Expected workshop income

Expected income should be calculated using the relevant registration amounts or fee values according to the application's documented business rules.

I would also distinguish expected income from actual payments received.

#### Expected outcome

Reports and summaries should accurately reflect the underlying application records without requiring duplicate manual entry.

---

### 3.6 Record Deletion, Cancellation and Clearing

**Related feedback:** Q3(f) and Q8(b)

#### Identified limitation

The application includes confirmation prompts for several destructive operations.

However, the assessment feedback identified incomplete consistency between local clearing operations and database operations.

The local clearing process clears several generic collections and resets counters, but does not fully clear every related data structure.

The separate SQL cancellation route also requires more consistent confirmation handling.

#### Root-cause analysis

Deletion, cancellation and clearing were implemented across different sections of the application.

These operations were not all managed through a single consistent process.

#### Proposed improvement

I would create centralised methods for destructive operations.

Before changing information, the application should:

1. Identify the affected record.
2. Display the proposed action.
3. Request explicit confirmation.
4. Perform the complete operation only after confirmation.
5. Update or invalidate related collections and indexes.
6. Recalculate affected totals.
7. Display a success or failure message.

Database changes should use appropriate transactions where multiple related changes must succeed together.

I would also distinguish between clearing temporary in-memory data and permanently deleting database records.

#### Expected outcome

Destructive operations should behave predictably and preserve data integrity.

---

### 3.7 Workshop Sorting and Filtering

**Related feedback:** Q2(d) and Q4(g)

#### Identified limitation

The application demonstrates category filtering and alphabetical sorting using standard workshop titles.

However, these operations do not consistently process all workshops captured and maintained by the user.

#### Root-cause analysis

The fixed workshop-title array was useful for demonstrating early programming concepts.

As the application expanded, some features continued to use those initial arrays instead of operating on the maintained workshop records.

#### Proposed improvement

I would implement sorting and filtering over the actual workshop objects.

Possible features include:

- Sorting workshops by title
- Sorting workshops by date
- Filtering workshops by category
- Filtering active workshops
- Filtering workshops with available spaces

For example, Java's `Comparator` could be used to organise workshop objects according to selected fields.

#### Expected outcome

Sorting and filtering should reflect the current information maintained by the application.

---

### 3.8 Date Handling

**Related feedback:** Q1(d)

#### Identified limitation

The application startup banner displays a fixed date rather than retrieving the current system date dynamically.

#### Proposed improvement

I would use Java's `LocalDate` and `DateTimeFormatter` classes to generate the startup date when the application runs.

This would eliminate the need to update a hard-coded date manually.

#### Expected outcome

The application should always display the current date when launched.

---

### 3.9 Database Constraint Testing

**Related feedback:** Q8(d)

#### Identified limitation

The project implements SQLite foreign-key enforcement using:

```sql
PRAGMA foreign_keys = ON;
```

However, the assessment feedback identified that the supporting test evidence did not conclusively demonstrate rejection of an orphan registration.

A successful database connection alone does not prove that foreign-key constraints are being enforced.

#### Proposed improvement

I would create a specific negative test that attempts to insert a registration referencing a participant or workshop that does not exist.

The test should verify that:

1. Foreign-key enforcement is enabled.
2. The invalid registration is rejected.
3. A suitable database error is handled.
4. No invalid registration is stored.
5. Normal valid database operations still succeed.

#### Expected outcome

The application should demonstrate both successful database operations and enforcement of relational integrity rules.

---

## 4. Proposed Development Roadmap

I would approach the improvements incrementally rather than rewriting the entire application at once.

| Phase | Improvement | Priority |
|---|---|---|
| 1 | Preserve original version and establish regression tests | High |
| 2 | Standardise numeric input validation | High |
| 3 | Recheck registration rules during every booking | High |
| 4 | Introduce structured records and a consistent source of truth | High |
| 5 | Reconcile collections, counters and database operations | High |
| 6 | Rebuild operational records during file loading | High |
| 7 | Improve cancellation, deletion and clearing operations | High |
| 8 | Recalculate summaries and reports from maintained records | High |
| 9 | Improve workshop sorting and filtering | Medium |
| 10 | Replace the static startup date | Low |
| 11 | Expand automated and manual testing | High |
| 12 | Update documentation and prepare an improved release | Medium |

Each phase should be tested before progressing to the next.

The original assessed version would remain available separately for reference.

---

## 5. Proposed Testing Strategy

One of the most important lessons from this assessment was the difference between testing an individual feature and testing an entire application workflow.

In future projects, I would place greater emphasis on integration testing and negative testing.

### 5.1 Input Validation Tests

| Test | Expected Result |
|---|---|
| Enter letters where capacity is required | Error message and retry |
| Enter negative workshop fee | Value rejected |
| Enter zero workshop capacity | Value rejected |
| Enter invalid date format | Error message and retry |
| Enter invalid menu selection | Menu remains operational |

### 5.2 Registration Tests

| Test | Expected Result |
|---|---|
| Register for an available workshop | Registration succeeds |
| Register twice for the same workshop | Duplicate prevented |
| Register for a full workshop | Registration rejected |
| Register for an inactive workshop | Registration rejected |
| Register for a past workshop | Registration rejected |
| Register with an unknown participant ID | Registration rejected |

### 5.3 Data Consistency Tests

| Test | Expected Result |
|---|---|
| Create a workshop | Record available in required application functions |
| Register a participant | Registration count updates correctly |
| Cancel a registration | Related counts and records remain consistent |
| Clear temporary data | All relevant temporary structures are cleared |
| Export a report | Export reflects maintained records |
| Reload saved data | Restored records are usable in normal operations |

### 5.4 Database Tests

| Test | Expected Result |
|---|---|
| Insert a valid participant | Record stored successfully |
| Insert duplicate email | Duplicate rejected |
| Insert a valid registration | Registration stored |
| Insert orphan registration | Foreign-key constraint rejects insertion |
| Search for missing record | Helpful message displayed |
| Simulate database failure | Application handles failure safely |

### 5.5 Regression Testing

After each improvement, I would repeat relevant previously successful tests to ensure that new functionality has not broken existing behaviour.

I would also introduce automated unit tests where practical, particularly for validation methods, calculations and registration rules.

---

## 6. Lessons Learned

### 6.1 Functionality and Integration Are Different

I learned that an application can contain several individually working features while still experiencing problems when those features interact.

Correctly implementing a collection or database operation is important, but the overall system must also maintain consistent information.

### 6.2 Data Should Have a Clear Source of Truth

Maintaining the same information independently across multiple structures increases the risk of inconsistencies.

Future applications should use a clearly defined approach for storing, updating and retrieving authoritative records.

### 6.3 Validation Must Be Consistent

Input validation should not be limited to the first point where data enters the application.

Business rules must also be checked whenever operations depend on them.

### 6.4 Testing Must Include Failure Scenarios

Successful tests demonstrate that expected functionality works.

Negative tests demonstrate how an application responds when users make mistakes or operations fail.

Both are necessary to build reliable software.

### 6.5 File Reading Is Not the Same as Data Restoration

Reading and displaying a file demonstrates file input.

Restoring previously saved records requires additional parsing, validation and reconstruction.

### 6.6 Documentation Supports Maintainability

Maintaining a README, test evidence and technical notes makes it easier to understand the purpose, limitations and development history of a project.

I intend to continue documenting significant improvements and their test results in future work.

---

## 7. Future Development Opportunities

Beyond the corrections identified in the assessment feedback, I would consider several optional enhancements once the application is stable.

These include:

- Improved object-oriented modelling
- More reusable application methods
- Automated unit and integration tests
- Database transactions for related operations
- More flexible workshop search and filtering
- Improved reporting functionality
- Clearer separation between input, business logic and persistence
- A graphical interface or web-based interface as a future extension

These features are not presented as requirements of the original assessment.

They represent possible directions for further personal development.

---

## 8. Personal Reflection

Developing the Skills Workshop Management System gave me practical experience in combining multiple Java programming concepts within a single application.

I became more comfortable working with collections, user input, exception handling, file operations and relational databases.

The database implementation was a particularly valuable part of the project because it required me to understand how Java communicates with SQLite through JDBC.

Although I achieved 88.13%, the feedback helped me recognise that writing functional code is only one part of software development.

Reliable software also depends on data integrity, consistent validation, maintainable design and thorough testing.

My next goal is to strengthen these areas by applying the lessons learned to future projects and, where appropriate, developing an improved version of this application.

I see this assessment as both a completed academic project and a foundation for continued practical development.

---

## 9. Project Version and Documentation

This GitHub repository contains the original assessed implementation of the Skills Workshop Management System.

The improvement strategies documented here are **proposed future work** and should not be interpreted as completed changes.

Any future implementation will be documented separately, with appropriate testing evidence and version history.

### Supporting Documentation

- `README.md` — Application overview and setup instructions
- `src/` — Original Java source files
- `database.sql` — SQLite database schema
- `data/` — Sample application input files
- `SODF_FA1_Test_Table.xlsx` — Assessment testing evidence
- `Testing_Data.md` — Additional testing information
- `docs/SODF102_FA1_Feedback_Public.pdf` — Assessment feedback, public copy

---

## 10. Conclusion

The Skills Workshop Management System represents an important stage in my development as a Java programmer.

The assessment demonstrated my ability to implement core Java features, relational database operations and supporting project documentation.

The feedback also highlighted the importance of testing how application components work together.

By reviewing these findings and preparing a structured improvement plan, I aim to develop stronger skills in software architecture, validation, data management and testing.

My objective is not simply to make the application work, but to understand how to make it more reliable, maintainable and consistent.

**Final Assessment Result: 88.13% — 141/160**

**Improvement Status: Planned — Not Yet Implemented**
