import kotlin.test.Test
import kotlin.test.assertTrue

class ValidationTest {
    @Test
    fun canValidateUser() {
        val userValidation = Validation {
            User::name {
                swedishName()
            }
            User::personalNumber {
                swedishPersonalNumber()
            }
        }

        val validationResult = userValidation.validate(user1)
        println(validationResult.toString())
        assertTrue { validationResult.isEmpty() }
    }

    @Test
    fun canValidatePersonalNumberWithDifferentFormats() {
        val userValidation = Validation {
            User::personalNumber {
                swedishPersonalNumber()
            }
        }

        val numberOfErrors = listOf(user1, user2, user3, user4, user5).sumOf { userValidation.validate(it).size }
        println(numberOfErrors)
        assertTrue { numberOfErrors == 0 }
    }

    @Test
    fun canValidateIncorrectPersonalNumber() {
        val userValidation = Validation {
            User::personalNumber {
                swedishPersonalNumber()
            }
        }

        val numberOfErrors = listOf(user6, user7, user8, user9, user10).sumOf { userValidation.validate(it).size }
        println(numberOfErrors)
        assertTrue { numberOfErrors == 5 }
    }

    @Test
    fun canValidateDifferentNames() {
        val userValidation = Validation {
            User::name {
                swedishName()
            }
        }

        val errors = listOf(user1, user2, user3, user4, user5).map { userValidation.validate(it) }
        assertTrue { errors.sumOf { it.size } == 1 }
        assertTrue { errors[2]["name"]?.get(0) == "must be a swedish name" }
    }

    @Test
    fun noGivenNameGivesError() {
        val userValidation = Validation {
            User::name {
                swedishName()
            }
        }

        val error = userValidation.validate(user10)
        assertTrue { error.size == 1 }
        assertTrue { error["name"]?.get(0) == "must be a swedish name" }
    }

    @Test
    fun worksWithMultipleRules() {
        val userValidation = Validation {
            User::name {
                required()
                swedishName()
            }
        }

        val error = userValidation.validate(user10)
        assertTrue { error.size == 1 }
        assertTrue { error["name"]?.get(0) == "is required" }
        assertTrue { error["name"]?.get(1) == "must be a swedish name" }
    }

    companion object {
        val user1 = User("191230-2385", "John Johnson")
        val user2 = User("191230+2385", "Jön-Johnson")
        val user3 = User("19910901-2394", "Jøn son")
        val user4 = User("191912302385", "Jån son son")
        val user5 = User("1912302385", "Jän Johnson")
        val user6 = User("0000000000", "John Johnson")
        val user7 = User("1111111111", "John Johnson")
        val user8 = User("1111111111", "John Johnson")
        val user9 = User("911321", "John Johnson")
        val user10 = User("", "")
    }
}
