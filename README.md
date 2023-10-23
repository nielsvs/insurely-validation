# Validation

This is my submission to solve the Insurely backend code case. The base of the project was created in IntelliJ IDEA, by choosing 'Kotlin Multiplatform', but multiple files has been removed for simplicity.
The solution + tests can be found under `src/commonMain/kotlin/Validation.kt` and `src/commonTest/kotlin/*`.

## How to use
Kotlin's DSL has been utilized to make it as user-friendly and reusable to specify which validations should be carried out with the possibility to chain them. Please see below for an example of usage.
```kotlin
val user1 = User("John", "1234567890")
val userValidation = Validation {
    User::name {
        required()
        swedishName()
    }
    User::personalNumber {
        swedishPersonalNumber()
    }
}
val result = userValidation.validate(user1)
```

## How to testing
Run the following command from the source of the repository to execute all tests

```
./gradlew jvmTest
```

## Technology
- Kotlin 1.9.0

## Next steps
- More thorough testing of different personal numbers + names
- Support coordination number for personal number
- Extend with more generic rule options