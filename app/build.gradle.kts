import com.android.build.api.dsl.Packaging

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.personx.cryptx"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.personx.cryptx"
        minSdk = 24
        targetSdk = 35
        versionCode = 2
        versionName = "1.2.3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Exclude non-deterministic files

    // Merge all resources deterministically
    packaging {
        resources {
            // Exclude files that change between builds
            excludes += setOf(
                "/META-INF/*.version",
                "**/version-control-info.textproto",
                "**/build-data.properties"
            )
            // Ensure consistent merging of resources
            merges += setOf("**/strings.xml")
            pickFirsts += setOf("**/version.conf")
        }
    }

    buildTypes {
        release {
            isProfileable = false  // Disables baseline.prof generation
            ndk {
                debugSymbolLevel = "none"  // Or "FULL" if needed
            }

            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    dependenciesInfo {
        // Disables dependency metadata when building APKs.
        includeInApk = false
        // Disables dependency metadata when building Android App Bundles.
        includeInBundle = false
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Room components
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.material3.window.size.class1.android)
    ksp(libs.androidx.room.compiler) // Use kasp if you're using Kotlin

// Kotlin Extensions and Coroutines support for Room
    implementation(libs.androidx.room.ktx)
    implementation(libs.android.database.sqlcipher)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(project(":cryptography"))
    implementation(libs.coil.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}