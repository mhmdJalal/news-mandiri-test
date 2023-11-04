import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("io.gitlab.arturbosch.detekt") version("1.23.3")
    kotlin("plugin.serialization") version "1.7.10"
}

val apikeyPropertiesFile by lazy { rootProject.file("apikey.properties") }
val apikeyProperties by lazy { Properties() }
apikeyProperties.load(FileInputStream(apikeyPropertiesFile))

android {
    namespace = AppDetail.applicationId
    compileSdk = AppDetail.compileSdkVersion

    defaultConfig {
        applicationId = AppDetail.applicationId
        minSdk = AppDetail.minSdkVersion
        targetSdk = AppDetail.targetSdkVersion
        versionCode = AppDetail.versionCode
        versionName = AppDetail.versionName

        buildConfigField("String", "BASE_URL", "\"newsapi.org\"")

        buildConfigField("String", "NEWS_API_KEY", "\"${apikeyProperties.getProperty("NEWS_API_KEY")}\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        targetCompatibility(JavaVersion.VERSION_11)
        sourceCompatibility(JavaVersion.VERSION_11)
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(Deps.kotlin)
    implementation(Deps.kotlinReflect)
    implementation(Deps.appCompat)
    implementation(Deps.materialDesign)
    implementation(Deps.constraintLayout)
    implementation(Deps.legacySupport)

    implementation(Deps.splitties)
    implementation(Deps.prettyTime)

    // GLIDE IMAGE COMPILER
    implementation(Deps.glide)
    kapt(Kapt.glide)
    implementation(Deps.glideOkhttp) {
        isTransitive = true
    }

    // KTOR
    implementation(Deps.Ktor.core)
    implementation(Deps.Ktor.cio)
    implementation(Deps.Ktor.android)
    implementation(Deps.Ktor.serialization)
    implementation(Deps.Ktor.logging)
    implementation(Deps.Ktor.contentNegotiation)

    // KOIN
    implementation(Deps.Koin.core)
    implementation(Deps.Koin.android)

    // LIFECYCLE
    implementation(Deps.lifecycleLiveData)
    implementation(Deps.lifecycleViewModel)
    implementation(Deps.lifecycleCommon)
    testImplementation(Deps.archCoreTesting)

    // KOTLIN COROUTINES
    implementation(Deps.coroutineCore)
    implementation(Deps.coroutineAndroid)

    // TESTING
    testImplementation(Deps.Testing.jUnit)
    androidTestImplementation(Deps.Testing.extJUnit)
    androidTestImplementation(Deps.Testing.espresso)
}