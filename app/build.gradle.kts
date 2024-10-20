plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}

android {
    namespace = "com.example.babybook"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.babybook"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.3.5"
        vectorDrawables.useSupportLibrary = true


        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)


}
dependencies {
    implementation ("androidx.work:work-runtime:2.7.1")
    implementation ("com.google.guava:guava:32.0.1-android")
    implementation ("androidx.viewpager2:viewpager2:1.0.0")
    implementation(libs.navigation.fragment)
    implementation(libs.recyclerview)
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore)
    implementation(libs.legacy.support.v4)
    implementation(platform("com.google.firebase:firebase-bom:33.1.1"))
    implementation ("com.google.firebase:firebase-storage:20.2.1") // Add this line for Firebase Storage
    implementation("com.google.firebase:firebase-auth")
    implementation ("androidx.appcompat:appcompat:1.7.0")
    implementation ("androidx.annotation:annotation:1.4.0")
    implementation ("androidx.viewpager2:viewpager2:1.0.0")
    implementation ("androidx.fragment:fragment:1.5.5")
    implementation ("com.google.android.material:material:1.9.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("com.google.android.gms:play-services-location:21.0.1")
    implementation ("com.google.android.gms:play-services-maps:18.1.0")
    implementation ("com.hbb20:ccp:2.7.3")
    implementation ("com.google.android.material:material:1.9.0")
    implementation ("com.google.android.gms:play-services-maps:18.1.0")

    implementation ("com.google.android.flexbox:flexbox:3.0.0")
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")

    
}