fun main() {
    val words = listOf("apple", "cat", "banana", "dog", "elephant")

    words.associateWith { it.length }
         .filter { (_, length) -> length > 4 }
         .forEach { (word, length) -> println("$word has length $length") }
}
