# Exercise 2: Transforming Between Collection Types

## Task

Given a list of strings, create a map where the keys are the strings and the values are their lengths.
Then print only the entries where the length is greater than 4.

## Sample Data

```kotlin
val words = listOf("apple", "cat", "banana", "dog", "elephant")
```

## Expected Output

```
apple has length 5
banana has length 6
elephant has length 7
```

## Solution

```kotlin
fun main() {
    val words = listOf("apple", "cat", "banana", "dog", "elephant")

    words.associateWith { it.length }
         .filter { (_, length) -> length > 4 }
         .forEach { (word, length) -> println("$word has length $length") }
}
```

## Explanation

| Step | Function | Description |
|------|----------|-------------|
| 1 | `associateWith { it.length }` | Transforms the list into a `Map<String, Int>` where each word maps to its character count |
| 2 | `.filter { (_, length) -> length > 4 }` | Keeps only entries where the length exceeds 4 |
| 3 | `.forEach { (word, length) -> ... }` | Destructures each map entry and prints it |

## How to Run

### Using the Kotlin compiler directly:
```bash
kotlinc src/main/kotlin/Main.kt -include-runtime -d exercise2.jar
java -jar exercise2.jar
```

### Using Gradle:
```bash
./gradlew run
```
