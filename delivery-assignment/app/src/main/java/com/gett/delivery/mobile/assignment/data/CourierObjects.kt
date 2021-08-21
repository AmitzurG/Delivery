package com.gett.delivery.mobile.assignment.data

import com.google.gson.annotations.SerializedName

data class DirectionObject(val routes: List<Route?>?, val status: String?)
data class Route(val summary: String?, @SerializedName("overview_polyline") val overviewPolyline: OverviewPolyline?)
data class OverviewPolyline(val points: String?)

data class Position(val type: String, val state: String, val geo: Geo, val parcels: List<Parcel>?)
data class Geo(val address: String, val latitude: Double, val longitude: Double)
data class Parcel(val barcode: String, @SerializedName("display_identifier") val displayIdentifier: String)

data class SnappedPointsObject(val snappedPoints: List<SnappedPoint>?)
data class SnappedPoint(val location: Location, val placeId: String)
data class Location(val latitude: Double, val longitude: Double)

