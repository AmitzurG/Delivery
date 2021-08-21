package com.gett.delivery.mobile.assignment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import com.gett.delivery.mobile.assignment.data.CourierRepository
import com.gett.delivery.mobile.assignment.data.Position
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class CourierViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {

    @Inject lateinit var repository: CourierRepository
    private lateinit var positions: List<Position>
    private var destinationIndex = 1 // initial the destination index to the first destination of type pickup
    val destinationPosition
        get() = positions[destinationIndex]
    val deliveryCompleted
        get() = destinationIndex == positions.size - 1

    fun getRoute() = liveData(Dispatchers.IO) {
        if (!::positions.isInitialized) {
            positions = repository.getPositions(getApplication())
        }
        val direction = repository.getDirectionObject(getFirstOrigin(), getLastDestination(), getWaypoints())
        emit(direction?.routes?.get(0)?.overviewPolyline?.points)
    }

    fun routeToNextDestination() = liveData(Dispatchers.IO) {
        if (!::positions.isInitialized) {
            positions = repository.getPositions(getApplication())
        }
        val direction = repository.getDirectionObject(getOrigin(), getDestination())
        emit(direction?.routes?.get(0)?.overviewPolyline?.points)
    }

    fun getPositions(vararg positionTypes: String) = liveData(Dispatchers.IO) {
        if (!::positions.isInitialized) {
            positions = repository.getPositions(getApplication())
        }
        val filteredPositions: List<Position>
        if (positionTypes.isNullOrEmpty()) {
            filteredPositions = positions
        } else {
            filteredPositions = mutableListOf()
            for (type in positionTypes) {
                filteredPositions.addAll(positions.filter { it.type == type })
            }
        }
        emit(filteredPositions)
    }

    fun goToNextDestination() {
        // destinations of type "pickup" or "drop_off" are after "navigate_to_pickup or "navigate_to_drop_off", so +2,
        destinationIndex += 2

        // it's worth to handle only destinations of type pickup/drop_off (filtering to list of only these types),
        // but I didn't notice that predecessors navigate_to_pickup/navigate_to_drop_off have same location,
        // and didn't left me a time to handle this way, please consider this
    }

    fun getSnappedPoints() = liveData(Dispatchers.IO) {
        if (!::positions.isInitialized) {
            positions = repository.getPositions(getApplication())
        }
        val snappedPoints = repository.getSnappedPoints(getPath())
        emit(snappedPoints)
    }

    private fun getFirstOrigin(): String {
        val origin = positions.first()
        return "${origin.geo.latitude},${origin.geo.longitude}"
    }

    private fun getLastDestination(): String {
        val destination = positions.last()
        return "${destination.geo.latitude},${destination.geo.longitude}"
     }

    private fun getWaypoints(): String {
        var waypoints = ""
        if (positions.size > 2) {
            waypoints = "${positions[1].geo.latitude},${positions[1].geo.longitude}"
            for (i in 2 until positions.size - 1) {
                waypoints = "$waypoints|${positions[i].geo.latitude},${positions[i].geo.longitude}"
            }
        }
        return waypoints
    }

    private fun getOrigin(): String {
        val originIndex = if (destinationIndex > 1) destinationIndex - 2 else destinationIndex - 1
        val origin = positions[originIndex]
        return "${origin.geo.latitude},${origin.geo.longitude}"
    }

    private fun getDestination(): String {
        val destination = positions[destinationIndex]
        return "${destination.geo.latitude},${destination.geo.longitude}"
    }

    private fun getPath(): String {
//        var path = ""
//        if (positions.size > 2) {
//            path = "${positions[0].geo.latitude},${positions[0].geo.longitude}"
//            for (i in 1 until positions.size) {
//                path = "$path|${positions[i].geo.latitude},${positions[i].geo.longitude}"
//            }
//        }
        val originIndex = if (destinationIndex > 1) destinationIndex - 2 else destinationIndex - 1
        val origin = positions[originIndex]
        val destination = positions[destinationIndex]
        return "${origin.geo.latitude},${origin.geo.longitude}|" +
                "${destination.geo.latitude},${destination.geo.longitude}"
    }
}
