# Mobile Application for Reporting and Managing Device Failures within the PJAID System

MVP (Minimum Viable Product)
- Failure report registration
- QR code scanning
- User's list of reports
- Report details


## Team & task division

| Person              | Tasks                                                            |
|---------------------|------------------------------------------------------------------|
| Jakub Marcinkowski  | [ANDROID] Formularz zgłoszenia awarii, Lista zgłoszeń użytkownika|
| Dagmara Gibas       | [ANDROID] Szczegóły zgłoszenia                                   |
| Karol Spica         | [ANDROID] Dodanie odczytywanie QR kodu w aplikacji mobilnej      |
------------------------------------------------------------------------------------------


# Git usage instructions

Source code management in the project team was based on the GIT version control system. Below are the best practices followed during work with the repository.


## Creating a Branch

- Before starting work on a new feature or fix, create a new branch.  
- This way, you won't interfere with the main branch of the project (main).  
- Each change should be implemented in a separate branch.  
- For longer work, commit changes regularly.

## Pull Request and Code Review

After finishing work on a feature:

1. Go to **GitHub** and create a **Pull Request (PR)**.  
2. Set **your branch** as the source, and **main** as the target.
3. Other team members perform a **Code Review**, which includes:
   - logic implementation check,
   - compliance with coding standards,
   - error verification.
4. Make corrections according to comments and commit again — changes will be automatically added to the existing PR.

## Testing and merging changes

- Before approving the PR, thoroughly test your branch.
- Make sure there are no errors and that all unit and integration tests pass.
- After successfully merging into `main`, delete your branch locally and remotely.

## Naming Conventions

### Branch names:

`SCRUM_TASK_NUMBER_short_description`

**Example:**  
`SCRUM_4_database_configuration`

### Commit messages:
```
[SCRUM 4] - short description of the committed changes
```

## Summary

1. Create a new branch before each change  
2. Commit changes regularly  
3. Create Pull Requests after finishing work  
4. Conduct Code Review before merging  
5. Merge only tested code  
6. Delete outdated branches

---

# Sonar usage instructions

- It is worth installing the **SonarQube for IDE** plugin for your IDE (for IntelliJ users).  
- The plugin adds the context menu option:  
  **"Analyze with SonarCube for IDE"**.  
- After launching this option, **information about what should be improved in the code** will appear.
- It's useful to have it installed locally, because **GitHub Actions** have been added to GitHub that **automatically run Sonar analysis**.
- If the code **does not pass the analysis**, the ability to merge it will be **blocked**

---

## Full CI/CD workflow used in the project

![Schemat CICD](image/projectFlow.png)

Source code is analyzed by SonarCloud for:
- bugs
- security vulnerabilities (Security Hotspots, Bugs, Code Smells)
- test coverage

# Sonar example
![Sonar exampl](image/sonar.png)

# Pull request example
![PR exampl](image/pr.png)

# Commits example
![Commits exampl](image/commits.png)

Code documentation was maintained using JavaDoc. We tried to place comments at the class level and methods that we considered needed  
# Class comment example
![Class comment](image/classComment.png)

# Method comment example
![Method comment](image/methodComment.png)


# Unit tests

## Tools:
**JUnit**  
– Testing framework for unit and instrumentation tests  
– Used for: `@Test`, `@Before`, `@After`, `assertEquals`, etc.

**Mockito**  
– Library for mocking objects and verifying interactions  
– Used for: `mock()`, `when()`, `verify()`, `@Mock`, `mockStatic()`

**Espresso**  
– UI testing library – interactions with views (`onView()`, `withId()`, `perform()`, `check()`)

**AndroidJUnitRunner**  
– Android runner for running instrumentation tests (`@RunWith(AndroidJUnit4.class)`)


## Example classes with unit tests::
- GetDeviceByIdUseCaseTest.java – tests logic of retrieving a device
- SendReportUseCaseTest.java – tests logic of sending a report
- TokenAuthenticatorTest.java – tests JWT/token authorization logic


# Instrumented tests

## Example classes with instrumented tests:
- MainActivityTest.java – tests operation of  MainActivity
- CreateTicketActivityTest.java – tests the report submission screen

## Monkey test
![Monkey test](image/monkeyTest.png)
