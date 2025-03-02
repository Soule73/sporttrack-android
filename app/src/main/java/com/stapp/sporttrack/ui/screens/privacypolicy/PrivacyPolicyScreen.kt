package com.stapp.sporttrack.ui.screens.privacypolicy

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stapp.sporttrack.ui.theme.SportTrackTheme

/**
 * Shows the privacy policy.
 */
@Composable
fun PrivacyPolicyScreen(modifier: Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = """Nous collectons et utilisons vos informations personnelles uniquement aux fins décrites dans cette politique de confidentialité. Nous nous engageons à protéger votre vie privée et à garantir la sécurité de vos données personnelles. Voici les autorisations que nous demandons et pourquoi nous en avons besoin :""".trimIndent(),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "1. LIRE LE RYTHME CARDIAQUE (READ_HEART_RATE)".trimIndent(),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Nous avons besoin de cette permission pour lire les données de votre rythme cardiaque à partir de Health Connect afin de suivre et d'analyser votre activité physique.".trimIndent(),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "2. ÉCRIRE LE RYTHME CARDIAQUE (WRITE_HEART_RATE)".trimIndent(),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Nous avons besoin de cette permission pour écrire des données de rythme cardiaque dans Health Connect.".trimIndent(),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "3. LIRE LES PAS (READ_STEPS)".trimIndent(),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Nous avons besoin de cette permission pour lire le nombre de pas que vous avez effectués, afin de suivre vos objectifs de marche et de condition physique.".trimIndent(),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "4. ÉCRIRE LES PAS (WRITE_STEPS)",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Nous avons besoin de cette permission pour écrire des données de pas dans Health Connect.".trimIndent(),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "5. LIRE L'EXERCICE (READ_EXERCISE)",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Nous avons besoin de cette permission pour lire les données de vos exercices, afin de suivre et d'analyser vos activités physiques.".trimIndent(),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "6. ÉCRIRE L'EXERCICE (WRITE_EXERCISE)".trimIndent(),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Nous avons besoin de cette permission pour écrire des données d'exercice dans Health Connect.",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "7. LIRE LES CALORIES TOTALES (READ_TOTAL_CALORIES)",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Nous avons besoin de cette permission pour lire les données de calories que vous avez brûlées, afin de suivre votre activité physique et votre régime.".trimIndent(),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "8. ÉCRIRE LES CALORIES TOTALES (WRITE_TOTAL_CALORIES)".trimIndent(),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Nous avons besoin de cette permission pour écrire des données de calories dans Health Connect.".trimIndent(),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "9. LIRE LE POIDS (READ_WEIGHT)",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Nous avons besoin de cette permission pour lire les données de votre poids, afin de suivre votre condition physique.".trimIndent(),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "10. ÉCRIRE LE POIDS (WRITE_WEIGHT)",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Nous avons besoin de cette permission pour écrire des données de poids dans Health Connect.".trimIndent(),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "11. LIRE LES DONNÉES DE SANTÉ EN ARRIÈRE-PLAN (READ_HEALTH_DATA_IN_BACKGROUND)",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Nous avons besoin de cette permission pour lire des données de santé pendant que l'application est en arrière-plan, afin de fournir des analyses continues.".trimIndent(),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "12. ÉCRIRE LES DONNÉES DE SANTÉ EN ARRIÈRE-PLAN (WRITE_HEALTH_DATA_IN_BACKGROUND)",
                style = MaterialTheme.typography.titleMedium
            )
            Text(text = "Nous avons besoin de cette permission pour écrire des données de santé pendant que l'application est en arrière-plan, afin de fournir des analyses continues.".trimIndent())
            Text(
                text = "13. LIRE LES DONNÉES DE SANTÉ (READ_HEALTH_DATA)",
                style = MaterialTheme.typography.titleMedium
            )
            Text(text = "Nous avons besoin de cette permission pour lire des données de santé pendant que l'application est en arrière-plan, afin de fournir des analyses continues.".trimIndent())

            Text(
                text = """
             Nous nous engageons à garantir la confidentialité et la sécurité de vos données personnelles. Si vous avez des questions concernant notre politique de confidentialité, veuillez nous contacter.
            """.trimIndent(),
                fontSize = 16.sp
            )
        }
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "DefaultPreviewDark"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "DefaultPreviewLight",
    showBackground = true
)
@Composable
fun PrivacyPolicyScreenPreview() {
    SportTrackTheme {
        Scaffold { paddingValues ->
            PrivacyPolicyScreen(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(top = 10.dp)
                    .padding(horizontal = 10.dp),
            )
        }
    }
}
