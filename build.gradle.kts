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
val robotServerPluginImplementation by configurations.creating


dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("ru.yandex.qatools.ashot:ashot:1.5.4")
    implementation("com.lowagie:itext:2.1.7")

    testImplementation("junit", "junit", "4.12")
    testImplementation("org.jetbrains.test:remote-robot:0.0.1.SNAPSHOT-1")
    testImplementation("com.squareup.okhttp3:okhttp:3.9.0")

    robotServerPluginImplementation("org.jetbrains.test:robot-server-plugin:0.0.1.SNAPSHOT-1")
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
    changeNotes(
        """
      Add change notes here.<br>
      <em>most HTML tags may be used</em>"""
    )
}

task<Copy>("copyPlugin") {
    dependsOn(":prepareSandbox")
    val zipPath by lazy { robotServerPluginImplementation.files.first { it.extension == "zip" } }
    val zipFile = file(zipPath)
    val outputDir = file("build/idea-sandbox/plugins/")
    from(zipTree(zipFile))
    into(outputDir)
}

task<Copy>("cleanSandbox") {

}

tasks.getByName("runIde") {
    dependsOn(":copyPlugin")
}
