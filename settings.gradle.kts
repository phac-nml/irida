pluginManagement {
    repositories {
        maven { url = uri("https://repo.spring.io/milestone") }
        gradlePluginPortal()
    }
    plugins {
        id("com.github.node-gradle.node") version "3.4.0"
        id("io.spring.dependency-management") version "1.0.11.RELEASE"
        id("org.gradle.test-retry") version "1.4.0"
        id("org.springframework.boot") version "2.7.3"
        id("org.springdoc.openapi-gradle-plugin") version "1.3.4"
    }
}

rootProject.name = "irida"