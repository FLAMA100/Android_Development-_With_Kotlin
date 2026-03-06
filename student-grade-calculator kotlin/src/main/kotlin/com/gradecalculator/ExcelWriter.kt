package com.gradecalculator

import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileOutputStream

class ExcelWriter {

    fun writeResults(students: List<StudentRecord>, outputPath: String) {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Grade Results")

        val headerStyle = createHeaderStyle(workbook)
        val aStyle      = createGradeStyle(workbook, IndexedColors.LIGHT_GREEN)
        val bStyle      = createGradeStyle(workbook, IndexedColors.LIGHT_BLUE)
        val cStyle      = createGradeStyle(workbook, IndexedColors.LEMON_CHIFFON)
        val dStyle      = createGradeStyle(workbook, IndexedColors.LIGHT_ORANGE)
        val fStyle      = createGradeStyle(workbook, IndexedColors.ROSE)
        val normalStyle = createNormalStyle(workbook)

        val headers = listOf("No.", "Student Name", "Mark", "Grade", "Description")
        val headerRow = sheet.createRow(0)
        headers.forEachIndexed { col, title ->
            val cell = headerRow.createCell(col)
            cell.setCellValue(title)
            cell.cellStyle = headerStyle
        }

        students.forEachIndexed { index, student ->
            val row = sheet.createRow(index + 1)
            val gradeStyle: XSSFCellStyle = when (student.grade) {
                "A"  -> aStyle
                "B"  -> bStyle
                "C"  -> cStyle
                "D"  -> dStyle
                else -> fStyle
            }
            val c0 = row.createCell(0); c0.setCellValue((index + 1).toDouble()); c0.cellStyle = normalStyle
            val c1 = row.createCell(1); c1.setCellValue(student.name);            c1.cellStyle = normalStyle
            val c2 = row.createCell(2); c2.setCellValue(student.mark);            c2.cellStyle = normalStyle
            val c3 = row.createCell(3); c3.setCellValue(student.grade);           c3.cellStyle = gradeStyle
            val c4 = row.createCell(4); c4.setCellValue(student.gradeDescription);c4.cellStyle = gradeStyle
        }

        val summaryStart = students.size + 2
        val titleCell = sheet.createRow(summaryStart).createCell(0)
        titleCell.setCellValue("Summary")
        titleCell.cellStyle = headerStyle

        val gradeCounts = students.groupBy { it.grade }.mapValues { it.value.size }
        val summaryData = listOf(
            "Total Students" to students.size.toString(),
            "A  (80 - 100)"  to (gradeCounts["A"] ?: 0).toString(),
            "B  (70 - 79)"   to (gradeCounts["B"] ?: 0).toString(),
            "C  (60 - 69)"   to (gradeCounts["C"] ?: 0).toString(),
            "D  (50 - 59)"   to (gradeCounts["D"] ?: 0).toString(),
            "F  (Below 50)"  to (gradeCounts["F"] ?: 0).toString(),
            "Class Average"  to "%.2f".format(students.map { it.mark }.average()),
            "Highest Mark"   to students.maxOf { it.mark }.toString(),
            "Lowest Mark"    to students.minOf { it.mark }.toString()
        )
        summaryData.forEachIndexed { i, (label, value) ->
            val row = sheet.createRow(summaryStart + 1 + i)
            val lc = row.createCell(0); lc.setCellValue(label); lc.cellStyle = normalStyle
            val vc = row.createCell(1); vc.setCellValue(value); vc.cellStyle = normalStyle
        }

        sheet.setColumnWidth(0, 8  * 256)
        sheet.setColumnWidth(1, 28 * 256)
        sheet.setColumnWidth(2, 12 * 256)
        sheet.setColumnWidth(3, 12 * 256)
        sheet.setColumnWidth(4, 18 * 256)

        FileOutputStream(outputPath).use { workbook.write(it) }
        workbook.close()
    }

    private fun createHeaderStyle(wb: XSSFWorkbook): XSSFCellStyle {
        val style = wb.createCellStyle()
        style.fillForegroundColor = IndexedColors.DARK_BLUE.index
        style.fillPattern = FillPatternType.SOLID_FOREGROUND
        style.alignment = HorizontalAlignment.CENTER
        style.borderBottom = BorderStyle.THIN
        style.borderTop    = BorderStyle.THIN
        style.borderLeft   = BorderStyle.THIN
        style.borderRight  = BorderStyle.THIN
        val font = wb.createFont()
        font.bold = true
        font.color = IndexedColors.WHITE.index
        font.fontName = "Arial"
        font.fontHeightInPoints = 11
        style.setFont(font)
        return style
    }

    private fun createGradeStyle(wb: XSSFWorkbook, color: IndexedColors): XSSFCellStyle {
        val style = wb.createCellStyle()
        style.fillForegroundColor = color.index
        style.fillPattern = FillPatternType.SOLID_FOREGROUND
        style.alignment = HorizontalAlignment.CENTER
        style.borderBottom = BorderStyle.THIN
        style.borderTop    = BorderStyle.THIN
        style.borderLeft   = BorderStyle.THIN
        style.borderRight  = BorderStyle.THIN
        val font = wb.createFont()
        font.bold = true
        font.fontName = "Arial"
        font.fontHeightInPoints = 11
        style.setFont(font)
        return style
    }

    private fun createNormalStyle(wb: XSSFWorkbook): XSSFCellStyle {
        val style = wb.createCellStyle()
        style.borderBottom = BorderStyle.THIN
        style.borderTop    = BorderStyle.THIN
        style.borderLeft   = BorderStyle.THIN
        style.borderRight  = BorderStyle.THIN
        val font = wb.createFont()
        font.fontName = "Arial"
        font.fontHeightInPoints = 11
        style.setFont(font)
        return style
    }
}
