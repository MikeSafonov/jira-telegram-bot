import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import io.spring.gradle.dependencymanagement.dsl.ImportsHandler
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    kotlin("jvm") version "1.3.21"
    idea
    jacoco
    id("org.jetbrains.kotlin.plugin.spring") version "1.3.21"
    id("org.jetbrains.kotlin.plugin.jpa") version "1.3.21"
    id("org.springframework.boot") version "2.1.3.RELEASE"
    `build-scan` version "2.2"
}

apply(plugin = "io.spring.dependency-management")

group = "com.github.mikesafonov"


repositories {
    mavenCentral()
    jcenter()
}

tasks.withType<Wrapper> {
    gradleVersion = "5.2.1"
    distributionType = Wrapper.DistributionType.ALL
}

tasks.getByName<BootJar>("bootJar") {
    launchScript()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs = listOf("-Xjsr305=strict")
}

configure<DependencyManagementExtension> {
    imports(delegateClosureOf<ImportsHandler> {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:Greenwich.RELEASE")
    })
}

tasks.withType<Test> {
    useJUnitPlatform()
}

configurations {
    compile {
        exclude(module = "spring-boot-starter-logging")
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    compile("org.springframework.boot:spring-boot-starter-web")
    compile("org.springframework.boot:spring-boot-starter-data-jpa")
    compile("org.springframework.boot:spring-boot-starter-log4j2")

    compile("com.fasterxml.jackson.module:jackson-module-kotlin")

    compile("org.telegram:telegrambots-spring-boot-starter:4.1.2")

    compile("org.freemarker:freemarker:2.3.28")
    compile("no.api.freemarker:freemarker-java8:1.3.0")

    compile("io.github.microutils:kotlin-logging:1.6.22")
    compile("org.apache.logging.log4j:log4j-web")

    compile("org.postgresql:postgresql")
    compile("com.h2database:h2")
    compile("mysql:mysql-connector-java")
    compile("org.flywaydb:flyway-core")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation ("io.kotlintest:kotlintest-runner-junit5:3.2.1")
    testImplementation("io.mockk:mockk:1.9.1")
    testRuntimeOnly ("org.junit.jupiter:junit-jupiter-engine")
}

jacoco{
    toolVersion = "0.8.3"
}

tasks.jacocoTestReport{
    reports {
        xml.isEnabled = true
        csv.isEnabled = false
    }
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
    
    publishAlways()
}