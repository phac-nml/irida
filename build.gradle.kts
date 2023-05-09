import com.github.gradle.node.pnpm.task.PnpmTask
import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
    id("base")
    id("java")
    id("maven-publish")
    id("war")
    id("com.github.node-gradle.node")
    id("io.spring.dependency-management")
    id("org.gradle.test-retry")
    id("org.springframework.boot")
    id("org.springdoc.openapi-gradle-plugin")
}

group = "ca.corefacility.bioinformatics"
version = "23.01.3"
description = "irida"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    withJavadocJar()
    withSourcesJar()
}

tasks.wrapper {
    gradleVersion = "7.4.2"
    distributionSha256Sum = "29e49b10984e585d8118b7d0bc452f944e386458df27371b49b4ac1dec4b7fda"
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            pom {
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("eric")
                        name.set("Eric Enns")
                        email.set("eric.enns@phac-aspc.gc.ca")
                        organization.set("Public Health Agency of Canada")
                        organizationUrl.set("https://www.canada.ca/en/public-health.html")
                    }
                    developer {
                        id.set("aaron")
                        name.set("Aaron Petkau")
                        email.set("aaron.petkau@phac-aspc.gc.ca")
                        organization.set("Public Health Agency of Canada")
                        organizationUrl.set("https://www.canada.ca/en/public-health.html")
                    }
                    developer {
                        id.set("josh")
                        name.set("Josh Adam")
                        email.set("josh.adam@phac-aspc.gc.ca")
                        organization.set("Public Health Agency of Canada")
                        organizationUrl.set("https://www.canada.ca/en/public-health.html")
                    }
                    developer {
                        id.set("deep")
                        name.set("Sukhdeep Sidhu")
                        email.set("sukhdeep.sidhu@phac-aspc.gc.ca")
                        organization.set("Public Health Agency of Canada")
                        organizationUrl.set("https://www.canada.ca/en/public-health.html")
                    }
                    developer {
                        id.set("jeff")
                        name.set("Jeffrey Thiessen")
                        email.set("jeffrey.thiessen@phac-aspc.gc.ca")
                        organization.set("Public Health Agency of Canada")
                        organizationUrl.set("https://www.canada.ca/en/public-health.html")
                    }
                    developer {
                        id.set("katherine")
                        name.set("Katherine Thiessen")
                        email.set("katherine.thiessen@phac-aspc.gc.ca")
                        organization.set("Public Health Agency of Canada")
                        organizationUrl.set("https://www.canada.ca/en/public-health.html")
                    }
                }
                contributors {
                    contributor {
                        name.set("Franklin Bristow")
                    }
                    contributor {
                        name.set("Thomas Matthews")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/phac-nml/irida.git")
                    developerConnection.set("scm:git:ssh://git@github.com:phac-nml/irida.git")
                    url.set("https://github.com/phac-nml/irida")
                }
            }

            from(components["java"])
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter") {
        exclude(group = "org.apache.logging.log4j", module = "log4j-to-slf4j")
    }
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.security:spring-security-oauth2-authorization-server:0.3.1")
    implementation("org.springframework.security:spring-security-oauth2-resource-server:5.7.3")
    implementation("com.nimbusds:oauth2-oidc-sdk:10.1")
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
    implementation("org.apache.jena:jena-tdb:4.6.1") {
        exclude(group = "xml-apis")
        exclude(group = "org.slf4j")
    }
    implementation("org.apache.jena:jena-text:4.6.1") {
        exclude(group = "xml-apis")
        exclude(group = "org.slf4j")
        exclude(group = "log4j")
    }
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-web-services")
    implementation("org.springframework.boot:spring-boot-starter-hateoas")
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
    implementation("commons-cli:commons-cli:1.5.0")
    implementation("org.aspectj:aspectjweaver")
    implementation("org.apache.commons:commons-csv:1.9.0")
    implementation("com.sksamuel.diff:diff:1.1.11")
    implementation("org.pf4j:pf4j:2.4.0")
    implementation("org.biojava:biojava3-core:3.0")
    implementation("com.google.code.gson:gson")
    implementation("com.github.pjfanning:excel-streaming-reader:4.0.4")
    implementation("org.springdoc:springdoc-openapi-webmvc-core:1.6.11") {
        exclude(group = "jakarta.xml.bind", module = "jakarta.xml.bind-api")
        exclude(group = "jakarta.validation", module = "jakarta.validation-api")
    }

    // Microsoft Azure
    implementation("com.azure:azure-storage-blob:12.18.0") {
        exclude(group = "jakarta.xml.bind", module = "jakarta.xml.bind-api")
        exclude(group = "jakarta.activation", module = "jakarta.activation-api")
    }

    // Amazon AWS
    implementation("com.amazonaws:aws-java-sdk-s3:1.12.326") {
        exclude(group = "commons-logging", module = "commons-logging")
    }

    // Customized fastqc
    implementation(files("${projectDir}/lib/jbzip2-0.9.jar"))
    implementation(files("${projectDir}/lib/sam-1.103.jar"))
    implementation(files("${projectDir}/lib/cisd-jhdf5.jar"))
    implementation(files("${projectDir}/lib/fastqc-0.11.9.jar"))

    // Customized blend4j
    implementation("com.sun.jersey:jersey-client:1.19.4")
    implementation("com.sun.jersey:jersey-json:1.19.4")
    implementation("com.sun.jersey:jersey-core:1.19.4")
    implementation("com.sun.jersey.contribs:jersey-multipart:1.19.4")
    implementation("org.codehaus.jackson:jackson-core-asl:1.9.12")
    implementation("org.codehaus.jackson:jackson-mapper-asl:1.9.12")
    implementation("org.codehaus.jackson:jackson-jaxrs:1.9.12")
    implementation(files("${projectDir}/lib/blend4j-0.2.1-2201df9.jar"))

    // Runtime dependencies
    runtimeOnly("mysql:mysql-connector-java")
    providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")

    // Testing dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.springframework.boot", module = "spring-boot-test-autoconfigure")
    }
    testImplementation("org.junit.platform:junit-platform-launcher")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.mockito:mockito-core")
    testImplementation("io.rest-assured:rest-assured")
    testImplementation("io.rest-assured:json-path")
    testImplementation("com.github.springtestdbunit:spring-test-dbunit:1.3.0")
    testImplementation("org.dbunit:dbunit:2.7.3") {
        exclude(group = "org.slf4j")
    }
    testImplementation("org.seleniumhq.selenium:selenium-support:4.4.0")
    testImplementation("org.seleniumhq.selenium:selenium-chrome-driver:4.4.0")
    testImplementation("org.mockftpserver:MockFtpServer:3.0.0")
}

tasks.register<Zip>("packageDistribution") {
    dependsOn(listOf(":war", ":toolsListExport"))
    destinationDirectory.set(layout.buildDirectory.dir("dist"))

    from(layout.buildDirectory.dir("dist")) {
        include("*.war")
        into("${project.name}-${project.version}")
    }

    from(layout.buildDirectory) {
        include("tools-list.yml")
        into("${project.name}-${project.version}")
    }

    from(layout.projectDirectory) {
        include(listOf("CHANGELOG.md", "UPGRADING.md", "LICENSE"))
        into("${project.name}-${project.version}")
    }

    from(layout.projectDirectory.dir("packaging")) {
        include("README.md")
        into("${project.name}-${project.version}")
    }
}

extra["snakeyaml.version"] = "1.31"

tasks.build {
    dependsOn("packageDistribution")
}

// we need to set the classifier to "classes" for building pipeline plugins
tasks.jar {
    archiveClassifier.set("classes")
}

// we do not want to build an executable war so skip this task
tasks.bootWar { enabled = false }

tasks.war {
    archiveClassifier.set("")
    destinationDirectory.set(layout.buildDirectory.dir("dist"))
    exclude("node_modules/")
    exclude("resources/css/")
    exclude("resources/js/")
    exclude("webpack*")
    exclude(".npmrc")
    exclude("pnpm-lock.yaml")
    exclude("package.json")
    exclude("styles.js")
    exclude("entries.js")
    exclude(".eslintrc.js")
    exclude("postcss.config.js")
    rootSpec.exclude("**/jwk-key-store.jks")
}

node {
    download.set(true)
    version.set("16.15.1")
    pnpmVersion.set("7.4.0")
    workDir.set(file("${project.projectDir}/.gradle/nodejs"))
    nodeProjectDir.set(file("${project.projectDir}/src/main/webapp"))
}

tasks.named<PnpmTask>("pnpmInstall") {
    args.set(listOf("--frozen-lockfile"))
}

tasks.register<PnpmTask>("pnpmCachePrune") {
    group = "pnpm"
    description = "Prune pnpm cache directory."
    pnpmCommand.set(listOf("store", "prune"))
}

tasks.register<PnpmTask>("pnpmCacheDir") {
    group = "pnpm"
    description = "Report pnpm cache directory."
    pnpmCommand.set(listOf("store", "path"))
}

tasks.named<PnpmTask>("pnpmInstall") {
    finalizedBy(":pnpmCachePrune")
}

tasks.register<PnpmTask>("cleanWebapp") {
    dependsOn(":pnpmInstall")
    pnpmCommand.set(listOf("clean"))
}

tasks.register<PnpmTask>("buildWebapp") {
    inputs.files(fileTree("${project.projectDir}/src/main/webapp/resources"))
    inputs.file("${project.projectDir}/src/main/webapp/package.json")
    inputs.file("${project.projectDir}/src/main/webapp/pnpm-lock.yaml")

    outputs.dir("${project.projectDir}/src/main/webapp/dist")

    dependsOn(":pnpmInstall")
    pnpmCommand.set(listOf("clean", "build"))
}

tasks.register<PnpmTask>("startWebapp") {
    dependsOn(":pnpmInstall")
    pnpmCommand.set(listOf("clean", "start"))
    inputs.dir("${project.projectDir}/src/main/webapp/resources")
    outputs.dir("${project.projectDir}/src/main/webapp/dist")
}

springBoot {
    mainClass.set("ca.corefacility.bioinformatics.irida.IridaApplication")
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

val permittedTestSystemProperties = listOf(
    "java.io.tmpdir",
    "server.base.url",
    "server.port",
    "file.processing.decompress",
    "spring.datasource.dbcp2.max-wait",
    "webdriver.chrome.driver",
    "webdriver.selenium.url",
    "test.galaxy.url",
    "test.galaxy.invalid.url",
    "test.galaxy.invalid.url2",
)

fun createIntegrationTestTask(name: String, tags: String?, excludeListeners: String?): TaskProvider<Test> {
    return tasks.register<Test>("${name}ITest") {
        group = "verification"
        description = "Runs the ${name} integration test suite."
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
        val filteredProvidedSystemProperties = providedSystemProperties.filterKeys { it in permittedTestSystemProperties || it in defaultSystemProperties}
        systemProperties(defaultSystemProperties + filteredProvidedSystemProperties)

        useJUnitPlatform {
            includeTags(tags)
        }
        testLogging {
            events("passed", "skipped", "failed")
            showStandardStreams = true
        }
        retry {
            maxRetries.set(4)
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
    "fileSystem" to mapOf(
        "tags" to "IntegrationTest & FileSystem",
        "excludeListeners" to "ca.corefacility.bioinformatics.irida.junit5.listeners.*"
    ),
)

integrationTestsMap.forEach {
    createIntegrationTestTask(it.key, it.value.get("tags"), it.value.get("excludeListeners"))
}

task<JavaExec>("toolsListExport") {
    classpath = java.sourceSets["main"].runtimeClasspath
    mainClass.set("ca.corefacility.bioinformatics.irida.util.ToolsListExporter")
    args("${buildDir}/tools-list.yml")
}

tasks.named<BootRun>("bootRun") {
    if (project.gradle.taskGraph.hasTask(":generateOpenApiDocs")) {
        val defaultSystemProperties = mapOf(
            "spring.profiles.active" to "dev,swagger",
            "liquibase.update.database.schema" to "false",
            "spring.datasource.url" to "jdbc:mysql://localhost:3306/irida_test",
            "spring.datasource.dbcp2.max-wait" to "5000"
        )
        val providedSystemProperties = System.getProperties().mapKeys { it.key as String }
        val filteredProvidedSystemProperties = providedSystemProperties.filterKeys { it in defaultSystemProperties}
        systemProperties(defaultSystemProperties + filteredProvidedSystemProperties)
    }
}

task<Exec>("generateJWKKeyStore") {
    workingDir(file("${projectDir}/src/main/resources"))
    commandLine(listOf("keytool", "-genkeypair", "-alias", "JWK", "-keyalg", "RSA", "-noprompt", "-dname", "CN=irida.bioinformatics.corefacility.ca, OU=ID, O=IRIDA, L=IRIDA, S=IRIDA, C=CA", "-keystore", "jwk-key-store.jks", "-validity", "3650", "-storepass", "SECRET", "-keypass", "SECRET", "-storetype", "PKCS12"))
    outputs.file(file("${projectDir}/src/main/resources/jwk-key-store.jks"))
}

openApi {
    outputDir.set(file("${projectDir}/doc/swagger-ui"))
    outputFileName.set("open-api.json")
    waitTimeInSeconds.set(60)
}

tasks.processResources {
    filesMatching("version.properties") {
        expand(project.properties)
    }
    dependsOn(":buildWebapp")
    dependsOn(":generateJWKKeyStore")
}

tasks.javadoc {
    destinationDir = file("${project.projectDir}/doc/developer/apidocs")
    outputs.dir(file("${project.projectDir}/doc/developer/apidocs"))
}

task<Exec>("docsSiteDependencies") {
    workingDir(file("${projectDir}/doc"))

    commandLine(listOf("bundle", "install"))

    inputs.file("${project.projectDir}/doc/Gemfile")
    outputs.file("${project.projectDir}/doc/Gemfile.lock")
}

task<Exec>("buildDocsSite") {
    dependsOn(listOf(":generateOpenApiDocs", ":javadoc", ":docsSiteDependencies"))

    workingDir(file("${project.projectDir}/doc"))

    commandLine("bundle")
    args(listOf("exec", "jekyll", "build"))

    inputs.dir("${project.projectDir}/doc")
    outputs.dir("${project.projectDir}/doc/_site")
}

task<Exec>("serveDocsSite") {
    dependsOn(listOf(":generateOpenApiDocs", ":javadoc", ":docsSiteDependencies"))

    workingDir(file("${project.projectDir}/doc"))

    commandLine("bundle")
    args(listOf("exec", "jekyll", "serve"))

    inputs.dir("${project.projectDir}/doc")
    outputs.dir("${project.projectDir}/doc/_site")
}

task<Delete>("cleanDocsSite") {
    project.delete("${project.projectDir}/doc/_site")
}

tasks.clean {
    dependsOn(":cleanDocsSite")
}
