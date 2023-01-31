val kspVersion: String by project

plugins {
    id("com.google.devtools.ksp")
    kotlin("jvm")
}

group = "org.kmapper"
version = "1.0"

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.21")
    implementation("com.squareup:kotlinpoet:1.9.0")
    implementation("com.google.devtools.ksp:symbol-processing-api:$kspVersion")
    implementation("com.google.auto.service:auto-service-annotations:1.0")
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-reflect
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.21")

    ksp("dev.zacsweers.autoservice:auto-service-ksp:0.5.2")
}

sourceSets.main {
    java.srcDirs("src/main/kotlin", "../example/src/main/kotlin", "../example/build/generated/ksp/main/kotlin")
}