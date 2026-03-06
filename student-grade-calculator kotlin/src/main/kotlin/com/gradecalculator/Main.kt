package com.gradecalculator

import java.awt.*
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.*
import java.awt.event.*
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.swing.*
import javax.swing.border.*
import javax.swing.table.DefaultTableModel
import javax.swing.table.DefaultTableCellRenderer

object Theme {
    val BG         = Color(15,  17,  26)
    val SURFACE    = Color(24,  27,  40)
    val CARD       = Color(32,  36,  54)
    val ACCENT     = Color(99, 179, 237)
    val ACCENT2    = Color(154, 117, 234)
    val SUCCESS    = Color(72, 199, 142)
    val WARNING    = Color(255, 190,  80)
    val DANGER     = Color(255,  99,  99)
    val TEXT       = Color(226, 232, 240)
    val TEXT_MUTED = Color(113, 128, 150)
    val BORDER     = Color(45,  52,  75)

    fun gradeColor(g: String) = when(g) {
        "A" -> SUCCESS; "B" -> ACCENT; "C" -> WARNING; "D" -> Color(255,150,50); else -> DANGER
    }
}

open class RoundPanel(private val radius: Int = 16, bg: Color = Theme.CARD) : JPanel() {
    init { isOpaque = false; background = bg }
    override fun paintComponent(g: Graphics) {
        val g2 = g as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2.color = background
        g2.fillRoundRect(0, 0, width, height, radius, radius)
        super.paintComponent(g)
    }
}

class GradientButton(text: String) : JButton(text) {
    private var hovered = false
    init {
        isContentAreaFilled = false; isFocusPainted = false; isBorderPainted = false
        foreground = Color.WHITE
        font = Font("Segoe UI", Font.BOLD, 13)
        cursor = Cursor(Cursor.HAND_CURSOR)
        preferredSize = Dimension(200, 44)
        addMouseListener(object : MouseAdapter() {
            override fun mouseEntered(e: MouseEvent) { hovered = true; repaint() }
            override fun mouseExited(e: MouseEvent)  { hovered = false; repaint() }
        })
    }
    override fun paintComponent(g: Graphics) {
        val g2 = g as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        val c1 = if (hovered) Color(120, 195, 255) else Theme.ACCENT
        val c2 = if (hovered) Color(180, 140, 255) else Theme.ACCENT2
        g2.paint = GradientPaint(0f, 0f, c1, width.toFloat(), 0f, c2)
        g2.fillRoundRect(0, 0, width, height, 12, 12)
        super.paintComponent(g)
    }
}

class StatCard(label: String, initial: String, accent: Color) : RoundPanel(14, Theme.SURFACE) {
    private val valueLabel = JLabel(initial, SwingConstants.CENTER)
    init {
        layout = BorderLayout()
        border = EmptyBorder(14, 10, 14, 10)
        val top = JLabel(label, SwingConstants.CENTER)
        top.font = Font("Segoe UI", Font.PLAIN, 11)
        top.foreground = Theme.TEXT_MUTED
        valueLabel.font = Font("Segoe UI", Font.BOLD, 28)
        valueLabel.foreground = accent
        add(top, BorderLayout.NORTH)
        add(valueLabel, BorderLayout.CENTER)
    }
    fun setValue(v: String) { valueLabel.text = v }
}

class GradeCalculatorUI : JFrame("Student Grade Calculator") {

    private var inputFile: File? = null
    private val students = mutableListOf<StudentRecord>()

    private val dropLabel     = JLabel("Drop Excel file here  or  click Browse", SwingConstants.CENTER)
    private val fileNameLabel = JLabel("No file selected", SwingConstants.CENTER)
    private val browseBtn     = GradientButton("Browse File")
    private val processBtn    = GradientButton("Calculate Grades")
    private val exportBtn     = GradientButton("Export to Excel")
    private val statusLabel   = JLabel(" ", SwingConstants.CENTER)
    private val tableModel    = DefaultTableModel(arrayOf("No.", "Student Name", "Mark", "Grade", "Description"), 0)
    private val table         = JTable(tableModel)

    private val statTotal = StatCard("Total",   "0", Theme.ACCENT)
    private val statAvg   = StatCard("Average", "-", Theme.ACCENT2)
    private val statHigh  = StatCard("Highest", "-", Theme.SUCCESS)
    private val statLow   = StatCard("Lowest",  "-", Theme.DANGER)

    init {
        defaultCloseOperation = EXIT_ON_CLOSE
        preferredSize = Dimension(960, 720)
        contentPane.background = Theme.BG
        layout = BorderLayout()
        add(buildHeader(), BorderLayout.NORTH)
        add(buildCenter(), BorderLayout.CENTER)
        pack(); setLocationRelativeTo(null); isVisible = true
    }

    private fun buildHeader(): JPanel {
        val p = JPanel(BorderLayout())
        p.background = Theme.SURFACE
        p.border = MatteBorder(0, 0, 1, 0, Theme.BORDER)
        val title = JLabel("  Student Grade Calculator")
        title.font = Font("Segoe UI", Font.BOLD, 20)
        title.foreground = Theme.TEXT
        title.border = EmptyBorder(16, 20, 16, 0)
        val sub = JLabel("Upload an Excel file · Calculate · Export  ")
        sub.font = Font("Segoe UI", Font.PLAIN, 12)
        sub.foreground = Theme.TEXT_MUTED
        p.add(title, BorderLayout.WEST)
        p.add(sub,   BorderLayout.EAST)
        return p
    }

    private fun buildCenter(): JPanel {
        val p = JPanel(BorderLayout(16, 16))
        p.background = Theme.BG
        p.border = EmptyBorder(20, 20, 20, 20)
        p.add(buildLeftPanel(),  BorderLayout.WEST)
        p.add(buildRightPanel(), BorderLayout.CENTER)
        return p
    }

    private fun buildLeftPanel(): JPanel {
        val p = RoundPanel(16, Theme.SURFACE)
        p.layout = BoxLayout(p, BoxLayout.Y_AXIS)
        p.border = EmptyBorder(20, 20, 20, 20)
        p.preferredSize = Dimension(260, 0)

        val dropZone = RoundPanel(12, Theme.CARD)
        dropZone.layout = BorderLayout()
        dropZone.preferredSize = Dimension(220, 120)
        dropZone.maximumSize   = Dimension(220, 120)
        dropZone.alignmentX    = Component.CENTER_ALIGNMENT
        dropLabel.font      = Font("Segoe UI", Font.PLAIN, 12)
        dropLabel.foreground = Theme.TEXT_MUTED
        dropZone.add(dropLabel, BorderLayout.CENTER)

        DropTarget(dropZone, object : DropTargetAdapter() {
            override fun dragEnter(e: DropTargetDragEvent) { dropZone.background = Color(40,60,90); dropZone.repaint() }
            override fun dragExit(e: DropTargetEvent)      { dropZone.background = Theme.CARD;      dropZone.repaint() }
            override fun drop(e: DropTargetDropEvent) {
                e.acceptDrop(DnDConstants.ACTION_COPY)
                val files = e.transferable.getTransferData(DataFlavor.javaFileListFlavor) as List<*>
                val f = files.firstOrNull() as? File
                if (f != null && (f.name.endsWith(".xlsx") || f.name.endsWith(".xls"))) setFile(f)
                else setStatus("Please drop an Excel (.xlsx) file", Theme.DANGER)
                dropZone.background = Theme.CARD; dropZone.repaint()
            }
        })

        p.add(dropZone)
        p.add(Box.createVerticalStrut(12))

        fileNameLabel.alignmentX = Component.CENTER_ALIGNMENT
        fileNameLabel.font = Font("Segoe UI", Font.PLAIN, 11)
        fileNameLabel.foreground = Theme.TEXT_MUTED
        p.add(fileNameLabel)
        p.add(Box.createVerticalStrut(16))

        for (btn in listOf(browseBtn, processBtn, exportBtn)) {
            btn.alignmentX = Component.CENTER_ALIGNMENT
            btn.maximumSize = Dimension(220, 44)
            p.add(btn)
            p.add(Box.createVerticalStrut(10))
        }
        exportBtn.isEnabled = false

        p.add(Box.createVerticalStrut(16))
        statusLabel.alignmentX = Component.CENTER_ALIGNMENT
        statusLabel.font = Font("Segoe UI", Font.BOLD, 12)
        statusLabel.foreground = Theme.SUCCESS
        p.add(statusLabel)
        p.add(Box.createVerticalGlue())

        // Legend
        val legend = RoundPanel(10, Theme.CARD)
        legend.layout = GridLayout(5, 1, 4, 4)
        legend.border = EmptyBorder(12, 12, 12, 12)
        legend.maximumSize = Dimension(220, 160)
        legend.alignmentX = Component.CENTER_ALIGNMENT
        for ((grade, ltext) in listOf("A" to "A  >= 80  Distinction", "B" to "B  70-79  Merit",
                "C" to "C  60-69  Credit", "D" to "D  50-59  Pass", "F" to "F  < 50   Fail")) {
            val row = JPanel(FlowLayout(FlowLayout.LEFT, 6, 0)); row.isOpaque = false
            val dot = JLabel("*"); dot.foreground = Theme.gradeColor(grade); dot.font = Font("Segoe UI", Font.BOLD, 14)
            val lbl = JLabel(ltext); lbl.foreground = Theme.TEXT_MUTED; lbl.font = Font("Segoe UI", Font.PLAIN, 11)
            row.add(dot); row.add(lbl); legend.add(row)
        }
        p.add(legend)

        browseBtn.addActionListener  { browseFile() }
        processBtn.addActionListener { processFile() }
        exportBtn.addActionListener  { exportFile() }
        return p
    }

    private fun buildRightPanel(): JPanel {
        val p = JPanel(BorderLayout(0, 16))
        p.background = Theme.BG
        val statsRow = JPanel(GridLayout(1, 4, 12, 0))
        statsRow.background = Theme.BG
        for (card in listOf(statTotal, statAvg, statHigh, statLow)) statsRow.add(card)
        p.add(statsRow, BorderLayout.NORTH)
        val tableCard = RoundPanel(16, Theme.SURFACE)
        tableCard.layout = BorderLayout()
        tableCard.border = EmptyBorder(16, 16, 16, 16)
        table.background = Theme.SURFACE; table.foreground = Theme.TEXT
        table.font = Font("Segoe UI", Font.PLAIN, 13); table.rowHeight = 36
        table.showHorizontalLines = true; table.showVerticalLines = false
        table.gridColor = Theme.BORDER; table.isEnabled = false
        table.setSelectionBackground(Color(50, 60, 90)); table.setSelectionForeground(Theme.TEXT)
        val header = table.tableHeader
        header.background = Theme.CARD; header.foreground = Theme.TEXT_MUTED
        header.font = Font("Segoe UI", Font.BOLD, 12)
        val cr = DefaultTableCellRenderer(); cr.horizontalAlignment = SwingConstants.CENTER
        for (i in listOf(0, 2, 3, 4)) table.columnModel.getColumn(i).cellRenderer = cr
        table.columnModel.getColumn(0).preferredWidth = 50
        table.columnModel.getColumn(1).preferredWidth = 200
        table.columnModel.getColumn(2).preferredWidth = 80
        table.columnModel.getColumn(3).preferredWidth = 70
        table.columnModel.getColumn(4).preferredWidth = 120
        val scroll = JScrollPane(table)
        scroll.background = Theme.SURFACE; scroll.viewport.background = Theme.SURFACE
        scroll.border = BorderFactory.createEmptyBorder()
        tableCard.add(scroll, BorderLayout.CENTER)
        p.add(tableCard, BorderLayout.CENTER)
        return p
    }

    private fun setFile(f: File) {
        inputFile = f
        fileNameLabel.text = f.name; fileNameLabel.foreground = Theme.ACCENT
        setStatus("File loaded - click Calculate", Theme.ACCENT)
        dropLabel.text = "File ready"; dropLabel.foreground = Theme.SUCCESS
    }

    private fun browseFile() {
        val fc = JFileChooser()
        fc.dialogTitle = "Select Student Excel File"
        fc.fileFilter = javax.swing.filechooser.FileNameExtensionFilter("Excel Files", "xlsx", "xls")
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) setFile(fc.selectedFile)
    }

    private fun processFile() {
        val f = inputFile ?: run { setStatus("Please select a file first", Theme.DANGER); return }
        try {
            students.clear()
            students.addAll(ExcelReader().readStudents(f.absolutePath))
            tableModel.rowCount = 0
            students.forEachIndexed { i, s ->
                tableModel.addRow(arrayOf(i + 1, s.name, s.mark, s.grade, s.gradeDescription))
            }
            statTotal.setValue(students.size.toString())
            statAvg.setValue("%.1f".format(students.map { it.mark }.average()))
            statHigh.setValue(students.maxOf { it.mark }.toString())
            statLow.setValue(students.minOf { it.mark }.toString())
            exportBtn.isEnabled = true
            setStatus(" students processed!", Theme.SUCCESS)
        } catch (e: Exception) {
            setStatus("Error: ", Theme.DANGER)
        }
    }

    private fun exportFile() {
        if (students.isEmpty()) { setStatus("Nothing to export", Theme.WARNING); return }
        val fc = JFileChooser()
        fc.dialogTitle = "Save Grade Results"
        fc.fileFilter = javax.swing.filechooser.FileNameExtensionFilter("Excel Files", "xlsx")
        val ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
        fc.selectedFile = File("grade_results_.xlsx")
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            var out = fc.selectedFile.absolutePath
            if (!out.endsWith(".xlsx")) out += ".xlsx"
            try {
                ExcelWriter().writeResults(students, out)
                setStatus("Saved to ", Theme.SUCCESS)
                JOptionPane.showMessageDialog(this, "Results exported!\n", "Export Complete", JOptionPane.INFORMATION_MESSAGE)
            } catch (e: Exception) {
                setStatus("Export failed: ", Theme.DANGER)
            }
        }
    }

    private fun setStatus(msg: String, color: Color) { statusLabel.text = msg; statusLabel.foreground = color }
}

fun main() {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    SwingUtilities.invokeLater { GradeCalculatorUI() }
}
