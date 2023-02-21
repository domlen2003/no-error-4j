plugins {
    `java-library`
    `maven-publish`
}

repositories {
    mavenCentral()
}

version = "1.0.0-RC3"
group = "com.github.domlen2003"

dependencies {
    api("org.jetbrains:annotations:23.0.0")
    testImplementation("junit:junit:4.13.2")
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

tasks.withType<JavaCompile> {
    options.javaModuleVersion.set(provider { version as String })
}

tasks.withType<Test> {
    useJUnit()
    maxHeapSize = "1G"
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/domlen2003/no-error-4j")
            credentials {
                username = System.getenv("USERNAME")
                password = System.getenv("TOKEN")
            }
        }
    }
    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
}