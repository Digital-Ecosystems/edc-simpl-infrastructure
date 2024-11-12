plugins {
	`java-library`
}

val edcGroup: String by project
val edcVersion: String by project

dependencies {
	api("${edcGroup}:connector-core:${edcVersion}")
	api("${edcGroup}:runtime-metamodel:${edcVersion}")
}

java {
	withJavadocJar()
	withSourcesJar()
}
