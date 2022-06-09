pluginManagement {
    repositories {
        maven { url = uri("https://repo.spring.io/milestone") }
        gradlePluginPortal()
    }
    plugins {
        id("org.gradle.test-retry") version "1.4.0"
        id("org.springframework.boot") version "2.6.6"
        id("io.spring.dependency-management") version "1.0.11.RELEASE"
        id("org.siouan.frontend-jdk11") version "6.0.0"
        id("org.springdoc.openapi-gradle-plugin") version "1.3.4"
    }
}

rootProject.name = "irida"