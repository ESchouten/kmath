plugins {
    id("ru.mipt.npm.gradle.mpp")
//    id("ru.mipt.npm.gradle.native")
}

kscience {
    useAtomic()
}

kotlin.sourceSets {
    all {
        languageSettings.optIn("space.kscience.kmath.misc.UnstableKMathAPI")
    }

    commonMain {
        dependencies {
            api(project(":kmath-coroutines"))
        }
    }
}

readme {
    maturity = ru.mipt.npm.gradle.Maturity.EXPERIMENTAL
}

// Testing multi-receiver!
tasks.withType<org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile> {
    enabled = false
}
