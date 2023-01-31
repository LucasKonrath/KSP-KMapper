plugins {
    id("com.google.devtools.ksp")
    kotlin("jvm")
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":processor"))
    implementation(project(mapOf("path" to ":processor")))
    ksp(project(":processor"))
}

sourceSets.main {
    java.srcDirs("src/main/kotlin")
}

ksp {
    arg("enabled", "true")
}
