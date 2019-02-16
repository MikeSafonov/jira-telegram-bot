import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import io.spring.gradle.dependencymanagement.dsl.ImportsHandler
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.21"
    id ("org.jetbrains.kotlin.plugin.spring") version "1.3.21"
    id ("org.jetbrains.kotlin.plugin.jpa") version "1.3.21"
    id("org.springframework.boot") version "2.1.2.RELEASE"

}

apply(plugin = "io.spring.dependency-management")

group = "com.github.mikesafonov"


repositories {
    mavenCentral()
}

configure<DependencyManagementExtension> {
    imports(delegateClosureOf<ImportsHandler> {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:Greenwich.RELEASE")
    })
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    compile("org.springframework.boot:spring-boot-starter-web")
    compile("org.springframework.boot:spring-boot-starter-data-jpa")

    compile("com.fasterxml.jackson.module:jackson-module-kotlin")

    compile("org.telegram:telegrambots-spring-boot-starter:4.1.2")

    compile("com.github.spullara.mustache.java:compiler:0.9.6")

    compile("io.github.microutils:kotlin-logging:1.6.22")

    compile("org.postgresql:postgresql:42.2.5")
    compile("org.flywaydb:flyway-core:5.2.4")


    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs = listOf("-Xjsr305=strict")
}

tasks.withType<Wrapper> {
    gradleVersion = "5.2.1"
    distributionType = Wrapper.DistributionType.ALL
}