
package com.stapp.sporttrack.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector
import com.stapp.sporttrack.R


const val EXERCISE_TYPE_NAV_ARGUMENT = "exerciseType"
/**
+ * Represents all screens within the application.
+ * Each screen is defined with properties to configure its behavior and appearance,
+ * such as navigation route, title, app bar visibility, bottom app bar visibility,
+ * back button presence, and icon.
+ * @property route The unique string identifier used for navigation to this screen.
+ * @property titleId The resource ID of the string used as the screen's title.
+ * @property hasAppBar `true` if the screen should display an app bar; `false` otherwise. Defaults to `true`.
+ * @property isLargeAppBar `true` if the app bar should use the large variant (e.g., expanded); `false` otherwise. Defaults to `false`.
+ * @property hasBottomAppBar `true` if the screen should display a bottom app bar; `false` otherwise. Defaults to `true`.
+ * @property hasBackBtn `true` if the app bar should include a back button; `false` otherwise. Defaults to `false`.
+ * @property icon The optional icon (as an `ImageVector`) to display in the navigation UI associated with this screen. Defaults to `null`.
+ * Each enum constant represents a distinct screen in the application and is configured with
+ * the specific UI elements needed for that screen.
 */
enum class Screen(
    val route: String,
    val titleId: Int,
    val hasAppBar: Boolean = true,
    val isLargeAppBar: Boolean = false,
    val hasBottomAppBar: Boolean = true,
    val hasBackBtn: Boolean = false,
    val icon: ImageVector? = null
) {
    LoginScreen(
        "login_screen",
        R.string.login_screen,
        hasAppBar = false,
        hasBottomAppBar = false
    ),
    RegisterScreen(
        "register_screen",
        R.string.register_screen,
        hasAppBar = false,
        hasBottomAppBar = false
    ),
    RegisterScreenStep1(
        "register_screen_step_1",
        R.string.register_screen_step_1,
        hasAppBar = false,
        hasBottomAppBar = false
    ),
    RegisterScreenStep2(
        "register_screen_step_2",
        R.string.register_screen_step_2,
        hasAppBar = false,
        hasBottomAppBar = false
    ),
    RegisterScreenStep3(
        "register_screen_3",
        R.string.register_screen_3,
        hasAppBar = false,
        hasBottomAppBar = false
    ),
    WelcomeScreen(
        "welcome_screen",
        R.string.welcome_screen,
        isLargeAppBar = true,
        icon = Icons.Outlined.Home
    ),
    DailyDetailsScreen(
        "daily_details_screen",
        R.string.daily_details_screen,
        isLargeAppBar = true,
        hasBackBtn = true,
        hasBottomAppBar = false,
    ),
    TrainingDetailsScreen(
        "training_details_screen",
        R.string.session_detail_screen,
        hasBottomAppBar = false,
        hasBackBtn = true
    ),
    ProfileScreen(
        "profile_screen",
        R.string.profile_screen,
        icon = Icons.Default.Person,
        hasAppBar = false
    ),
    ExerciseList(
        "exercise_list",
        R.string.exercise_list,
        isLargeAppBar = true,
        icon = Icons.Default.FitnessCenter
    ),
    ExerciseSession(
        "exercise_session",
        R.string.exercise_session,
        hasBottomAppBar = false,
        hasBackBtn=true
    ),
    ExerciseSessionDetail(
        "exercise_session_detail",
        R.string.exercise_session_detail,
        hasAppBar = false,
        hasBottomAppBar = false
    ),
    PrivacyPolicy(
        "privacy_policy",
        R.string.privacy_policy,
        hasBackBtn = true,
        isLargeAppBar = true,
        hasBottomAppBar = false
    ),

}
