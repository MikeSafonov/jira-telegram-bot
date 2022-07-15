import info.solidsoft.gradle.pitest.PitestPluginExtension
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import io.spring.gradle.dependencymanagement.dsl.ImportsHandler
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

buildscript {
    repositories {
        mavenCentral()
    }
    configurations.maybeCreate("pitest")
    dependencies {
        classpath("info.solidsoft.gradle.pitest:gradle-pitest-plugin:1.4.0")
        "pitest"("io.kotest:kotest-plugins-pitest:4.3.1")
    }
}


plugins {
    kotlin("jvm") version "1.4.20"
    idea
    jacoco
    id("org.jetbrains.kotlin.plugin.spring") version "1.4.20"
    id("org.jetbrains.kotlin.plugin.jpa") version "1.4.20"
    id("org.springframework.boot") version "2.2.1.RELEASE"
    id("org.sonarqube") version "3.2.0"
    `build-scan` version "2.2"
}

apply(plugin = "io.spring.dependency-management")
apply(plugin = "info.solidsoft.pitest")


group = "com.github.mikesafonov"
version = "1.7.0"

repositories {
    mavenCentral()
    jcenter()
}

tasks.withType<Wrapper> {
    gradleVersion = "5.6.2"
    distributionType = Wrapper.DistributionType.BIN
}

tasks.getByName<BootJar>("bootJar") {
    archiveFileName.set("jira-telegram-bot.jar")
    launchScript()
}

springBoot {
    buildInfo {
        properties {
            additional = mapOf("version" to project.version)
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs = listOf("-Xjsr305=strict")
}

configure<DependencyManagementExtension> {
    imports(delegateClosureOf<ImportsHandler> {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:Greenwich.SR3")
    })
}

sourceSets.create("testIntegration") {
    java.srcDir("src/testIntegration/kotlin")
    resources.srcDir("src/testIntegration/resources")
    compileClasspath += sourceSets.getByName("main").output + sourceSets.getByName("test").output
    runtimeClasspath += sourceSets.getByName("main").output + sourceSets.getByName("test").output
}

tasks.withType<Test> {
    useJUnitPlatform()
}

configurations {
    compile {
        exclude(module = "spring-boot-starter-logging")
    }
}

val testIntegrationImplementation by configurations.existing {
    extendsFrom(configurations["testImplementation"], configurations["implementation"])
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.cloud:spring-cloud-starter-vault-config")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("org.telegram:telegrambots-spring-boot-starter:5.3.0")

    implementation("org.freemarker:freemarker:2.3.30")
    implementation("no.api.freemarker:freemarker-java8:1.3.0")

    implementation("io.github.microutils:kotlin-logging-jvm:2.0.3")
    implementation("org.apache.logging.log4j:log4j-web")

    implementation("io.micrometer:micrometer-core")
    implementation("io.micrometer:micrometer-registry-prometheus")

    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("mysql:mysql-connector-java:5.1.49")

    implementation("org.flywaydb:flyway-core")
    implementation("com.atlassian.jira:jira-rest-java-client-core:5.1.6")
    implementation("io.atlassian.fugue:fugue:4.7.2")
    implementation("com.google.oauth-client:google-oauth-client:1.30.4")
    implementation("com.google.http-client:google-http-client-jackson2:1.42.2")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation("io.kotest:kotest-property-jvm:4.3.1")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:4.3.1")
    testImplementation("io.mockk:mockk:1.10.5")
    testImplementation("org.junit.jupiter:junit-jupiter-api")

    "testIntegrationImplementation"("org.springframework.boot:spring-boot-starter-test")
    "testIntegrationImplementation"("org.springframework.boot:spring-boot-starter-data-jpa")
    "testIntegrationImplementation"("com.h2database:h2:1.4.199")
    "testIntegrationImplementation"("org.postgresql:postgresql")
    "testIntegrationImplementation"("mysql:mysql-connector-java:5.1.49")
    "testIntegrationImplementation"("org.testcontainers:postgresql:1.16.0")
    "testIntegrationImplementation"("org.testcontainers:mysql:1.16.0")
}

jacoco {
    toolVersion = "0.8.3"
}

tasks.jacocoTestReport {
    reports {
        xml.isEnabled = true
        csv.isEnabled = false
    }
}

tasks.register("testIntegration", Test::class.java){
    testClassesDirs = sourceSets.getByName("testIntegration").output.classesDirs
    classpath = sourceSets.getByName("testIntegration").runtimeClasspath
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}


buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"

    publishAlways()
}

configure<PitestPluginExtension> {
    pitestVersion = "1.4.5"
    testPlugin = "KotlinTest"
    avoidCallsTo = setOf("kotlin.jvm.internal")
    targetClasses = setOf("com.github.mikesafonov.jira.telegram.service.*")
    targetTests = setOf("com.github.mikesafonov.jira.telegram.service.*")
    threads = 2
    outputFormats = setOf("HTML", "XML")
    timestampedReports = false
    useClasspathFile = true
    enableDefaultIncrementalAnalysis = true
    setHistoryInputLocation(".pitest/pitHistory.txt")
    setHistoryOutputLocation(".pitest/pitHistory.txt")
}

sonarqube {
    properties {
        property("sonar.sourceEncoding", "UTF-8")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.organization", "mikesafonov-github")
        property("sonar.projectName", "jira-telegram-bot")
        property("sonar.projectKey", "MikeSafonov_jira-telegram-bot")
        property("sonar.projectVersion", "1.7.0")
        property("sonar.sources", "src/main")
        property("sonar.tests", "src/test")
        property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/test/jacocoTestReport.xml")
        property("sonar.coverage.exclusions", "**/com/github/mikesafonov/jira/telegram/config/*, **/com/github/mikesafonov/jira/telegram/dao/*, **/main/resources/db/**")
        property("sonar.exclusions", "**/main/resources/db/**")
        property("sonar.links.homepage", "https://github.com/MikeSafonov/jira-telegram-bot")
        property("sonar.links.ci", "https://github.com/MikeSafonov/jira-telegram-bot/actions")
        property("sonar.links.scm", "https://github.com/MikeSafonov/jira-telegram-bot")
        property("sonar.links.issue", "https://github.com/MikeSafonov/jira-telegram-bot/issues")
    }
}
