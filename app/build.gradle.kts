import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

android {
    namespace = "com.example.mini_project_prm"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.mini_project_prm"
        minSdk = 34
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        android.buildFeatures.buildConfig = true

        val supabaseKey = localProperties["supabase.anonKey"] as String
        val supabaseUrl = localProperties["supabase.url"] as String
        val zalo_app_id = (localProperties["zalo_app_id"] as String).toInt()
        val zalo_mac_key = localProperties["zalo_mac_key"] as String
        val zalo_url_create_order = localProperties["zalo_url_create_order"] as String
        val google_client_id = localProperties["google_client_id"] as String

        buildConfigField("String", "SUPABASE_ANON_KEY", "\"$supabaseKey\"")
        buildConfigField("String", "SUPABASE_URL", "\"$supabaseUrl\"")
        buildConfigField("int", "ZALO_APP_ID", "$zalo_app_id")
        buildConfigField ("String", "ZALO_MAC_KEY", "\"${zalo_mac_key}\"")
        buildConfigField ("String", "ZALO_URL_CREATE_ORDER", "\"${zalo_url_create_order}\"")
        buildConfigField("String", "GOOGLE_CLIENT_ID", "\"${google_client_id}\"")
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

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(fileTree(mapOf(
        "dir" to "E:\\AndroidProject\\Mini_project_prm\\app\\libs",
        "include" to listOf("*.aar", "*.jar"),
        "exclude" to listOf("")
    )))
    // Room components
    val room_version = "2.6.1"
    implementation ("androidx.room:room-runtime:$room_version")
    implementation ("androidx.room:room-ktx:$room_version")
    kapt("androidx.room:room-compiler:$room_version")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.0")
    implementation("commons-codec:commons-codec:1.14")

    // JSON
    implementation("org.json:json:20231013")
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:21.2.0")
}