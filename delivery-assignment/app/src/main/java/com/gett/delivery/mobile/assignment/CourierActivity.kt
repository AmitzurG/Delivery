package com.gett.delivery.mobile.assignment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.DialogFragment
import com.gett.delivery.mobile.assignment.data.CourierRepository
import com.gett.delivery.mobile.assignment.data.Position
import com.gett.delivery.mobile.assignment.data.SnappedPoint
import com.gett.delivery.mobile.assignment.databinding.ActivityCourierBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CourierActivity : AppCompatActivity(), OnMapReadyCallback {
    private val TAG = CourierRepository::class.java.name

    private lateinit var binding: ActivityCourierBinding
    private val viewModel: CourierViewModel by viewModels()
    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourierBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        addButtons()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        drawRoute()
        drawDestinationPoints()
        drawRouteToNextDestination()
    }

    // draw the whole courier route in yellow
    private fun drawRoute() = viewModel.getRoute().observe(this) {
        val points = PolyUtil.decode(it)
        if (points.size > 1) {
            setMapArea(points.first(), points.last())
            val polyline = PolylineOptions().addAll(points).width(8f).color(Color.YELLOW)
            map.addPolyline(polyline)
        }
    }

    private fun setMapArea(start: LatLng, end: LatLng) {
        val bounds = LatLngBounds.Builder().include(start).include(end).build()
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
    }

    @SuppressLint("PotentialBehaviorOverride")
    // draw markers for position types "pickup" and "drop_off", clicking on the marker will present the list of parcels to pickup/drop
    private fun drawDestinationPoints() = viewModel.getPositions("pickup", "drop_off").observe(this) { positions ->
        for ((i, position) in positions.withIndex()) {
            map.addMarker(MarkerOptions()
                .position(LatLng(position.geo.latitude, position.geo.longitude))
                .title(position.geo.address)
                .snippet(i.toString()))
        }

        // clicking on destination marker presents the list of parcels for it
        map.setOnMarkerClickListener {
            try {
                val index = it.snippet?.toInt() ?: 0
                val position = positions[index]
                if (position.parcels != null) {
                    showParcelsDialog(position)
                }
            } catch (e: NumberFormatException) {
                Log.w(TAG, "NumberFormatException - error=${e.message}")
            }
            true
        }
    }

    private var destinationPolyline: Polyline? = null
    // draw route to the destination in blue
    private fun drawRouteToNextDestination() = viewModel.routeToNextDestination().observe(this) {
        val points = PolyUtil.decode(it)
        val polylineOptions = PolylineOptions().addAll(points).width(16f).color(Color.BLUE)
        destinationPolyline = map.addPolyline(polylineOptions)
    }

    private fun goToNextDestination() {
        if (viewModel.destinationPosition.parcels != null) {
            showParcelsDialog(viewModel.destinationPosition)
            destinationPolyline?.remove()
            roadSnappedPointsPolyline?.remove()
            drawRouteToNextDestination()
        }
        if (!viewModel.deliveryCompleted) {
            viewModel.goToNextDestination()
        }
    }

    private var completed = false
    private fun showParcelsDialog(position: Position) {
        val parcelsDialog = ParcelsDialogFragment()
        val parcelsDialogArgs = Bundle().apply {
            putString(ParcelsDialogFragment.TITLE_KEY, "${position.geo.address} - ${position.type}")
            putStringArray(ParcelsDialogFragment.PARCELS_KEY, parcelList(position))
        }
        parcelsDialog.arguments = parcelsDialogArgs
        parcelsDialog.show(supportFragmentManager, null)
        parcelsDialog.deliveryCompleted = viewModel.deliveryCompleted
        completed = viewModel.deliveryCompleted
    }

    private fun parcelList(position: Position): Array<String> {
        val parcelList = arrayListOf<String>()
        position.parcels?.let { parcels ->
            for (parcel in parcels) {
                parcelList.add("${parcel.displayIdentifier} - ${parcel.barcode}")
            }
        }
        return parcelList.toTypedArray()
    }

    private var roadSnappedPointsPolyline: Polyline? = null
    // draw snapped points to road in red
    private fun drawRoadSnappedPointsRoute(snappedPoints: List<SnappedPoint>) {
        val points = mutableListOf<LatLng>()
        for (snappedPoint in snappedPoints) {
            points.add(LatLng(snappedPoint.location.latitude, snappedPoint.location.longitude))
        }
        val polylineOptions = PolylineOptions().addAll(points).width(12f).color(Color.RED)
        roadSnappedPointsPolyline = map.addPolyline(polylineOptions)
    }

    private fun addButtons() {
        // use of Jetpack Compose for the courier buttons ("Snap to road" button and "Arrived" button)
        // to demonstrate using of Jetpack Compose declarative UI toolkit,
        // the map is SupportMapFragment (com.google.android.gms.maps.SupportMapFragment) and it's not possible to use it in Jetpack Compose

        binding.composeView.setContent {
            MaterialTheme {
                CourierButtons(
                    snapToRoadButtonOnClick = {
                        viewModel.getSnappedPoints().observe(this) {
                            drawRoadSnappedPointsRoute(it)
                        }
                    },

                    arrivedButtonOnClick = {
                        // make an arrival sound
                        val toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
                        toneGenerator.startTone(ToneGenerator.TONE_CDMA_PIP, 150)

                        if (completed) {
                            DeliveryCompletedDialogFragment().show(supportFragmentManager, null)
                        } else {
                            goToNextDestination()
                        }
                    }
                )
            }
        }
    }

    class ParcelsDialogFragment : DialogFragment() {
        companion object {
            const val TITLE_KEY = "titleKey"
            const val PARCELS_KEY = "parcelsKey"
        }

        var deliveryCompleted = false

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val builder = AlertDialog.Builder(context).apply {
                setTitle(arguments?.getString(TITLE_KEY))
                setItems(arguments?.getStringArray(PARCELS_KEY) ?: arrayOf(getString(R.string.noParcels))) { _, _ -> }
                setPositiveButton(R.string.done) { _, _ ->
                    if (deliveryCompleted) {
                        DeliveryCompletedDialogFragment().show(parentFragmentManager, null)
                    }
                }
            }
            return builder.create()
        }
    }

    class DeliveryCompletedDialogFragment : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val builder = AlertDialog.Builder(context).apply {
                setTitle(R.string.deliveryCompleted)
                setPositiveButton(android.R.string.ok) { _, _ -> }
            }
            return builder.create()
        }
    }

// with the next comment code we can get location updates, and on arriving to destination location we can show the parcels for it,
// it's possible to use Geofence of 100 meter radius, for example, around the destination for determine if the courier arrives to the destination,
// need to start location updates in onResume(), need to stop location updates in onPause(),
// Also, by using location, we can get the current location and draw route from it to the pickup location,
// hasn't been implemented because a limited time, more time is required to implement this, also to test this, need physically go to the route area


//    private val fusedLocationClient by lazy { LocationServices.getFusedLocationProviderClient(this) }
//    private val locationRequest = LocationRequest.create().apply {
//        interval = TimeUnit.SECONDS.toMillis(30)
//        fastestInterval = TimeUnit.SECONDS.toMillis(30)
//        maxWaitTime = TimeUnit.MINUTES.toMillis(2)
//        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//    }
//    private val locationCallback = object : LocationCallback() {
//        override fun onLocationResult(locationResult: LocationResult?) {
//            super.onLocationResult(locationResult)
//            if (locationResult?.lastLocation != null) {
//                // location updated, if it's destination location show the parcels for it
//            } else {
//                Log.w(TAG, "Location information isn't available.")
//            }
//        }
//    }

}

// use of Jetpack Compose for the courier buttons ("Snap to road" button and "Arrived" button)
// to demonstrate using of Jetpack Compose declarative UI toolkit,
// the map is SupportMapFragment and it's not possible to use it in Jetpack Compose

@Composable
fun CourierButtons(snapToRoadButtonOnClick: () -> Unit = {}, arrivedButtonOnClick: () -> Unit = {}) {
    val context = LocalContext.current

    Surface(color = androidx.compose.ui.graphics.Color.Transparent) {
        Row(horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {

            // snap the navigation route to road button
            Button(
                onClick = snapToRoadButtonOnClick,
                modifier = Modifier.width(150.dp).padding(10.dp)

            ) {
                Text(text = context.getString(R.string.snapToRoad))
            }

            // arrived button
            Button(
                onClick = arrivedButtonOnClick,
                modifier = Modifier.width(150.dp).padding(10.dp)
            ) {
                Text(text = context.getString(R.string.arrived))
            }
        }
    }
}

@Preview("CourierButtons preview")
@Composable
fun CourierButtonsPreview() {
    MaterialTheme {
        CourierButtons()
    }
}
