plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

version = "0.1"
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