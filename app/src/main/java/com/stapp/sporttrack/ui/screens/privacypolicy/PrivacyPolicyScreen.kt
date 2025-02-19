/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stapp.sporttrack.ui.screens.privacypolicy

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stapp.sporttrack.R
import com.stapp.sporttrack.ui.theme.SportTrackTheme

/**
 * Shows the privacy policy.
 */
@Composable
fun PrivacyPolicyScreen(modifier: Modifier) {
//    Politique de Confidentialité
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.fillMaxWidth(0.5f),
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = stringResource(id = R.string.health_connect_logo)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = stringResource(id = R.string.privacy_policy),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(32.dp))
        // Text(stringResource(R.string.privacy_policy_description))

        Text(
            text = """
                Nous collectons et utilisons vos informations personnelles uniquement aux fins décrites dans cette politique de confidentialité.
                Nous nous engageons à protéger votre vie privée et à garantir la sécurité de vos données personnelles.
                
                Voici les autorisations que nous demandons et pourquoi nous en avons besoin :
                
                1. **LIRE LE RYTHME CARDIAQUE (READ_HEART_RATE)** :
                Nous avons besoin de cette permission pour lire les données de votre rythme cardiaque à partir de Health Connect afin de suivre et d'analyser votre activité physique.
                
                2. **ÉCRIRE LE RYTHME CARDIAQUE (WRITE_HEART_RATE)** :
                Nous avons besoin de cette permission pour écrire des données de rythme cardiaque dans Health Connect.
                
                3. **LIRE LES PAS (READ_STEPS)** :
                Nous avons besoin de cette permission pour lire le nombre de pas que vous avez effectués, afin de suivre vos objectifs de marche et de condition physique.
                
                4. **ÉCRIRE LES PAS (WRITE_STEPS)** :
                Nous avons besoin de cette permission pour écrire des données de pas dans Health Connect.
                
                5. **LIRE L'EXERCICE (READ_EXERCISE)** :
                Nous avons besoin de cette permission pour lire les données de vos exercices, afin de suivre et d'analyser vos activités physiques.
                
                6. **ÉCRIRE L'EXERCICE (WRITE_EXERCISE)** :
                Nous avons besoin de cette permission pour écrire des données d'exercice dans Health Connect.
                
                7. **LIRE LES CALORIES TOTALES BRÛLÉES (READ_TOTAL_CALORIES_BURNED)** :
                Nous avons besoin de cette permission pour lire les données des calories que vous avez brûlées, afin de suivre votre activité physique et votre régime.
                
                8. **ÉCRIRE LES CALORIES TOTALES BRÛLÉES (WRITE_TOTAL_CALORIES_BURNED)** :
                Nous avons besoin de cette permission pour écrire des données de calories brûlées dans Health Connect.
                
                9. **LIRE LE POIDS (READ_WEIGHT)** :
                Nous avons besoin de cette permission pour lire les données de votre poids, afin de suivre votre condition physique.
                
                10. **ÉCRIRE LE POIDS (WRITE_WEIGHT)** :
                Nous avons besoin de cette permission pour écrire des données de poids dans Health Connect.
                
                11. **LIRE LES DONNÉES DE SANTÉ EN ARRIÈRE-PLAN (READ_HEALTH_DATA_IN_BACKGROUND)** :
                Nous avons besoin de cette permission pour lire des données de santé pendant que l'application est en arrière-plan, afin de fournir des analyses continues.
                
                12. **LIRE L'HISTORIQUE DES DONNÉES DE SANTÉ (READ_HEALTH_DATA_HISTORY)** :
                Nous avons besoin de cette permission pour lire l'historique de vos données de santé, afin de fournir des analyses et des tendances à long terme.
                
                Nous nous engageons à garantir la confidentialité et la sécurité de vos données personnelles. Si vous avez des questions concernant notre politique de confidentialité, veuillez nous contacter.
            """.trimIndent(),
            fontSize = 16.sp
        )
    }
}

@Preview
@Composable
fun PrivacyPolicyScreenPreview() {
    SportTrackTheme {
        PrivacyPolicyScreen(Modifier)
    }
}
