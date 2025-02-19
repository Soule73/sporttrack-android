package com.stapp.sporttrack.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

data class SystemBarStyle(
    val backgroundColor: Color,
    val contentColor: Color
)

//val CustomStatusBarStyle = SystemBarStyle(
//    backgroundColor = Color.White, // Couleur de fond personnalisée
//    contentColor = BlueBlack       // Couleur de contenu personnalisée
//)
//
//val CustomNavigationBarStyle = SystemBarStyle(
//    backgroundColor = Color.White, // Couleur de fond personnalisée
//    contentColor = BlueBlack        // Couleur de contenu personnalisée
//)

private val DarkColorScheme = darkColorScheme(
    primary = LightPurple,
    secondary = LightYellow,
//    tertiary = LightGray,
//    background = BlueBlack,
//    surface = BlueBlack,
//    onPrimary = Color.White,
//    onSecondary = Color.Black,
//    onTertiary = Color.Black,
//    onBackground = Color.White,
//    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = LightPurple,
    secondary = LightYellow,
//    tertiary = LightGray,
//    background = Color.White,
//    surface = LightGray,
//    onPrimary = Color.White,
//    onSecondary = Color.Black,
//    onTertiary = Color.Black,
//    onBackground = Color.Black,
//    onSurface = Color.Black
)

@Composable
fun SportTrackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}


//private val DarkColorScheme = darkColorScheme(
//    primary = Purple80,
//    secondary = PurpleGrey80,
//    tertiary = Pink80
//)
//
//private val LightColorScheme = lightColorScheme(
//    primary = Purple40,
//    secondary = PurpleGrey40,
//    tertiary = Pink40
//
//    /* Other default colors to override
//    background = Color(0xFFFFFBFE),
//    surface = Color(0xFFFFFBFE),
//    onPrimary = Color.White,
//    onSecondary = Color.White,
//    onTertiary = Color.White,
//    onBackground = Color(0xFF1C1B1F),
//    onSurface = Color(0xFF1C1B1F),
//    */
//)
//
//@Composable
//fun SportTrackTheme(
//    darkTheme: Boolean = isSystemInDarkTheme(),
//    // Dynamic color is available on Android 12+
//    dynamicColor: Boolean = true,
//    content: @Composable () -> Unit
//) {
//    val colorScheme = when {
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }
//
//        darkTheme -> DarkColorScheme
//        else -> LightColorScheme
//    }
//
//    MaterialTheme(
//        colorScheme = colorScheme,
//        typography = Typography,
//        content = content
//    )
//}