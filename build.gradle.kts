plugins {
    `java-library`
    `maven-publish`
    signing
}

repositories {
    mavenCentral()
}

version = "1.0.0-RC3"
group = "io.github.domlen2003"

dependencies {
    api("org.jetbrains:annotations:23.0.0")
    testImplementation("junit:junit:4.13.2")
}

java {
    toolchain {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

tasks.withType<JavaCompile> {
    options.javaModuleVersion.set(provider { version as String })
}

tasks.withType<Test> {
    useJUnit()
    maxHeapSize = "1G"
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
    options.quiet()
}


publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/domlen2003/no-error-4j")
            credentials {
                username = System.getenv("GITHUB_USERNAME")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
        maven {
            name = "OSSRH"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = System.getenv("SONATYPE_USERNAME")
                password = System.getenv("SONATYPE_PASSWORD")
            }
        }
    }
    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
            pom {
                name.set("No Error For Java")
                description.set("Datatypes to reduce Java's error prone Null, Exceptions etc.")
                url.set("https://github.com/domlen2003/no-error-4j")
                licenses {
                    license {
                        name.set("MIT")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                scm {
                    connection.set("https://github.com/domlen2003/no-error-4j.git")
                    url.set("https://github.com/domlen2003/no-error-4j")
                }
                developers {
                    developer {
                        id.set("domlen2003")
                        name.set("Dominik Lenz")
                        email.set("dominikalexanderlenz@gmail.com")
                    }
                }
            }
        }
    }
}

signing {
    useInMemoryPgpKeys(System.getenv("PGP_SECRET_KEY"),System.getenv("PGP_PASSPHRASE"))
    sign(publishing.publications.getByName("gpr"))
}
