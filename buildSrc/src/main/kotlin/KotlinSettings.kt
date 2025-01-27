/**
 * Kotlin settings
 */
object KotlinSettings {
  const val languageVersion: String = "1.9"
  const val apiVersion: String = "1.9"

  @Deprecated("The JVM Target version depends on the JDK. Do not use this constant!", level = DeprecationLevel.ERROR)
  const val jvmTarget: String = "1.8"

  /**
   * Contains the annotations we opted in.
   * Can be used to configure Kotlin extensions directly
   */
  val optInExperimentalAnnotations: List<String> = listOf(
    "kotlin.ExperimentalStdlibApi", //additional methods in the std lib (e.g. buildList{})
    "kotlin.time.ExperimentalTime", //support for duration and other time related classes
    "kotlin.contracts.ExperimentalContracts", //allows the definition of contracts (e.g. how often a lambda is called in a method)
    "kotlin.experimental.ExperimentalTypeInference", //type inference
    "kotlin.js.ExperimentalJsExport", //required for @JsExport
    "kotlin.ExperimentalMultiplatform", //Multi platform
    "kotlinx.serialization.ExperimentalSerializationApi", //Seems to work only with free compiler args

    "kotlinx.coroutines.ExperimentalCoroutinesApi", //Coroutines stuff
    "kotlinx.coroutines.FlowPreview", //Coroutines stuff
    "kotlin.ExperimentalUnsignedTypes", //Unsigned Types

    "kotlin.io.encoding.ExperimentalEncodingApi", //Base64 encoding lib
  )

  /**
   * Language features that are enabled
   */
  val languageFeatures: List<String> = listOf(
    //"InlineClasses", //enable inline classes - no longer necessary since now value classes are/should be used
  )

  /**
   * Compiler arguments can be found here:
   * https://github.com/JetBrains/kotlin/blob/master/compiler/cli/cli-common/src/org/jetbrains/kotlin/cli/common/arguments/K2JSCompilerArguments.kt
   */

  /**
   * The free compiler args that must be used to configure the Kotlin compiler tasks
   */
  val freeCompilerArgs: List<String> = buildList {
    addAll(optInExperimentalAnnotations.map { "-opt-in=$it" }) //Opt in to the experimental features we are using
    add("-progressive") //Advanced compiler checks that are not always backwards compatible within a major version of Kotlin
    add("-Xinline-classes") //Enable inline classes
    add("-Xcontext-receivers") //Enable context receivers (https://github.com/Kotlin/KEEP/blob/master/proposals/context-receivers.md#detailed-design)
    add("-XXLanguage:+EnumEntries") //Enable enum entries (https://youtrack.jetbrains.com/issue/KT-54621/Preview-of-Enum.entries-modern-and-performant-replacement-for-Enum.values)
  }

  /**
   * Additional compiler args - only used for JS
   */
  val additionalFreeCompilerArgsJS: List<String> = buildList {
    //add("-Xir-property-lazy-initialization") //enable lazy initialize for top level JS properties //ATTENTION: Does not work with kvision (https://github.com/rjaros/kvision/issues/231)
  }

  /**
   * Additional compiler args - only used for the JVM
   */
  val additionalFreeCompilerArgsJVM: List<String> = buildList {
    add("-Xjvm-default=all") //Enable generation of default methods in interfaces
    add("-Xjsr305=strict") //Strict null checks for kotlin projects
  }
}
