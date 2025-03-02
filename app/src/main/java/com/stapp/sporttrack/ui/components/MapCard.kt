package com.stapp.sporttrack.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline

@Composable
fun MapCard(
    displayMap: Boolean,
    cameraPositionState: CameraPositionState,
    mapProperties: MapProperties,
    onMapLoaded: (() -> Unit)?,
    currentPostion: LatLng?,
    path: List<LatLng>,
    configuration: Configuration
) {
    if (displayMap) {
        Box(
            modifier = Modifier.padding(10.dp)
        ) {
            GoogleMap(
                cameraPositionState = cameraPositionState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height((configuration.screenHeightDp.dp / 2))
                    .clip(MaterialTheme.shapes.extraLarge),
                onMapLoaded = onMapLoaded,
                properties = mapProperties,
            ) {
                if (currentPostion != null) {
                    Marker(
                        state = MarkerState(position = currentPostion),
                        title = "Votre position"
                    )

                    if (path.isNotEmpty()) {
                        Polyline(
                            points = path,
                            color = MaterialTheme.colorScheme.primary,
                            width = 5f
                        )
                    }
                }
            }
            //googleMapOptionsFactory = {
            //    GoogleMapOptions().mapId(mapId)
            //}
        }
    }

}