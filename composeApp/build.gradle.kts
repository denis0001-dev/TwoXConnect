import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    kotlin("plugin.serialization") version "1.9.22"
}

kotlin {
    androidTarget()

    compilerOptions {
        freeCompilerArgs.addAll("-Xexpect-actual-classes")
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    jvm("desktop")
    
    sourceSets {
        val desktopMain by getting
        
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.core.splashscreen)
            implementation(libs.androidx.adaptive.android)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.constraintlayout.compose.multiplatform)
            implementation(libs.navigation.compose)
            implementation(compose.materialIconsExtended)

            // Serialization
            implementation(libs.kotlinx.serialization.json)

            implementation(libs.kotlinx.io.core)
            
            implementation(project(":utils"))
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
        iosMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.3.0")
            implementation("org.jetbrains.kotlinx:kotlinx-io-core-posix:0.3.0")
            implementation("org.jetbrains.kotlinx:kotlinx-io-bytestring:0.3.0")
            implementation("org.jetbrains.kotlinx:kotlinx-io-buffer:0.3.0")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-native:1.7.3")
        }
    }
}

android {
    namespace = "ru.twoxconnect"
    compileSdk = 35

    defaultConfig {
        applicationId = "ru.twoxconnect"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "ru.twoxconnect.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "ru.twoxconnect"
            packageVersion = "1.0.0"

            linux {
                iconFile.set(project.file("icons/icon.png"))
            }

            windows {
                iconFile.set(project.file("icons/icon.ico"))
            }
        }
    }
}

compose.resources {
    publicResClass = false
    packageOfResClass = "ru.twoxconnect"
    generateResClass = auto
}

tasks.create("generateResourceAccessors") {
    dependsOn(
        *(
            tasks.filter {
                it.name.startsWith("generateResourceAccessors") &&
                !it.name.matches("^(:${project.name})?generateResourceAccessors$".toRegex())
            }.toTypedArray()
        )
    )
}