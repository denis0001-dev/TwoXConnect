@file:Suppress("UnusedReceiverParameter", "unused", "NOTHING_TO_INLINE", "KotlinRedundantDiagnosticSuppress")
package ru.denis0001dev.utils

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.LayoutDirection
import androidx.constraintlayout.compose.ConstrainScope
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayoutBaseScope
import androidx.constraintlayout.compose.HorizontalAnchorable
import androidx.constraintlayout.compose.VerticalAnchorable
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.FontResource

@Composable
fun Typography(fontRes: FontResource) = Typography().let {
    val font = FontFamily(Font(fontRes))
    it.copy(
        displayLarge = it.displayLarge.copy(fontFamily = font),
        displayMedium = it.displayMedium.copy(fontFamily = font),
        displaySmall = it.displaySmall.copy(fontFamily = font),
        headlineLarge = it.headlineLarge.copy(fontFamily = font),
        headlineMedium = it.headlineMedium.copy(fontFamily = font),
        headlineSmall = it.headlineSmall.copy(fontFamily = font),
        titleLarge = it.titleLarge.copy(fontFamily = font),
        titleMedium = it.titleMedium.copy(fontFamily = font),
        titleSmall = it.titleSmall.copy(fontFamily = font),
        bodyLarge = it.bodyLarge.copy(fontFamily = font),
        bodyMedium = it.bodyMedium.copy(fontFamily = font),
        bodySmall = it.bodySmall.copy(fontFamily = font),
        labelLarge = it.labelLarge.copy(fontFamily = font),
        labelMedium = it.labelMedium.copy(fontFamily = font),
        labelSmall = it.labelSmall.copy(fontFamily = font)
    )
}

val ConstrainScope.left get() = absoluteLeft
val ConstrainScope.right get() = absoluteRight

val ConstrainedLayoutReference.left get() = absoluteLeft
val ConstrainedLayoutReference.right get() = absoluteRight

inline infix fun HorizontalAnchorable.link(
    anchor: ConstraintLayoutBaseScope.HorizontalAnchor
) = linkTo(anchor)

inline infix fun VerticalAnchorable.link(
    anchor: ConstraintLayoutBaseScope.VerticalAnchor
) = linkTo(anchor)


expect fun Modifier.clearFocusOnKeyboardDismiss(): Modifier

operator fun Modifier.plus(other: Modifier) = then(other)

var ClipboardManager.text
    get() = getText()
    set(value) = setText(value!!)

@Composable
expect fun ToggleNavScrimEffect(enabled: Boolean = false)

interface SupportClipboardManager {
    suspend fun setText(string: String)
    suspend fun getText(): String?
    suspend fun hasText(): Boolean = getText()?.isNotEmpty() == true

    fun setTextListener(listener: (String) -> Unit)
}

val LocalSupportClipboardManager: ProvidableCompositionLocal<SupportClipboardManager> = compositionLocalOf {
    throw IllegalStateException("This CompositionLocal hasn't been provided.")
}

val supportClipboardManagerImpl
    @Composable get() = LocalClipboardManager().toSupport()

inline fun ClipboardManager.toSupport() = object : SupportClipboardManager {
    private var listener: ((String) -> Unit)? = null

    override suspend fun setText(string: String) {
        this@toSupport.setText(string.toAnnotatedString())
        listener?.invoke(string)
    }
    override suspend fun getText() = this@toSupport.getText()?.toString() ?: ""

    override fun setTextListener(listener: (String) -> Unit) {
        this.listener = listener
    }
}

fun String.toAnnotatedString() = AnnotatedString(this)

@Composable
inline operator fun <T> CompositionLocal<T>.invoke() = current

val LocalSnackbar: ProvidableCompositionLocal<SnackbarHostState> = compositionLocalOf { throw NullPointerException() }

inline fun resetFocus(
    keyboardController: SoftwareKeyboardController?,
    focusManager: FocusManager
) {
    keyboardController?.hide()
    focusManager.clearFocus(true)
}

val unsupported: Nothing
    get() = throw UnsupportedOperationException()

val WindowWidthSizeClass.rawValue: Int get() =
    when (this) {
        WindowWidthSizeClass.COMPACT -> 0
        WindowWidthSizeClass.MEDIUM -> 1
        WindowWidthSizeClass.EXPANDED -> 2
        else -> throw IllegalArgumentException("Unsupported WindowWidthSizeClass: $this")
    }

val WindowHeightSizeClass.rawValue: Int get() =
    when (this) {
        WindowHeightSizeClass.COMPACT -> 0
        WindowHeightSizeClass.MEDIUM -> 1
        WindowHeightSizeClass.EXPANDED -> 2
        else -> throw IllegalArgumentException("Unsupported WindowHeightSizeClass: $this")
    }

operator fun WindowWidthSizeClass.compareTo(other: WindowWidthSizeClass) = this.rawValue.compareTo(other.rawValue)
operator fun WindowHeightSizeClass.compareTo(other: WindowHeightSizeClass) = this.rawValue.compareTo(other.rawValue)

val WindowSizeClass.width get() = windowWidthSizeClass
val WindowSizeClass.height get() = windowHeightSizeClass

val WindowAdaptiveInfo.widthSizeClass get() = windowSizeClass.width
@Suppress("unused")
val WindowAdaptiveInfo.heightSizeClass get() = windowSizeClass.height

fun String.encodeJSON(): String {
    val out = StringBuilder()
    for (i in indices) {
        when (val c: Char = get(i)) {
            '"', '\\', '/' -> out.append('\\').append(c)
            '\t' -> out.append("\\t")
            '\b' -> out.append("\\b")
            '\n' -> out.append("\\n")
            '\r' -> out.append("\\r")
            else -> if (c.code <= 0x1F) {
                out.append("\\u${c.code.toString(16).padStart(4, '0')}")
            } else {
                out.append(c)
            }
        }
    }
    return "$out"
}

@Composable
operator fun PaddingValues.plus(other: PaddingValues) = PaddingValues(
    top = calculateTopPadding() + other.calculateTopPadding(),
    bottom = calculateBottomPadding() + other.calculateTopPadding(),
    start = when (LocalLayoutDirection()) {
        LayoutDirection.Ltr -> calculateLeftPadding(LocalLayoutDirection())
        LayoutDirection.Rtl -> calculateRightPadding(LocalLayoutDirection())
    },
    end = when (LocalLayoutDirection()) {
        LayoutDirection.Ltr -> calculateRightPadding(LocalLayoutDirection())
        LayoutDirection.Rtl -> calculateLeftPadding(LocalLayoutDirection())
    }
)

expect val materialYouAvailable: Boolean