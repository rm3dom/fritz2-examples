import com.google.devtools.ksp.gradle.KspTaskMetadata
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

fun KotlinDependencyHandler.npm(dependency: Provider<MinimalExternalModuleDependency>): Dependency =
    dependency.map { dep ->
        val name = if (dep.group == "npm") dep.name else "@${dep.group}/${dep.name}"
        npm(name, dep.version!!)
    }.get()

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://maven.pkg.jetbrains.space/public/p/krpc/maven")
    //maven("https://oss.sonatype.org/content/repositories/releases")
}


val fritz2Version: String = libs.versions.fritz2.get()


plugins {
    kotlin("multiplatform") version (libs.versions.kotlin)
    id("com.google.devtools.ksp").version(libs.versions.ksp)
}


kotlin {
    js {
        browser {
        }
        binaries.executable()
    }


    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.fritz2.core)
                api(libs.fritz2.headless)
            }
        }

        val jsMain by getting {
            dependencies {
                // tailwind
                api(npm(libs.tailwindcss.core))
                api(npm(libs.tailwindcss.typography))
                api(npm(libs.tailwindcss.forms))

                // webpack
                api(npm(libs.postcss.core))
                api(npm(libs.postcss.loader))
                api(npm(libs.autoprefixer))
                api(npm(libs.css.loader))
                api(npm(libs.style.loader))
                api(npm(libs.cssnano))
            }
        }
    }
}


// KSP support for Lens generation
dependencies.kspCommonMainMetadata("dev.fritz2", "lenses-annotation-processor", fritz2Version)
kotlin.sourceSets.commonMain { tasks.withType<KspTaskMetadata> { kotlin.srcDir(destinationDirectory) } }

