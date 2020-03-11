plugins {
    id("org.jetbrains.intellij") version "0.4.16"
    java
    kotlin("jvm") version "1.3.61"
}

group = "com.jetbrains.test"
version = "1.0-SNAPSHOT"

repositories {
    maven { setUrl("https://dl.bintray.com/kotlin/kotlin-eap") }
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("ru.yandex.qatools.ashot:ashot:1.5.4")
    implementation("com.lowagie:itext:2.1.7")
    testCompile("junit", "junit", "4.12")
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    setPlugins("java")
    version = "2019.3.2"
}
configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}
tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    changeNotes("""
      Add change notes here.<br>
      <em>most HTML tags may be used</em>""")
}