pluginManagement {
    repositories {
        maven { url = uri("https://repo.spring.io/milestone") }
        gradlePluginPortal()
    }
    plugins {
        id("com.github.node-gradle.node") version "7.1.0"
        id("io.spring.dependency-management") version "1.1.6"
        id("org.gradle.test-retry") version "1.6.0"
        id("org.springframework.boot") version "2.7.18"
        id("org.springdoc.openapi-gradle-plugin") version "1.9.0"
    }
}

rootProject.name = "irida"
