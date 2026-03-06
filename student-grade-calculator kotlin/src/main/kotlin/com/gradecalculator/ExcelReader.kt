package com.gradecalculator

import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File

class ExcelReader {

    fun readStudents(filePath: String): List<StudentRecord> {
        val file = File(filePath)
        require(file.exists()) { "File not found: $filePath" }
        require(filePath.endsWith(".xlsx") || filePath.endsWith(".xls")) {
            "File must be an Excel file (.xlsx or .xls)"
        }

        val students = mutableListOf<StudentRecord>()

        WorkbookFactory.create(file).use { workbook ->
            val sheet = workbook.getSheetAt(0)
            val headerRow = sheet.getRow(0)

            // Detect which columns hold name and mark
            val nameColIndex = findColumnIndex(headerRow, listOf("name", "student", "student name"))
            val markColIndex = findColumnIndex(headerRow, listOf("mark", "marks", "score", "grade", "result"))

            val startRow = if (nameColIndex != -1 && markColIndex != -1) 1 else 0
            val nameCol = if (nameColIndex != -1) nameColIndex else 0
            val markCol = if (markColIndex != -1) markColIndex else 1

            for (rowIndex in startRow..sheet.lastRowNum) {
                val row = sheet.getRow(rowIndex) ?: continue

                val nameCell = row.getCell(nameCol) ?: continue
                val markCell = row.getCell(markCol) ?: continue

                val name = nameCell.toString().trim()
                val mark = when (markCell.cellType) {
                    CellType.NUMERIC -> markCell.numericCellValue
                    CellType.STRING  -> markCell.stringCellValue.trim().toDoubleOrNull() ?: continue
                    else             -> continue
                }

                if (name.isNotBlank()) {
                    students.add(StudentRecord(name = name, mark = mark))
                }
            }
        }

        require(students.isNotEmpty()) {
            "No student data found in the file. Ensure columns are: Name, Mark"
        }

        return students
    }

    private fun findColumnIndex(headerRow: org.apache.poi.ss.usermodel.Row?, keywords: List<String>): Int {
        if (headerRow == null) return -1
        for (cell in headerRow) {
            val cellValue = cell.toString().trim().lowercase()
            if (keywords.any { cellValue.contains(it) }) {
                return cell.columnIndex
            }
        }
        return -1
    }
}
