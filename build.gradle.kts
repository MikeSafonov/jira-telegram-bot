import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

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

tasks.withType<BootJar>{
    mainClassName = "com.github.mikesafonov.jira.telegram.Application"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    compile("com.fasterxml.jackson.module:jackson-module-kotlin")
    compile("org.springframework.boot:spring-boot-starter-web")
    compile ("org.telegram:telegrambots-spring-boot-starter:4.1.2")
    
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