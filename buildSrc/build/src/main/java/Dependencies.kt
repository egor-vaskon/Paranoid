//Project dependecies (including dependencies of all modules)
object Dependencies {
    //Kotlin std lib
    val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"

    //Core ktx
    val coreKtx = "androidx.core:core-ktx:${Versions.coreKtx}"

    //App compat
    val appCompat = "androidx.appcompat:appcompat:${Versions.appCompat}"

    //Constrait layout
    val constraintLayout =
        "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"

    //Lifecycle
    val lifecycleLivedata = "androidx.lifecycle:lifecycle-livedata:${Versions.lifecycle}"
    val lifecycleViewModel = "androidx.lifecycle:lifecycle-viewmodel:${Versions.lifecycle}"
    val lifecycleCommonJava8 = "androidx.lifecycle:lifecycle-common-java8:${Versions.lifecycle}"

    //Gson
    val gson = "com.google.code.gson:gson:${Versions.gson}"

    //Material components
    val materialComponents = "com.google.android.material:material:${Versions.materialComponents}"

    //Legacy support v4
    val legacySupportV4 = "androidx.legacy:legacy-support-v4:${Versions.legacySupportV4}"

    //Junit
    val junit = "junit:junit:${Versions.junit}"

    //External junit
    val extJunit = "androidx.test.ext:junit:${Versions.extJunit}"

    //Espresso
    val espressoCore = "androidx.test.espresso:espresso-core:${Versions.espresso}"

    //RxJava
    val rxJava = "io.reactivex.rxjava3:rxjava:${Versions.rxJava}"
    val rxAndroid = "io.reactivex.rxjava3:rxandroid:${Versions.rxJava}"

    //Room
    val roomRuntime = "androidx.room:room-runtime:${Versions.room}"
    val roomRxJava3 = "androidx.room:room-rxjava3:${Versions.room}"
    val roomCompiler = "androidx.room:room-compiler:${Versions.roomCompiler}"

    val lifecycleLibs = arrayListOf(lifecycleLivedata, lifecycleViewModel, lifecycleCommonJava8)
    val rxJavaLibs = arrayListOf(rxJava, rxAndroid)
    val roomLibs = arrayListOf(roomRuntime, roomRxJava3)
    val androidTestDependecies = arrayListOf(extJunit, espressoCore)

    val appDependencies = arrayListOf(
        kotlinStdLib,
        coreKtx,
        appCompat,
        constraintLayout,
        gson,
        materialComponents,
        legacySupportV4
    ).apply {
        addAll(rxJavaLibs)
        addAll(roomLibs)
    }

    val appTestDependecies = arrayListOf(junit)
    val appAndroidDependecies = arrayListOf<String>().apply {
        addAll(androidTestDependecies)
    }
}

fun DependencyHandler.kapt(list: List<String>) {
    list.forEach { dependency ->
        add("kapt", dependency)
    }
}

fun DependencyHandler.implementation(list: List<String>) {
    list.forEach { dependency ->
        add("implementation", dependency)
    }
}

fun DependencyHandler.annotationProcessor(list: List<String>) {
    list.forEach { dependency ->
        add("annotationProcessor", dependency)
    }
}

fun DependencyHandler.androidTestImplementation(list: List<String>) {
    list.forEach { dependency ->
        add("androidTestImplementation", dependency)
    }
}

fun DependencyHandler.testImplementation(list: List<String>) {
    list.forEach { dependency ->
        add("testImplementation", dependency)
    }
}