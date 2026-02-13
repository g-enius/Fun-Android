pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
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

rootProject.name = "fun-android"
include(":app")
include(":model")
include(":platform:ui-components")
include(":platform:navigation")
include(":services:network")
include(":services:favorites")
include(":services:search")
include(":features:home")
include(":features:search")
include(":features:items")
include(":features:profile")
include(":features:profile-detail")
include(":features:detail")
include(":features:settings")
