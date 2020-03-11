package com.jetbrains.test.fontChecker.ui

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.htmlComponent
import com.intellij.ui.components.panels.VerticalLayout
import com.intellij.ui.layout.panel
import java.awt.BorderLayout
import java.awt.Font
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class FontDiffDialog(
    private val stableFont: Font,
    private val newFont: Font,
    private val text: String
) : DialogWrapper(true) {
    init {
        init()
        title = "Font diff"
    }

    override fun createCenterPanel(): JComponent? {
        return JPanel( VerticalLayout(2)).apply {
            add(JLabel(text).apply { font = stableFont })
            add(JLabel(text).apply { font = newFont })
        }
    }


}