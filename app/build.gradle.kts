import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdkVersion(Sdk.COMPILE_SDK_VERSION)

    val versionFile = file("version.properties")
    var versionCodeFromProperties: String? = null

    if (versionFile.canRead()) {
        val properties = Properties()

        properties.load(FileInputStream(versionFile))

        versionCodeFromProperties = properties["VERSION_CODE"] as String?
    }

    defaultConfig {
        minSdkVersion(Sdk.MIN_SDK_VERSION)
        targetSdkVersion(Sdk.TARGET_SDK_VERSION)

        applicationId = AppCoordinates.APP_ID
        versionCode = versionCodeFromProperties?.let { Integer.valueOf(it) } ?: System.getenv("BUILD_NUMBER")?.let { Integer.valueOf(it) } ?: 0
        versionName = "${AppCoordinates.APP_VERSION_CODE_MAJOR}.${AppCoordinates.APP_VERSION_CODE_MINOR}.$versionCode"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
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

    lintOptions {
        isWarningsAsErrors = true
        isAbortOnError = true
    }

    // Use this block to configure different flavors
//    flavorDimensions("version")
//    productFlavors {
//        create("full") {
//            dimension = "version"
//            applicationIdSuffix = ".full"
//        }
//        create("demo") {
//            dimension = "version"
//            applicationIdSuffix = ".demo"
//        }
//    }
}

dependencies {
    implementation(kotlin("stdlib-jdk7"))

    implementation(project(":library-android"))
    implementation(project(":library-kotlin"))

    implementation(SupportLibs.ANDROIDX_APPCOMPAT)
    implementation(SupportLibs.ANDROIDX_CONSTRAINT_LAYOUT)
    implementation(SupportLibs.ANDROIDX_CORE_KTX)

    testImplementation(TestingLib.JUNIT)

    androidTestImplementation(AndroidTestingLib.ANDROIDX_TEST_EXT_JUNIT)
    androidTestImplementation(AndroidTestingLib.ANDROIDX_TEST_EXT_JUNIT_KTX)
    androidTestImplementation(AndroidTestingLib.ANDROIDX_TEST_RULES)
    androidTestImplementation(AndroidTestingLib.ESPRESSO_CORE)
}

tasks.whenTaskAdded{
    if(this.name == "assembleRelease"){
        doFirst {
            val versionFile = file("version.properties")
            if (versionFile.canRead()) {
                val properties = Properties()

                properties.load(FileInputStream(versionFile))

                val bumpedCode = properties["VERSION_CODE"] as Int + 1

                println("Bumping version code to $bumpedCode")

                properties["VERSION_CODE"] = bumpedCode
                properties.store(versionFile.writer(), null)
            }else {
                throw GradleException("Could not read version.properties!")
            }
        }
        doLast {
            println("Successfully assembled version: ${android.defaultConfig.versionName}")
        }
    }
}