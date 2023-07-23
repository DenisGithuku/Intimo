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
