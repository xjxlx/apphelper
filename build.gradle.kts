plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.android.helper"
    compileSdk =
        libs.versions.compileSdks
            .get()
            .toInt()

    defaultConfig {
        minSdk =
            libs.versions.minSdk
                .get()
                .toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    lint {
        baseline = file("lint-baseline.xml")
    }

    configurations.all {
        resolutionStrategy {
            force(libs.recyclerview)
            force(libs.okhttp3)
            force(libs.rxjava2)
        }
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    // 系统级类库
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)

    // 私有的类库引用
    implementation(libs.eventbus) { isTransitive = false }
    implementation(libs.rxpermissions) { isTransitive = false } // rxjava3 版本

    implementation(libs.rxjava3) {
        // 禁止依赖的传递
        isTransitive = false
    }
    implementation(libs.android.pickerview) // 日历选择器
    implementation(libs.viewpager2)

    implementation(libs.rxjava2.rxandroid2) // 必要rxAndroid依赖，切线程时需要用到

    api(project(":refresh"))

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ui.test.junit4)

    // room数据库的依赖
//    implementation()("androidx.room:room-runtime:2.4.2") {// 禁止依赖的传递
//        transitive = false
//    }

    // 高德
//    implementation()("com.amap.api():location:5.6.1") {// 定位
//        transitive = false// 禁止依赖的传递
//    }
//    implementation()("com.amap.api():search:8.1.0") {  // 搜索
//        transitive = false// 禁止依赖的传递
//    }
//    implementation()("com.amap.api():3dmap:8.1.0") {  // 地图
//        transitive = false// 禁止依赖的传递
//    }
}
