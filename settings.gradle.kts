dependencyResolutionManagement {
    repositories {
        mavenCentral()
        mavenLocal()
    }
}

include(":extension")

include(":launchers:base:connector")
include(":launchers:dev:connector-consumer")
include(":launchers:dev:connector-provider")

