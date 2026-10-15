import java.util.Properties
plugins {
    alias(libs.plugins.android.application)
}
val mapkitApiKey: String = run {
    val properties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { properties.load(it) }
    }
    properties.getProperty("MAPKIT_API_KEY", "")

}


android {
    namespace = "com.socialnavigator"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.socialnavigator"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "MAPKIT_API_KEY", "\"$mapkitApiKey\"")
        manifestPlaceholders["MAPKIT_API_KEY"] = mapkitApiKey
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("com.yandex.android:maps.mobile:4.33.0-lite")
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
    // Retrofit + Gson
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    // OkHttp Logging (для отладки — видеть запросы в Logcat)
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

}