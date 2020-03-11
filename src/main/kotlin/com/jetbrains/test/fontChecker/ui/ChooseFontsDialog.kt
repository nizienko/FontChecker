package com.jetbrains.test.fontChecker.ui

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.layout.panel
import java.io.File
import java.nio.file.Paths
import javax.swing.JComponent


class ChooseFontsDialog : DialogWrapper(true) {
    init {
        init()
        title = "Choose fonts to compare"
    }

    private lateinit var stableFontTextField: TextFieldWithBrowseButton
    private lateinit var newFontTextField: TextFieldWithBrowseButton
    private lateinit var casesTextField: TextFieldWithBrowseButton
    private lateinit var reportTextField: TextFieldWithBrowseButton

    override fun createCenterPanel(): JComponent? {
        val ttfFileDescriptor = FileChooserDescriptorFactory.createSingleFileDescriptor("ttf")
        val txtFileDescriptor = FileChooserDescriptorFactory.createSingleFileDescriptor("txt")

        return panel {
            row { label("Cases") }
            row {
                casesTextField = textFieldWithBrowseButton("cases", fileChooserDescriptor = txtFileDescriptor,
                    value = "C:\\Users\\User\\IdeaProjects\\FontChecker\\src\\main\\resources\\fonts\\cases.txt")
            }
            row { label("Stable font file") }
            row {
                stableFontTextField = textFieldWithBrowseButton("Choose STABLE font", fileChooserDescriptor = ttfFileDescriptor,
                value = "C:\\Users\\User\\IdeaProjects\\FontChecker\\src\\main\\resources\\fonts\\JetBrainsMono-Regular_0.21.ttf")
            }
            row { label("New font file") }
            row {
                newFontTextField = textFieldWithBrowseButton("Choose NEW font", fileChooserDescriptor = ttfFileDescriptor,
                value = "C:\\Users\\User\\IdeaProjects\\FontChecker\\src\\main\\resources\\fonts\\JetBrainsMono-Regular_1.0.3.ttf")
            }
            row { label("Report") }
            row {
                reportTextField = textFieldWithBrowseButton("Choose NEW font", fileChooserDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor(),
                    value = "C:\\Users\\User\\IdeaProjects\\FontChecker\\src\\main\\resources\\fonts\\")
            }
        }
    }

    val stableFile: File
        get() = Paths.get(stableFontTextField.text).toFile()
    val newFile: File
        get() = Paths.get(newFontTextField.text).toFile()
    val cases: File
        get() = Paths.get(casesTextField.text).toFile()
    val saveReportFile: File
        get() = Paths.get(reportTextField.text).resolve("report.pdf").toFile()
}