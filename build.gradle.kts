import xyz.jpenilla.resourcefactory.paper.PaperPluginYaml

plugins {
    id("java-library")
    alias(libs.plugins.shadow)
    alias(libs.plugins.run.paper)
    alias(libs.plugins.resource.factory.paper)
    alias(libs.plugins.gremlin.gradle)
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-public/")
}

configurations.compileOnly {
    extendsFrom(configurations.runtimeDownload.get())
}

dependencies {
    compileOnly(libs.paper.api)
    compileOnly(libs.nbtapi)

    runtimeDownload(libs.cloud)
    runtimeDownload(libs.cloud.annotations)
    runtimeDownload(libs.cloud.paper)
    runtimeDownload(libs.cloud.minecraft.extras)
    runtimeDownload(libs.configurate.hocon)
    runtimeDownload(libs.nitrite)
    runtimeDownload(libs.nitrite.mvstore)
    runtimeDownload(libs.mongodb)
    runtimeDownload(libs.adventure.nbt)
}

paperPluginYaml {
    main = "net.chunkful.inventoryrestore.InventoryrestorePlugin"
    loader = "xyz.jpenilla.gremlin.runtime.platformsupport.DefaultsPaperPluginLoader"
    apiVersion = "1.21.11"
    foliaSupported = true

    authors.addAll("MCMDEV")

    dependencies {
        server {
            register("NBTAPI") {
                required = false
                load = PaperPluginYaml.Load.BEFORE
            }
        }
    }
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

runPaper {
    folia {
        registerTask()
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    runServer {
        minecraftVersion(libs.versions.minecraft.get())
        jvmArgs("-Xms2G", "-Xmx2G", "-Dcom.mojang.eula.agree=true")
    }

    withType<JavaCompile> {
        // Preserve parameter names in the bytecode
        options.compilerArgs.add("-parameters")
    }
}
