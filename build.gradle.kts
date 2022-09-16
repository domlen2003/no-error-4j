plugins {
    `java-library`
    `maven-publish`
}

repositories {
    mavenCentral()
}

version = "0.1.3"
group = "cc.notabot.noerror4j"

dependencies {
    implementation("org.slf4j:slf4j-api:2.0.1")
    api("org.jetbrains:annotations:23.0.0")
}


java {
    sourceCompatibility = JavaVersion.toVersion("18")
    targetCompatibility = JavaVersion.toVersion("18")
}


tasks.compileJava {
    options.javaModuleVersion.set(provider { project.version as String })
    options.compilerArgs.add("--enable-preview")
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/domlen2003/no-error-4j")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
            }
        }
    }
    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
}