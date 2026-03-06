package com.gradecalculator

data class StudentRecord(
    val name: String,
    val mark: Double
) {
    val grade: String = when {
        mark >= 80 -> "A"
        mark >= 70 -> "B"
        mark >= 60 -> "C"
        mark >= 50 -> "D"
        else       -> "F"
    }

    val gradeDescription: String = when (grade) {
        "A" -> "Distinction"
        "B" -> "Merit"
        "C" -> "Credit"
        "D" -> "Pass"
        else -> "Fail"
    }
}
