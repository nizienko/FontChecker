package com.jetbrains.test.fontChecker

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.util.ui.ImageUtil
import com.jetbrains.test.fontChecker.ui.ChooseFontsDialog
import com.lowagie.text.*
import com.lowagie.text.pdf.PdfWriter
import ru.yandex.qatools.ashot.comparison.ImageDiff
import ru.yandex.qatools.ashot.comparison.ImageDiffer
import java.awt.Color
import java.awt.Font
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.file.Paths
import javax.imageio.ImageIO

class CompareFontAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val chooseFontFilesDialog = ChooseFontsDialog()
        if (chooseFontFilesDialog.showAndGet()) {
            println(chooseFontFilesDialog.stableFile.path)
            println(chooseFontFilesDialog.newFile.path)

            val doc = Document(PageSize.A4, 25F, 25F, 30F, 30F)
            PdfWriter.getInstance(doc, chooseFontFilesDialog.saveReportFile.apply { createNewFile() }.outputStream())
            doc.open()

            val stableFont = Font.createFont(Font.TRUETYPE_FONT, chooseFontFilesDialog.stableFile).deriveFont(35.0F)
            val newFont = Font.createFont(Font.TRUETYPE_FONT, chooseFontFilesDialog.newFile).deriveFont(35.0F)
            val cases = chooseFontFilesDialog.cases.readLines()
            val styles = listOf(0, 1, 2)

            cases.forEach { case ->
                //                doc.add(Paragraph("Case: $case"))
                val table = Table(4)
                styles.forEach { style ->
                    listOf(5, 7, 9, 12, 14, 16, 18, 20, 22, 24, 26, 32).forEach { size ->
                        val stableImage = generateImage(case, stableFont.deriveFont(style), size)
                        val newImage = generateImage(case, newFont.deriveFont(style), size)
                        val result = compare(stableImage, newImage)
                        if (result.hasDiff()) {
                            table.addCell(Cell("$size"))
                            table.addCell(Cell(Image.getInstance(stableImage, null)))
                            table.addCell(Cell(Image.getInstance(newImage, null)))
                            table.addCell(Cell(Image.getInstance(result.markedImage, null)))
                        }
                    }
                }
                doc.add(table)
            }
            doc.close()
        }
    }

    private fun compare(stableImage: BufferedImage, newImage: BufferedImage): ImageDiff {

        val diff = ImageDiffer().makeDiff(stableImage, newImage)

        val finalImage = ImageUtil.createImage(
            stableImage.width, stableImage.height * 3,
            BufferedImage.TYPE_INT_ARGB
        )
//        finalImage.createGraphics().apply {
//            drawImage(stableImage, 0, 0, null)
//            drawImage(newImage, 0, stableImage.height, null)
//            drawImage(diff.markedImage, 0, stableImage.height * 2, null)
//            color = if (diff.hasDiff()) {
//                Color.RED
//            } else {
//                Color.GRAY
//            }
//            drawLine(0, stableImage.height, stableImage.width, stableImage.height)
//            drawLine(0, stableImage.height * 2, stableImage.width, stableImage.height * 2)
//        }

//        val bytes = ByteArrayOutputStream().use { b ->
//            ImageIO.write(finalImage, "png", b)
//            b.toByteArray()
//        }
//        val path = Paths.get("C:\\imgs\\result_${System.currentTimeMillis()}.png")
//        path.toFile().writeBytes(bytes)
        return diff
    }

    private fun generateImage(text: String, font: Font, size: Int): BufferedImage {
        val image = ImageUtil.createImage(
            (text.length * size * 0.85).toInt() + 10, size + 30,
            BufferedImage.TYPE_INT_ARGB
        )
        val g = image.createGraphics()
        g.setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB
        )

        g.color = Color.WHITE
        g.fillRect(0, 0, image.width, image.height)
        g.color = Color.BLACK
        g.font = font.deriveFont(size.toFloat())
        g.drawString(text, 5, size + 5)
        g.dispose()
        return image
    }
}

data class ComparingData(
    val stableImage: BufferedImage,
    val newImage: BufferedImage,
    val diff: ImageDiff
)