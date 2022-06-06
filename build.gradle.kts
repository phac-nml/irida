plugins {
    id("org.gradle.test-retry") version "1.4.0"
    id("org.springframework.boot") version "2.6.6"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.siouan.frontend-jdk11") version "6.0.0"
    id("com.github.johnrengelman.processes") version "0.5.0"
    id("org.springdoc.openapi-gradle-plugin") version "1.3.4"
    java
    war
}

group = "ca.corefacility.bioinformatics"
version = "22.07-SNAPSHOT"
description = "irida"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://clojars.org/repo")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter") {
        exclude(group = "org.apache.logging.log4j", module = "log4j-to-slf4j")
    }
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.apache.oltu.oauth2:org.apache.oltu.oauth2.client:1.0.0") {
        exclude(group = "org.slf4j")
    }
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.data:spring-data-envers") {
        exclude(group = "org.slf4j")
    }
    implementation("org.liquibase:liquibase-core")
    implementation("org.hibernate:hibernate-envers")
    implementation("org.apache.commons:commons-dbcp2") {
        exclude(group = "commons-logging", module = "commons-logging")
    }
    implementation("commons-net:commons-net:3.8.0")
    implementation("org.apache.jena:jena-tdb:4.3.2") {
        exclude(group = "xml-apis")
        exclude(group = "org.slf4j")
    }
    implementation("org.apache.jena:jena-text:4.3.2") {
        exclude(group = "xml-apis")
        exclude(group = "org.slf4j")
        exclude(group = "log4j")
    }
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-web-services")
    implementation("org.springframework.boot:spring-boot-starter-hateoas")
    implementation("org.springframework.security.oauth:spring-security-oauth2:2.3.6.RELEASE") {
        exclude(group = "org.codehaus.jackson", module = "jackson-mapper-asl")
    }
    implementation("commons-io:commons-io:2.11.0")
    implementation("commons-fileupload:commons-fileupload:1.4")
    implementation("org.apache.poi:poi-ooxml:5.2.2") {
        exclude(group = "stax", module = "stax-api")
        exclude(group = "org.apache.logging.log4j", module = "log4j-api")
    }
    implementation("org.thymeleaf:thymeleaf-spring5")
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity5")
    implementation("com.github.mxab.thymeleaf.extras:thymeleaf-extras-data-attribute")
    implementation("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect")
    implementation("commons-beanutils:commons-beanutils:1.9.4")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.apache.logging.log4j:log4j-api")
    implementation("org.apache.logging.log4j:log4j-to-slf4j")
    implementation("com.google.guava:guava:31.0.1-jre")
    implementation("commons-cli:commons-cli:1.2")
    implementation("uk.ac.babraham:fastqc:0.11.9-nml-custom")
    implementation("org.aspectj:aspectjweaver")
    implementation("org.biojava:biojava3-core:3.0")
    implementation("org.apache.commons:commons-csv:1.8")
    implementation("com.sksamuel.diff:diff:1.1.11")
    implementation("org.pf4j:pf4j:2.4.0")
    implementation("com.github.jmchilton.blend4j:blend4j:0.2.1-2201df9") {
        exclude(group = "javax.xml.stream")
        exclude(group = "com.sun.xml.bind")
    }
    implementation("net.matlux:jvm-breakglass:0.0.8")
    implementation("com.google.code.gson:gson")
    implementation("com.github.pjfanning:excel-streaming-reader:3.6.1")
    implementation("org.springdoc:springdoc-openapi-webmvc-core:1.5.6") {
        exclude(group = "jakarta.xml.bind", module = "jakarta.xml.bind-api")
        exclude(group = "jakarta.validation", module = "jakarta.validation-api")
    }
    runtimeOnly("mysql:mysql-connector-java")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.springframework.boot", module = "spring-boot-test-autoconfigure")
    }
    testImplementation("org.junit.platform:junit-platform-launcher")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.mockito:mockito-core")
    testImplementation("io.rest-assured:rest-assured")
    testImplementation("io.rest-assured:json-path")
    testImplementation("com.github.springtestdbunit:spring-test-dbunit:1.3.0")
    testImplementation("org.dbunit:dbunit:2.7.2") {
        exclude(group = "org.slf4j")
    }
    testImplementation("org.seleniumhq.selenium:selenium-support:3.141.59")
    testImplementation("org.seleniumhq.selenium:selenium-chrome-driver:3.141.59")
    testImplementation("org.mockftpserver:MockFtpServer:2.6")
    providedCompile("org.springframework.boot:spring-boot-starter-tomcat")
}

tasks.bootRun {
    dependsOn(":assembleFrontend")
    systemProperties(System.getProperties().mapKeys { it.key as String })
}

frontend {
    nodeVersion.set("16.15.0")
    nodeInstallDirectory.set(file("${projectDir}/src/main/webapp/node"))
    yarnEnabled.set(true)
    yarnVersion.set("3.1.1")
    assembleScript.set("build")
    installScript.set("install")
    packageJsonDirectory.set(file("${projectDir}/src/main/webapp"))
}

tasks.withType<Test> {
    systemProperties(mapOf("junit.platform.execution.listeners.deactivate" to "ca.corefacility.bioinformatics.irida.junit5.listeners.Integration*"))
    useJUnitPlatform {
        excludeTags("IntegrationTest")
    }
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
    }
}

fun createIntegrationTestTask(name: String, tags: String?, excludeListeners: String?): TaskProvider<Test> {
    return tasks.register<Test>("${name}ITest") {
        val defaultSystemProperties = mapOf(
            "junit.platform.execution.listeners.deactivate" to excludeListeners,
            "spring.datasource.url" to "jdbc:mysql://localhost:3306/irida_integration_test",
            "irida.db.profile" to "it",
            "irida.it.nosandbox" to "true",
            "irida.it.headless" to "true",
            "spring.profiles.active" to "test",
            "irida.it.rootdirectory" to temporaryDir,
            "sequence.file.base.directory" to "${temporaryDir}/sequence-file-base",
            "reference.file.base.directory" to "${temporaryDir}/reference-file-base",
            "output.file.base.directory" to "${temporaryDir}/output-file-base"
        )
        val providedSystemProperties = System.getProperties().mapKeys { it.key as String }
        systemProperties(defaultSystemProperties + providedSystemProperties)

        useJUnitPlatform {
            includeTags(tags)
        }
        testLogging {
            events("passed", "skipped", "failed")
            showStandardStreams = true
        }
        retry {
            maxRetries.set(5)
        }
    }
}

val integrationTestsMap = mapOf(
    "ui" to mapOf(
        "tags" to "IntegrationTest & UI",
        "excludeListeners" to "ca.corefacility.bioinformatics.irida.junit5.listeners.UnitTestListener"
    ),
    "service" to mapOf(
        "tags" to "IntegrationTest & Service",
        "excludeListeners" to "ca.corefacility.bioinformatics.irida.junit5.listeners.*"
    ),
    "rest" to mapOf(
        "tags" to "IntegrationTest & Rest",
        "excludeListeners" to "ca.corefacility.bioinformatics.irida.junit5.listeners.*"
    ),
    "galaxy" to mapOf(
        "tags" to "IntegrationTest & Galaxy & !Pipeline",
        "excludeListeners" to "ca.corefacility.bioinformatics.irida.junit5.listeners.*"
    ),
    "galaxyPipeline" to mapOf(
        "tags" to "IntegrationTest & Galaxy & Pipeline",
        "excludeListeners" to "ca.corefacility.bioinformatics.irida.junit5.listeners.*"
    ),
)

integrationTestsMap.forEach {
    createIntegrationTestTask(it.key, it.value.get("tags"), it.value.get("excludeListeners"))
}

// we do not want to build an executable war so skip this task
tasks.bootWar { enabled = false }

tasks.war {
    //dependsOn(":toolsListExport")
    //archiveClassifier.set("")
    exclude("node")
    exclude("node_modules/")
    exclude(".yarn/")
    exclude("resources/css/")
    exclude("resources/js/")
    exclude("webpack*")
    exclude(".yarn*")
    exclude("package.json")
    exclude("styles.js")
    exclude("entries.js")
    exclude(".eslintrc.js")
    exclude("postcss.config.js")
}

task<JavaExec>("toolsListExport") {
    classpath = java.sourceSets["main"].runtimeClasspath
    mainClass.set("ca.corefacility.bioinformatics.irida.util.ToolsListExporter")
    args("${buildDir}/tools-list.yml")
}

openApi {
    outputDir.set(file("${buildDir}/doc"))
    outputFileName.set("open-api.json")
    //forkProperties.set("-Dspring.profiles.active=dev,swagger -Dirida.db.profile=it -Dspring.datasource.url=jdbc:mysql://localhost:3306/irida_integration_test")
}

tasks.named("processResources") {
    dependsOn(":assembleFrontend")
}

tasks.javadoc {
    destinationDir = file("${projectDir}/doc/developer/apidocs")
}
