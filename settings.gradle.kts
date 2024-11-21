dependencyResolutionManagement {
    repositories {
        mavenCentral()
        mavenLocal()
    }
}

include(":extensions:control-plane")
include(":extensions:data-plane")

include(":launchers:base:connector")
include(":launchers:dev:connector-consumer")
include(":launchers:dev:connector-provider")

