# Exercise 1: Build Your Own Higher-Order Function

## Task
Write a function `processList` that takes a list of integers and a lambda `(Int) -> Boolean`, and returns a new list containing only the elements that satisfy the predicate.

## Function Signature

```kotlin
fun processList(
    numbers: List<Int>,
    predicate: (Int) -> Boolean
): List<Int>
```

## Solution

### Idiomatic Kotlin (using `filter`)
```kotlin
fun processList(
    numbers: List<Int>,
    predicate: (Int) -> Boolean
): List<Int> {
    return numbers.filter { predicate(it) }
}
```

### Manual Implementation (without built-in `filter`)
```kotlin
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
```

## Test Cases

```kotlin
val nums = listOf(1, 2, 3, 4, 5, 6)

val even = processList(nums) { it % 2 == 0 }
println(even)  // [2, 4, 6]
```

## How to Run

```bash
kotlinc processList.kt -include-runtime -d processList.jar
java -jar processList.jar
```

## Expected Output

```
Even numbers: [2, 4, 6]
Greater than 3: [4, 5, 6]
Odd numbers: [1, 3, 5]
Even (manual): [2, 4, 6]
```
