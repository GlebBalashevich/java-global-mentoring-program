import com.diffplug.gradle.spotless.FormatExtension
import com.diffplug.gradle.spotless.SpotlessExtension

plugins {
    id("java")
    checkstyle
    jacoco
    id("com.diffplug.spotless") version "6.7.2" apply false
}

group = "com.epam.mentoring"
version = "1.0-SNAPSHOT"

subprojects {
    apply(plugin = "java")
    apply(plugin = "checkstyle")
    apply(plugin = "jacoco")
    apply(plugin = "com.diffplug.spotless")

    repositories {
        mavenCentral()
    }

    val slf4jVersion = "2.0.6"

    dependencies {
        implementation("org.slf4j:slf4j-api:$slf4jVersion")
        implementation("org.slf4j:slf4j-simple:$slf4jVersion")

        testImplementation("org.assertj:assertj-core:3.23.1")
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    }

    configure<SpotlessExtension> {
        format("misc") {
            target("*.md", ".gitignore")
            commonFormat()
        }
        java {
            commonFormat()
            removeUnusedImports()
            importOrderFile("$rootDir/gradle/spotless/.importorder")
            eclipse().configFile("$rootDir/gradle/spotless/formatter.xml")
            targetExclude("*/generated/**/*.*")
        }
    }

    configure<CheckstyleExtension> {
        configFile = file("$rootDir/gradle/checkstyle/checkstyle.xml")
        configDirectory.set(file("$rootDir/gradle/checkstyle"))
        toolVersion = "10.3"
    }

    tasks.jacocoTestReport {
        reports {
            xml.required.set(false)
            csv.required.set(false)
            html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
        }
    }

    tasks.jacocoTestCoverageVerification {
        violationRules {
            rule {
                limit {
                    minimum = "0.8".toBigDecimal()
                }
            }
        }
    }

    tasks.compileJava{
        options.compilerArgs.plusAssign(listOf("-Xlint:unchecked", "-Xlint:deprecation"))
        dependsOn("spotlessApply")
    }

    tasks.test {
        finalizedBy(tasks.jacocoTestReport)
        useJUnitPlatform()
    }
    tasks.jacocoTestReport {
        dependsOn(tasks.jacocoTestCoverageVerification)
    }
}

fun FormatExtension.commonFormat() {
    trimTrailingWhitespace()
    indentWithSpaces()
    endWithNewline()
}
