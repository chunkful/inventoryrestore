import org.jooq.meta.jaxb.JSONConverterImplementation
import xyz.jpenilla.resourcefactory.paper.PaperPluginYaml

plugins {
    id("java-library")
    alias(libs.plugins.shadow)
    alias(libs.plugins.run.paper)
    alias(libs.plugins.resource.factory.paper)
    alias(libs.plugins.gremlin.gradle)
    alias(libs.plugins.jooqCodegen)
    alias(libs.plugins.minotaur)
    alias(libs.plugins.hangar)
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

    runtimeDownload(libs.adventure.nbt)
    runtimeDownload(libs.cloud)
    runtimeDownload(libs.cloud.annotations)
    runtimeDownload(libs.cloud.paper)
    runtimeDownload(libs.cloud.minecraft.extras)
    runtimeDownload(libs.configurate.hocon)
    runtimeDownload(libs.nitrite)
    runtimeDownload(libs.nitrite.mvstore)
    runtimeDownload(libs.mongodb)
    runtimeDownload(libs.jooq)
    runtimeDownload(libs.jooq.jackson3Extensions)
    runtimeDownload(libs.flyway.core)
    runtimeDownload(libs.flyway.postgresql)
    runtimeDownload(libs.flyway.mysql)
    runtimeDownload(libs.hikaricp)
    runtimeDownload(libs.postgresql)
    runtimeDownload(libs.h2)
    runtimeDownload(libs.mariadb)

    jooqCodegen(libs.jooq.metaExtensions)
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

jooq {
    configuration {
        generator {
            database {
                name = "org.jooq.meta.extensions.ddl.DDLDatabase"
                properties {
                    property {
                        key = "scripts"
                        value = "src/main/resources/sql-migrations/h2/*.sql"
                    }
                    property {
                        key = "sort"
                        value = "flyway"
                    }
                    property {
                        key = "defaultNameCase"
                        value = "lower"
                    }
                    property {
                        key = "unqualifiedSchema"
                        value = "public"
                    }
                }

                forcedTypes {
                    forcedType {
                        userType = "net.chunkful.inventoryrestore.model.Snapshot"
                        isJsonConverter = true
                        jsonConverterImplementation = JSONConverterImplementation.JACKSON_3
                        includeExpression = ".*\\.DATA"
                    }
                    forcedType {
                        userType = "java.time.Instant"
                        converter = "net.chunkful.inventoryrestore.storage.sql.InstantConverter"
                        includeExpression = ".*\\.CREATED_AT"
                    }
                    forcedType {
                        userType = "net.chunkful.inventoryrestore.model.Snapshot.Cause"
                        includeExpression = ".*\\.CAUSE"
                    }
                }
            }

            generate {
                isFluentSetters = true
                isJavaTimeTypes = true
            }

            target {
                packageName = "net.chunkful.inventoryrestore.storage.sql.jooq"
            }
        }
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
        dependsOn(jooqCodegen)
    }
}

val supportedVersions = listOf("1.21.11", "26.1.2", "26.2", "26.3")

modrinth {
    token = System.getenv("MODRINTH_TOKEN")
    debugMode = System.getenv("CI") != "true"
    projectId = "chunkful-inventoryrestore"
    versionType = "release"
    uploadFile.set(tasks.shadowJar)
    gameVersions = supportedVersions
    loaders = listOf("paper", "folia")
    dependencies {
        required.project("nbtapi")
    }
    syncBodyFrom = file("README.md").readText()

    val tagMessage: String? = System.getenv("CI_COMMIT_TAG_MESSAGE")
    tagMessage?.let {
        changelog.set(it)
    }
}

tasks.modrinth {
    dependsOn(tasks.modrinthSyncBody)
}

hangarPublish {
    publications.register("plugin") {
        version = project.version.toString()
        id = "chunkful-inventoryrestore"
        channel = "Release"

        apiKey = System.getenv("HANGAR_TOKEN")

        platforms {
            paper {
                jar = tasks.shadowJar.get().archiveFile
                platformVersions = supportedVersions
                dependencies {
                    hangar("NBTAPI")
                }
            }
        }

        pages {
            resourcePage(provider {
                file("README.md").readText()
            })
        }

        val tagMessage: String? = System.getenv("CI_COMMIT_TAG_MESSAGE")
        tagMessage?.let {
            changelog.set(it)
        }
    }
}

tasks.named("publishPluginPublicationToHangar") {
    dependsOn(tasks.named("syncPluginPublicationMainResourcePagePageToHangar"))
}

tasks.register("publish") {
    dependsOn(tasks.build, tasks.modrinth, tasks.publishAllPublicationsToHangar)
}
