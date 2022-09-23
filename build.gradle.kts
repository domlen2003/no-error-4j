plugins {
    `java-library`
    `maven-publish`
}

repositories {
    mavenCentral()
}

version = "1.0.0-RC2"
group = "com.github.domlen2003"

dependencies {
    implementation("org.slf4j:slf4j-api:2.0.2")
    api("org.jetbrains:annotations:23.0.0")
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

tasks.withType(JavaCompile::class) {
    options.javaModuleVersion.set(provider { version as String })
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