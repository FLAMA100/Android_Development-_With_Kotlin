# Exercise 3: Complex Data Processing

## Task
Find the average age of people whose names start with **'A'** or **'B'**.
Print the result rounded to one decimal place.

## Steps
1. Filter people whose name starts with 'A' or 'B'
2. Extract ages
3. Calculate average
4. Format and print

## Expected Output
```
26.3
```

## How to Run

### Using kotlinc (command line)
```bash
kotlinc src/Main.kt -include-runtime -d exercise3.jar
java -jar exercise3.jar
```

### Using IntelliJ IDEA
1. Open the project in IntelliJ IDEA
2. Run `Main.kt` directly

## Explanation
- Filtered: Alice (25), Bob (30), Anna (22), Ben (28)
- Sum: 25 + 30 + 22 + 28 = 105
- Average: 105 / 4 = 26.25 → rounded to **26.3**
