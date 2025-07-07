To generate java tests for the given code, please follow these steps:
1. **Understand the Code**: Read through the provided Java code to understand its functionality,
   including the classes, methods, and their interactions.
2. **Identify Test Cases**: Determine the key functionalities and edge cases that need to be
   tested. This includes:
   - Normal cases (valid inputs)
   - Edge cases (boundary conditions, null inputs, etc.)
   - Error cases (invalid inputs, exceptions)
3. **Use JUnit**: Write tests using the JUnit framework, which is commonly used for unit testing in Java.
4. **Structure Tests**: Organize tests into classes that correspond to the classes being tested.
   - Each test class should have a clear naming convention, typically `ClassNameTest`.
   - Each test method should be descriptive, indicating what is being tested.
5. **Assertions**: Use assertions to verify that the actual output matches the expected output.
6. **Mocking**: If the code interacts with external systems or dependencies, consider using
   mocking frameworks like Mockito to isolate the unit being tested. Load the classes that are mocked using the import statement.