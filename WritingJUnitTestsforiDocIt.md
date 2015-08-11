## Our general philosophy ##

  * Tests are as important as productive code! Therefore we write tests with the same quality as the productive code.

  * If the productive code changes (e.g. due to a refactoring), also the test code changes.

## How to run the tests of iDocIt's projects? ##

**de.akra.idocit:**
Start the test suite de.akra.idocit.core.AllIDocItCoreTests which is stored in the "test"-source folder **as JUnit Test**.

**de.akra.idocit.java:**
Start the test suite de.akra.idocit.java.AllIDocItJavaTests which is stored in the "test"-source folder **as JUnit Plugin Test**.

**de.akra.idocit.wsdl:**
Start the test suite de.akra.idocit.java.AllIDocItJavaTests which is stored in the "test"-source folder **as JUnit Plugin Test**.

## Anything to consider for new tests? ##
Yes, please add each new test-class to the test suite of the project.