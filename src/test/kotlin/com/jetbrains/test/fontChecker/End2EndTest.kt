package com.jetbrains.test.fontChecker

import com.jetbrains.test.RemoteRobot
import org.junit.Test


class End2EndTest {

    @Test
    fun test() {
        val remoteRobot = RemoteRobot("http://127.0.0.1:8080")
    }
}