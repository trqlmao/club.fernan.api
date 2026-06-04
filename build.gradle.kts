plugins {
    `java-library`
    `maven-publish`
    id("com.diffplug.spotless") version "6.25.0"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenCentral()
}

dependencies {
    api("com.google.code.gson:gson:2.11.0")

    compileOnly("org.projectlombok:lombok:1.18.46")
    annotationProcessor("org.projectlombok:lombok:1.18.46")

    testImplementation(platform("org.junit:junit-bom:5.10.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testCompileOnly("org.projectlombok:lombok:1.18.46")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.46")
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(17)
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf("-Xlint:all", "-Xlint:-processing"))
}

tasks.withType<Javadoc>().configureEach {
    (options as StandardJavadocDocletOptions).apply {
        addStringOption("Xdoclint:none", "-quiet")
        encoding = "UTF-8"
        charSet("UTF-8")
        source = "17"
    }
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

spotless {
    java {
        target("src/**/*.java", "examples/**/*.java")
        palantirJavaFormat("2.50.0")
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint("1.3.1")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            pom {
                name.set("club.fernan.api")
                description.set("Drop-in Java client for the fernan.club REST API")
                url.set("https://github.com/trqlmao/club.fernan.api")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("trqlmao")
                        name.set("trq")
                    }
                }
                scm {
                    url.set("https://github.com/trqlmao/club.fernan.api")
                    connection.set("scm:git:https://github.com/trqlmao/club.fernan.api.git")
                }
            }
        }
    }
}
