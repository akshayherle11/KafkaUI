import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    jvm("desktop",{
        withJava()
    })


    sourceSets {
        val desktopMain by getting
        
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            // https://mvnrepository.com/artifact/org.apache.kafka/kafka-clients
            implementation("org.apache.kafka:kafka-clients:3.8.0")
            // https://mvnrepository.com/artifact/com.seanproctor/data-table-material3-desktop
            //implementation("com.seanproctor:data-table-material3-desktop:0.5.1")
            implementation("com.seanproctor:data-table-material3-jvm:0.10.0")

// https://mvnrepository.com/artifact/org.json/json
            implementation("org.json:json:20240303")

            implementation(compose.material3)
            implementation(compose.materialIconsExtended)

        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            // https://mvnrepository.com/artifact/com.seanproctor/data-table-material-jvm

        }
    }
}


compose.desktop {
    application {
        mainClass = "com.kafkaui.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.kafkaui"
            packageVersion = "1.0.0"
        }
    }
}
