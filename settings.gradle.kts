plugins {
    id("de.fayard.refreshVersions") version "0.11.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "Currency Watch"
include(":app")
