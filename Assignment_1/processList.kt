// Exercise 1: Build Your Own Higher-Order Function

/**
 * processList takes a list of integers and a predicate lambda,
 * and returns a new list containing only elements that satisfy the predicate.
 */
fun processList(
    numbers: List<Int>,
    predicate: (Int) -> Boolean
): List<Int> {
    return numbers.filter { predicate(it) }
}

/**
 * Manual implementation (without using built-in filter)
 */
fun processListManual(
    numbers: List<Int>,
    predicate: (Int) -> Boolean
): List<Int> {
    val result = mutableListOf<Int>()
    for (num in numbers) {
        if (predicate(num)) {
            result.add(num)
        }
    }
    return result
}

fun main() {
    val nums = listOf(1, 2, 3, 4, 5, 6)

    // Test 1: Filter even numbers
    val even = processList(nums) { it % 2 == 0 }
    println("Even numbers: $even")           // [2, 4, 6]

    // Test 2: Filter numbers greater than 3
    val greaterThan3 = processList(nums) { it > 3 }
    println("Greater than 3: $greaterThan3") // [4, 5, 6]

    // Test 3: Filter odd numbers
    val odd = processList(nums) { it % 2 != 0 }
    println("Odd numbers: $odd")             // [1, 3, 5]

    // Test 4: Manual implementation
    val evenManual = processListManual(nums) { it % 2 == 0 }
    println("Even (manual): $evenManual")    // [2, 4, 6]
}
