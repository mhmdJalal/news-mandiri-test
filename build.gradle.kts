// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.7.10")
    }
}

plugins {
    id("com.android.application") version("7.3.0") apply(false)
    id("com.android.library") version("7.3.0") apply(false)
    id("org.jetbrains.kotlin.android") version("1.7.10") apply(false)
    kotlin("plugin.serialization") version "1.7.10"
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}