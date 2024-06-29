pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven ("https://jitpack.io")
        maven ("d:/Users/jollitycn/.m2/repository/")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven ("d:/Users/jollitycn/.m2/repository/")
    }
}

rootProject.name = "MobileKeybroad"
include(":app")
 