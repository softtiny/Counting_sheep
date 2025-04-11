import java.util.Properties
import org.json.JSONObject
import java.io.File
import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestResult
import groovy.lang.Closure

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}
// Read version information from update.json
val updateJsonFile = File("${project.rootDir}/update.json")
val versionInfo = if (updateJsonFile.exists()) {
    val jsonContent = updateJsonFile.readText()
    val jsonObject = JSONObject(jsonContent)
    mapOf(
        "versionCode" to (jsonObject.optInt("latestVersionCode") ?: 1),
        "versionName" to (jsonObject.optString("latestVersion") ?: "1.0")
    )
} else {
    mapOf("versionCode" to 1, "versionName" to "1.0")
}

android {
    namespace = "proxy.kunkka.tts"
    compileSdk = 34

    defaultConfig {
        applicationId = "proxy.kunkka.tts"
        minSdk = 26
        targetSdk = 34
        versionCode = versionInfo["versionCode"] as Int
        versionName = versionInfo["versionName"] as String

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        multiDexEnabled = true
    }
    signingConfigs {
        create("release") {
            enableV1Signing = true
            enableV2Signing = true
            storeFile = file("./keystore.jks")
           
            val keyPropsFile = file("../key.properties")
            val keyProps  = Properties()
            keyProps.load(keyPropsFile.inputStream())

            storePassword = keyProps.getProperty("storePassword")
            keyAlias = keyProps.getProperty("keyAlias")
            keyPassword = keyProps.getProperty("keyPassword")
        }
    }
    buildTypes {
        release {
             signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

}

tasks.withType<Test> {
    testLogging {
        events("passed", "skipped", "failed", "standardOut", "standardError")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showStandardStreams = true
        afterSuite(object : Closure<Any>(this, this)  { 
            fun doCall(desc: TestDescriptor, result: TestResult) {
                if (desc.parent == null) { // will match the root suite
                    println("Test ${desc.name} - ${result.resultType}")
                }
            }
        })
    }
}

dependencies {
    // implementation 'com.github.javiersantos:AppUpdater:2.7'
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation("androidx.compose.runtime:runtime:1.6.0") // For LaunchedEffect and state management

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation("com.github.softtiny03:AppUpdater:2.7.5") {
        exclude(group = "com.android.support")
        exclude(module = "appcompat-v7")
        exclude(module = "support-v4")
    }

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}