import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform") version "1.4.31"
}

group = "com.github.reline"
version = "0.0.1-SNAPSHOT"
description = "OBD Kotlin-Multiplatform API"

repositories {
    google()
    mavenCentral()
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += "-Xjvm-default=all"
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    sourceCompatibility = JavaVersion.VERSION_1_8.toString()
    targetCompatibility = JavaVersion.VERSION_1_8.toString()
}

kotlin {
    jvm {
        withJava()
    }
    // todo: configure for iOS

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.squareup.okio:okio:2.10.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-testng"))
                implementation("org.testng:testng:6.8.13")
                implementation("org.easymock:easymock:3.3")
                implementation("org.powermock:powermock-module-testng:1.6.0")
                implementation("org.powermock:powermock-api-easymock:1.6.0")
            }
        }
    }
}
