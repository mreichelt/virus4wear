plugins {
    kotlin("multiplatform")
}

version = "unspecified"

repositories {
    mavenCentral()
}

kotlin {
    jvm()
    iosX64("iOS")

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
    }
}
