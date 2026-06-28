import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml
import xyz.jpenilla.resourcefactory.paper.PaperPluginYaml

plugins {
    `java-library`
    `maven-publish`
    idea
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.21"
    id("xyz.jpenilla.run-paper") version "3.0.0-beta.1" // Adds runServer and runMojangMappedServer tasks for testing
    id("xyz.jpenilla.resource-factory-paper-convention") version "1.3.1" // Generates plugin.yml based on the Gradle config
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = false
    }
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
        url = uri("https://mvn.lib.co.nz/public")
    }

    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }

    maven {
        url = uri("https://jitpack.io")
    }

    maven {
        url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }

    maven {
        url = uri("https://maven.enginehub.org/repo/")
    }

    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
    }

    maven {
        url = uri("https://mvnrepository.com/artifact/com.plotsquared")
    }

    maven {
        url = uri("https://repo.glaremasters.me/repository/towny/")
    }

    maven {
        url = uri("https://repo.viaversion.com")
    }

    maven {
        url = uri("https://repo.codemc.io/repository/maven-releases/")
    }

    maven {
        url = uri("https://repo.codemc.io/repository/maven-snapshots/")
    }

    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    paperweight.paperDevBundle("26.1.2.build.+")
    api(libs.com.sk89q.worldedit.worldedit.bukkit) {
        exclude(group = libs.org.bukkit15.get().group, module = libs.org.bukkit15.get().name )
        exclude(group = libs.org.bukkit13.get().group, module = libs.org.bukkit13.get().name )
    }
    compileOnly(libs.io.papermc.paper.paper.api)
    compileOnly(libs.com.github.milkbowl.vaultapi) {
        exclude(group = libs.org.bukkit15.get().group, module = libs.org.bukkit15.get().name )
        exclude(group = libs.org.bukkit13.get().group, module = libs.org.bukkit13.get().name )
    }
    compileOnly(libs.me.clip.placeholderapi)
    compileOnly(libs.com.palmergames.bukkit.towny.towny)
    compileOnly(libs.com.plotsquared.plotsquared.core)
    compileOnly(libs.com.plotsquared.plotsquared.bukkit)
    compileOnly(libs.com.sk89q.worldguard.worldguard.bukkit) {
        exclude(group = libs.org.bukkit15.get().group, module = libs.org.bukkit15.get().name )
        exclude(group = libs.org.bukkit13.get().group, module = libs.org.bukkit13.get().name )
    }
    compileOnly(libs.com.viaversion.viaversion.api)
    compileOnly(libs.com.github.techfortress.griefprevention)
    compileOnly(libs.com.github.retrooper.packetevents.spigot)
}

group = "io.github.Weber588"
version = "2.0-SNAPSHOT"
description = "WbsUtils"
java {
    // Configure the java toolchain. This allows gradle to auto-provision JDK 21 on systems that only have JDK 11 installed for example.
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}
java.sourceCompatibility = JavaVersion.VERSION_25

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

// Configure plugin.yml generation
// - name, version, and description are inherited from the Gradle project.
paperPluginYaml {
    load = BukkitPluginYaml.PluginLoadOrder.STARTUP
    main = "wbs.utils.WbsUtils"
    bootstrapper = "wbs.utils.WbsUtilsBootstrap"
    authors.add("Weber588")
    apiVersion = "26.2.1"
    version = "${project.version}"
    dependencies {
        server.create("PlaceholderAPI", {
            load = PaperPluginYaml.Load.BEFORE
            required = false
        })
        server.create("GriefPrevention", {
            load = PaperPluginYaml.Load.BEFORE
            required = false
        })
        server.create("Towny", {
            load = PaperPluginYaml.Load.BEFORE
            required = false
        })
        server.create("PlotSquared", {
            load = PaperPluginYaml.Load.BEFORE
            required = false
        })
        server.create("WorldGuard", {
            load = PaperPluginYaml.Load.BEFORE
            required = false
        })
        server.create("ViaVersion", {
            load = PaperPluginYaml.Load.BEFORE
            required = false
        })
        server.create("Vault", {
            load = PaperPluginYaml.Load.BEFORE
            required = false
        })
        server.create("packetevents", {
            load = PaperPluginYaml.Load.BEFORE
            required = false
        })
    }
}
