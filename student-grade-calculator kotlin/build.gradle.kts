plugins {
    kotlin("jvm") version "1.9.22"
    application
}

group = "com.gradecalculator"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.poi:poi:5.2.5")
    implementation("org.apache.poi:poi-ooxml:5.2.5")
    implementation("org.slf4j:slf4j-simple:2.0.9")
}

application {
    mainClass.set("com.gradecalculator.MainKt")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.gradecalculator.MainKt"
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    archiveBaseName.set("student-grade-calculator")
    archiveVersion.set("1.0.0")
}

kotlin {
    jvmToolchain(17)
}