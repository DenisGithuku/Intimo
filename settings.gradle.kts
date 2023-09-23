pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "Intimo"
include(":app")
include(":feature:onboarding")
include(":core:designsystem")
include(":core:model")
include(":core:datastore")
include(":core:data")
include(":feature:summary")
include(":core:local")
include(":core:database")
include(":feature:settings")
include(":core:ui")
include(":feature:habit")
include(":feature:usage_stats")
include(":core:util")
