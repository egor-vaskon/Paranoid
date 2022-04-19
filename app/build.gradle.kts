
plugins {
    id("com.google.devtools.ksp") version "1.6.10-1.0.2"
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdk = AppConfig.compileSdk
    buildToolsVersion = AppConfig.buildToolsVersion

    defaultConfig {
        applicationId = "com.egorvaskon.paranoid"
        minSdk = AppConfig.minSdk
        targetSdk = AppConfig.targetSdk
        versionCode = AppConfig.versionCode
        versionName = AppConfig.versionName

        testInstrumentationRunner = AppConfig.androidTestInstrumentation
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    viewBinding {
        android.buildFeatures.viewBinding = false
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(Dependencies.appDependencies)
    //annotationProcessor(Dependencies.appAnnotationProcessors)
    ksp(Dependencies.appAnnotationProcessors)
    testImplementation(Dependencies.appTestDependencies)
    androidTestImplementation(Dependencies.appAndroidTestDependencies)
    coreLibraryDesugaring(Dependencies.desugaring)
}