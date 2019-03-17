import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile

plugins {
    id("kotlin2js") version "1.3.21"
    id("org.jetbrains.kotlin.frontend") version "0.0.45"
}

val ktorVersion: String by project

group = "it.lamba"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven(url="https://kotlin.bintray.com/ktor")
}

dependencies {
    implementation(kotlin("stdlib-js"))
    implementation(ktor("client-js", ktorVersion))
    implementation(ktor("client-auth-js", ktorVersion))
    implementation(ktor("client-websocket-js", ktorVersion))
}

tasks.named<KotlinJsCompile>("compileKotlin2Js"){
    kotlinOptions.moduleKind = "commonjs"
}

fun DependencyHandler.ktor(module: String, version: String? = null): Any =
    "io.ktor:ktor-$module${version?.let { ":$version" } ?: "+"}"