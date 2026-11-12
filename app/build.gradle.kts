plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlin.compose)
	alias(libs.plugins.ksp)
	alias(libs.plugins.kotlin.serialization)
	alias(libs.plugins.room)
}

android {
	namespace = "vadimerenkov.aucards"
	compileSdk = 37

	defaultConfig {
		applicationId = "icu.xyblue135.needcards"
		minSdk = 26
		targetSdk = 37
		versionCode = 30
		versionName = "2.9.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}

	androidResources {
		generateLocaleConfig = true
	}

	signingConfigs {
		create("selfSigned") {
			storeFile = File(System.getProperty("user.home"), ".android/debug.keystore")
			storePassword = "android"
			keyAlias = "androiddebugkey"
			keyPassword = "android"
		}
	}

	buildTypes {
		debug {
			isMinifyEnabled = false
		}
		release {
			signingConfig = signingConfigs.getByName("selfSigned")
			isMinifyEnabled = true
			isShrinkResources = true
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)
		}
	}
	buildFeatures {
		buildConfig = true
		compose = true
	}

	bundle {
		language {
			enableSplit = false
		}
	}
	dependenciesInfo {
		// Disables dependency metadata when building APKs (for IzzyOnDroid/F-Droid)
		includeInApk = false
		// Disables dependency metadata when building Android App Bundles (for Google Play)
		includeInBundle = false
	}

	testOptions {
		unitTests {
			isIncludeAndroidResources = true
			isReturnDefaultValues = true
		}
	}
}

room {
	schemaDirectory("$projectDir/schemas")
}

dependencies {
	implementation(libs.androidx.lifecycle.viewmodel.compose)
	implementation(libs.androidx.datastore)
	implementation(libs.androidx.datastore.preferences)
	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.lifecycle.runtime.ktx)
	implementation(libs.androidx.activity.compose)
	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.androidx.ui)
	implementation(libs.androidx.ui.graphics)
	implementation(libs.androidx.ui.tooling.preview)
	implementation(libs.androidx.material3)
	implementation(libs.androidx.appcompat)
	implementation(libs.androidx.fragment)
	implementation(libs.activity)
	implementation(libs.androidx.activity.ktx)
	implementation(libs.androidx.lifecycle.viewmodel.savedstate)
	implementation(libs.androidx.media3.exoplayer)
	implementation(libs.compose.material.icons)

	//Glance
	implementation(libs.glance.appwidget)
	implementation(libs.glance.material3)

	//Room
	implementation(libs.androidx.room.runtime)
	ksp(libs.androidx.room.compiler)

	//Navigation
	implementation(libs.androidx.navigation.compose)
	implementation(libs.kotlinx.serialization.json)
	implementation(libs.kotlinx.serialization.core)
	implementation(libs.androidx.adaptive)

	//Coil
	implementation(libs.coil)

	//Colorpicker Compose by skydoves
	implementation(libs.compose.colorpicker)

	//Reorderable by Calvin-LL
	implementation(libs.reorderable)

	//Koin
	implementation(libs.bundles.koin)

	testImplementation(libs.junit)
	testImplementation("org.robolectric:robolectric:4.17")
	testImplementation("androidx.test:core-ktx:1.6.1")
	testImplementation(libs.androidx.truth)
	testImplementation(libs.turbine)
	testImplementation(libs.kotlinx.coroutines.test)
	testImplementation(libs.androidx.lifecycle.viewmodel.testing)
	testImplementation(libs.androidx.ui.test.junit4)
	testImplementation(libs.androidx.navigation.testing)

	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.androidx.espresso.core)
	androidTestImplementation(platform(libs.androidx.compose.bom))
	androidTestImplementation(libs.androidx.ui.test.junit4)
	androidTestImplementation(libs.androidx.truth)
	androidTestImplementation(libs.turbine)
	androidTestImplementation(libs.kotlinx.coroutines.test)
	androidTestImplementation(libs.androidx.lifecycle.viewmodel.testing)

	debugImplementation(libs.androidx.ui.tooling)
	debugImplementation(libs.androidx.ui.test.manifest)
}