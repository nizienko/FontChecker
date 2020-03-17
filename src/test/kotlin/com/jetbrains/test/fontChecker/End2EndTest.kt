package com.jetbrains.test.fontChecker

import com.jetbrains.test.RemoteRobot
import com.jetbrains.test.data.RemoteComponent
import com.jetbrains.test.fixtures.ComponentFixture
import com.jetbrains.test.fixtures.ContainerFixture
import com.jetbrains.test.search.locators.byXpath
import com.jetbrains.test.stepsProcessing.StepLogger
import com.jetbrains.test.stepsProcessing.StepWorker
import com.jetbrains.test.stepsProcessing.step
import com.jetbrains.test.utils.waitFor
import org.junit.Before
import org.junit.Test
import java.awt.event.KeyEvent.VK_A
import java.awt.event.KeyEvent.VK_CONTROL
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Duration


class End2EndTest {
    companion object {
        private val casesPath = Paths.get("src/main/resources/fonts/cases.txt").toAbsolutePath()
        private val stableFontPath =
            Paths.get("src/main/resources/fonts/JetBrainsMono-Regular_0.21.ttf").toAbsolutePath()
        private val newFontPath = Paths.get("src/main/resources/fonts/JetBrainsMono-Regular_1.0.3.ttf").toAbsolutePath()
        private val reportPath = Paths.get("src/main/resources/fonts").toAbsolutePath()
    }

    init {
        StepWorker.registerProcessor(StepLogger())
    }

    @Before
    fun deleteReport() {
        step("Delete old report file") {
            reportPath.resolve("report.pdf").toFile().delete()
        }
    }

    @Test
    fun test() = uiTest {
        step("Open 'Compare Font Dialog'") {
            val ideaFrame = find<ContainerFixture>(
                byXpath("IdeFrame", "//div[@class='IdeFrameImpl']")
            )

            val toolsMenu = ideaFrame.find<ContainerFixture>(
                byXpath("Tools", "//div[@accessiblename='Tools' and @class='ActionMenu' and @text='Tools']")
            ).apply {
                click()
            }

            toolsMenu.findAll<ComponentFixture>(
                byXpath("Compare fonts", "//div[@class='ActionMenuItem' and @text='Compare fonts']")
            ).first().click()
        }
        step("Fill data") {
            find<CompareFontDialog>(CompareFontDialog.locator).apply {
                casesTextField.setPath(casesPath)
                stableFontTextField.setPath(stableFontPath)
                newFontTextField.setPath(newFontPath)
                reportTextField.setPath(reportPath)
                ok()
            }
        }
        step("Check that report is created") {
            waitFor(
                errorMessage = "failed to create report file",
                duration = Duration.ofSeconds(10)
            ) { reportPath.resolve("report.pdf").toFile().exists() }
        }
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
        get() = step("Cases TextField") {
            return@step find<TextField>(
                byXpath("//div[@class='JLabel' and @text='Cases']//following::div[@class='TextFieldWithBrowseButton'][1]")
            )
        }

    val stableFontTextField
        get() = step("Stable font file TextField") {
            return@step find<TextField>(
                byXpath("//div[@class='JLabel' and @text='Stable font file']//following::div[@class='TextFieldWithBrowseButton'][1]")
            )
        }
    val newFontTextField
        get() = step("New font file TextField") {
            return@step find<TextField>(
                byXpath("//div[@class='JLabel' and @text='New font file']//following::div[@class='TextFieldWithBrowseButton'][1]")
            )
        }

    val reportTextField
        get() = step("Report TextField") {
            return@step find<TextField>(
                byXpath("//div[@class='JLabel' and @text='Report']//following::div[@class='TextFieldWithBrowseButton'][1]")
            )
        }

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
    fun setPath(path: Path) = step(".. set path of '${path.fileName}'") {
        click()
        val text = path.toString()
        remoteRobot.execute {
            robot.pressKey(VK_CONTROL)
            robot.pressAndReleaseKey(VK_A)
            robot.releaseKey(VK_CONTROL)
            robot.enterText(text)
        }
    }
}