import com.github.javaparser.printer.concretesyntaxmodel.CsmElement.token

plugins {
  id("java")
  id("org.jetbrains.kotlin.jvm") version "2.3.0"
  id("org.jetbrains.intellij.platform") version "2.11.0"
}

group = "org.kratosgado"
version = "1.5"

repositories {
  mavenCentral()
  intellijPlatform {
    defaultRepositories()
  }
}

dependencies {
    intellijPlatform {
        // Use the unified helper for 2025.3+ or specific version like "2026.1.1"
        intellijIdea("2026.1.1")
        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)
        // Add plugin dependencies if needed, e.g., bundledPlugin("com.intellij.java")
    }
}

intellijPlatform {
    pluginConfiguration {
        // Automatically patches plugin.xml
        id = "org.kratosgado.tabout"
        name = "Tabout"
        ideaVersion {
            sinceBuild = "252"
        }
    }

    signing {
        // Automatically uses environment variables CERTIFICATE_CHAIN, etc.
        certificateChain = System.getenv("CERTIFICATE_CHAIN")
        privateKey = System.getenv("PRIVATE_KEY")
        password = System.getenv("PRIVATE_KEY_PASSWORD")
    }

    publishing {
        token = System.getenv("PUBLISH_TOKEN")
    }
}

tasks {
  // Set the JVM compatibility versions
  withType<JavaCompile> {
    sourceCompatibility = "21"
    targetCompatibility = "21"
  }
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
      optIn.add("kotlin.RequiresOptIn")
    }
  }
}