plugins {
    `java-library`
    `maven-publish`
}

repositories {
    mavenCentral()
}

version = "0.2.4"
group = "com.github.domlen2003"

dependencies {
    implementation("org.slf4j:slf4j-api:2.0.1")
    api("org.jetbrains:annotations:23.0.0")
}


java {
    sourceCompatibility = JavaVersion.toVersion("18")
    targetCompatibility = JavaVersion.toVersion("18")
}


tasks.compileJava {
    options.javaModuleVersion.set(provider { version as String })
    options.compilerArgs.add("--enable-preview")
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