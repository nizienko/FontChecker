package com.jetbrains.test.fontChecker

import com.jetbrains.test.RemoteRobot
import com.jetbrains.test.data.RemoteComponent
import com.jetbrains.test.fixtures.ComponentFixture
import com.jetbrains.test.fixtures.ContainerFixture
import com.jetbrains.test.search.locators.byXpath
import com.jetbrains.test.utils.waitFor
import org.junit.Before
import org.junit.Test
import java.awt.event.KeyEvent.VK_A
import java.awt.event.KeyEvent.VK_CONTROL
import java.nio.file.Path
import java.nio.file.Paths


class End2EndTest {
    companion object {
        private val casesPath = Paths.get("resources/fonts/cases.txt").toAbsolutePath()
        private val stableFontPath = Paths.get("resources/fonts/JetBrainsMono-Regular_0.21.ttf").toAbsolutePath()
        private val newFontPath = Paths.get("resources/fonts/JetBrainsMono-Regular_1.0.3.ttf").toAbsolutePath()
        private val reportPath = Paths.get("resources/fonts").toAbsolutePath()
    }

    @Before
    fun deleteReport() {
        reportPath.resolve("report.pdf").toFile().delete()
    }

    @Test
    fun test() = uiTest {
        val ideaFrame = find<ContainerFixture>(
            byXpath("//div[@class='IdeFrameImpl']")
        )

        val toolsMenu = ideaFrame.find<ContainerFixture>(
            byXpath("//div[@accessiblename='Tools' and @class='ActionMenu' and @text='Tools']")
        )

        toolsMenu.click()

        toolsMenu.findAll<ComponentFixture>(
            byXpath("//div[@class='ActionMenuItem' and @text='Compare fonts']")
        ).first()
            .click()

        find<CompareFontDialog>(CompareFontDialog.locator).apply {
            casesTextField.setPath(casesPath)
            stableFontTextField.setPath(stableFontPath)
            newFontTextField.setPath(newFontPath)
            reportTextField.setPath(reportPath)
            ok()
        }
        waitFor(errorMessage = "failed to create report file") { reportPath.resolve("report.pdf").toFile().exists() }
    }
}

// ------------------------------------------------------------

fun uiTest(url: String = "http://127.0.0.1:8080", test: RemoteRobot.() -> Unit) {
    RemoteRobot(url).apply(test)
}

class CompareFontDialog(remoteRobot: RemoteRobot, remoteComponent: RemoteComponent) : ContainerFixture(
    remoteRobot,
    remoteComponent
) {
    companion object {
        val locator = byXpath("//div[@accessiblename='Choose fonts to compare' and @class='MyDialog']")
    }

    val casesTextField
        get() = find<TextField>(
            byXpath("//div[@class='JLabel' and @text='Cases']//following::div[@class='TextFieldWithBrowseButton'][1]")
        )

    val stableFontTextField
        get() = find<TextField>(
            byXpath("//div[@class='JLabel' and @text='Stable font file']//following::div[@class='TextFieldWithBrowseButton'][1]")
        )
    val newFontTextField
        get() = find<TextField>(
            byXpath("//div[@class='JLabel' and @text='New font file']//following::div[@class='TextFieldWithBrowseButton'][1]")
        )

    val reportTextField
        get() = find<TextField>(
            byXpath("//div[@class='JLabel' and @text='Report']//following::div[@class='TextFieldWithBrowseButton'][1]")
        )

    fun ok() {
        find<ComponentFixture>(byXpath("//div[@class='JButton' and @text='OK']")).click()
    }

    fun cancel() {
        find<ComponentFixture>(byXpath("//div[@class='JButton' and @text='Cancel']")).click()
    }
}

class TextField(remoteRobot: RemoteRobot, remoteComponent: RemoteComponent) : ContainerFixture(
    remoteRobot,
    remoteComponent
) {
    fun setPath(path: Path) {
        click()
        val text = path.toString()
        remoteRobot.execute {
            robot.pressKey(VK_CONTROL)
            robot.pressAndReleaseKey(VK_A)
            robot.releaseKey(VK_CONTROL)
        }
        remoteRobot.execute("robot.enterText('$text')")
    }
}