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
    maven("https://jetbrains.bintray.com/intellij-third-party-dependencies")
}
val end2endTestImplementation by configurations.creating {
    extendsFrom(configurations.testImplementation.get())
}
dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("ru.yandex.qatools.ashot:ashot:1.5.4")
    implementation("com.lowagie:itext:2.1.7")

    testImplementation("junit", "junit", "4.12")
    testImplementation("org.jetbrains.test:remote-robot:0.0.1.SNAPSHOT-1")
    testImplementation("com.squareup.okhttp3:okhttp:3.9.0")
}

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

task("uiTest") {
    group = "verification"
    dependencies {
        implementation("org.jetbrains.test:robot-server-plugin:0.0.1.SNAPSHOT-1")
    }
    dependsOn(":test")
}