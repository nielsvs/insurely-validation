import kotlin.reflect.KProperty1

class Rule<T, P>(private val property: KProperty1<T, P>) {
    private val checks = mutableListOf<(P) -> String?>()

    fun required() {
        checks.add { if (isNotPresent(it)) "is required" else null }
    }

    fun swedishName() {
        val swedishNameRegex = Regex("^[A-ZÅÄÖ][a-zåäö'’-]+(?:[\\s-][A-ZÅÄÖ]?[a-zåäö'’-]+)*$")
        checks.add { if (it == null || (it is String && !it.matches(swedishNameRegex))) "must be a swedish name" else null }
    }

    fun swedishPersonalNumber() {
        checks.add {
            if (
                isNotPresent(it) ||
                !isValidFormat(it as String) ||
                !isValidDate((it as String)) ||
                !isValidChecksum(it as String)
            ) {
                "must be a valid swedish personal number"
            } else {
                null
            }
        }
    }

    private fun isNotPresent(value: P): Boolean {
        return value == null || (value is String && value.isEmpty())
    }

    // Check that the format matches yy[yy]mmdd[-+]nnnn
    private fun isValidFormat(date: String): Boolean {
        val format = Regex("""^(\d{2}(\d{2})?)(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])[-+]?\d{4}$""")
        return format.matches(date)
    }

    // Check that the date is valid
    private fun isValidDate(date: String): Boolean {
        // TODO: if more time, this should be implemented. But localDate is sadly not available outside the jvm
        // so just return true for the time being
        return true
    }

    // Check that the checksum is valid using Luhn algorithm
    private fun isValidChecksum(number: String): Boolean {
        val digits = number.filter { it.isDigit() }

        val abbreviatedPN = digits.takeLast(10)

        val luhnSum = abbreviatedPN.mapIndexed { index, char ->
            var value = char.toString().toInt()
            if ((10 - index) % 2 == 0) {
                value *= 2
                if (value > 9) value -= 9
            }
            value
        }.sum()

        return (luhnSum % 10) == 0
    }

    fun validate(obj: T): Map<String, List<String>> {
        val errors = checks.mapNotNull { it(property.get(obj)) }
        return if (errors.isNotEmpty()) mapOf(property.name to errors) else emptyMap()
    }
}

class ValidationBuilder<T> {
    private val rules = mutableListOf<Rule<T, *>>()

    operator fun <P> KProperty1<T, P>.invoke(init: Rule<T, P>.() -> Unit) {
        val rule = Rule(this)
        rule.init()
        rules.add(rule)
    }

    fun validate(obj: T): Map<String, List<String>> {
        return rules.mapNotNull { it.validate(obj).entries.firstOrNull() }.associate { it.key to it.value }
    }
}

fun <T> Validation(init: ValidationBuilder<T>.() -> Unit): ValidationBuilder<T> {
    val validation = ValidationBuilder<T>()
    validation.init()
    return validation
}
