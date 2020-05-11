import com.yasintanriverdi.movielistsample.buildsrc.utils.getLocalProperty

plugins {
    id(Plugins.androidLibrary)
    kotlin(Plugins.kotlinAndroid)
    kotlin(Plugins.kotlinKapt)
}

android {
    buildTypes.forEach {
        it.buildConfigField(type = "String", name = "TMDB_API_BASE_URL", value = "test")
        it.buildConfigField(type = "String", name = "TMDB_API_KEY", value = getLocalProperty("TMDB_API_KEY", project))
    }
}

dependencies {

    implementation(Dependencies.Kotlin.stdlib)

    // Dagger
    implementation(Dependencies.Dagger.dagger)
    kapt(Dependencies.Dagger.compiler)

    // Retrofit-OkHttp
    implementation(Dependencies.Retrofit.retrofit)
    implementation(Dependencies.Retrofit.moshi)
    implementation(Dependencies.OkHttp.okhttp)
    implementation(Dependencies.OkHttp.loggingInterceptor)
}