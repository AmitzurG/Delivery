package com.gett.delivery.mobile.assignment.data

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.UnknownHostException
import javax.inject.Inject

class CourierRepository @Inject constructor(private val directionsService: DirectionsService, private val snapToRoadService: SnapToRoadService) { //}, private val CourierDao: ImageDao) {
    private val TAG = CourierRepository::class.java.name

    suspend fun getDirectionObject(origin: String, destination: String, waypoints: String? = null): DirectionObject? {
        var direction: DirectionObject? = null
        try {
            direction = if (waypoints == null) {
                directionsService.getDirectionObject(origin, destination)
            } else {
                directionsService.getDirectionObject(origin, destination, waypoints)
            }
        } catch (e: UnknownHostException) {
            Log.e(TAG, "Networking problem, UnknownHostException, null object will be returned, error=${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Exception, null object will be returned, error=${e.message}")
        }
        return direction
    }

    suspend fun getPositions(context: Context): List<Position> =
        Gson().fromJson(jsonString(context, "journey.json"), object : TypeToken<List<Position>>() {}.type)

    // it's worth to add the destination positions (from journey.json file) to a data base, set the Position class as room @Entity/table,
    // this requires a room type conversion (to types: Geo, Parcel) @TypeConverter, @ProvidedTypeConverter, because a limited time this hasn't been implemented for now

    private suspend fun jsonString(context: Context, fileName: String): String = withContext(Dispatchers.IO) {
        var jsonString = ""
        try {
            val inputStream = context.assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            jsonString = String(buffer)
        } catch (e: IOException) {
            Log.e(TAG, "IOException, error=${e.message}")
        }
        jsonString
    }

    suspend fun getSnappedPoints(path: String): List<SnappedPoint> {
        var snappedPoints = emptyList<SnappedPoint>()
        try {
            val snappedPointsObject = snapToRoadService.getSnappedPoints(path)
            snappedPoints = snappedPointsObject.snappedPoints ?: emptyList()
        } catch (e: UnknownHostException) {
            Log.e(TAG, "Networking problem, UnknownHostException, empty snapped point list will be returned, error=${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Exception, empty snapped point list will be returned, error=${e.message}")
        }
        return snappedPoints
    }
}
