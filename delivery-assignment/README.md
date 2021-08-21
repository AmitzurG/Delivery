# Gett.Delivery Mobile Assignment

Preliminary setup:
1. Copy the app/local.gradle.example file to a newly created app/local.gradle file.
2. Replace Google API key placeholder <paste-your-google-api-key-here> at app/local.gradle, with your Google API key.

In this assignment, you are requested to develop a courier app, which assists a courier to navigate through a delivery journey.
The delivery journey is constructed from a single pickup point and multiple drop-off points.
The app will present a navigation route from the courier current location to the next destination point (pickup / drop-off) on a map.
The courier will signal his arrival to the destination point by pressing the arrived button on the map.
Upon arrival at the pickup point, the courier will be presented with a list of parcels he is required to collect.
At the arrival of each drop-off point the courier will be presented with a list of parcels he is required to deliver in that particular drop-off point.
At the end of the delivery journey the courier will be presented with a delivery completed dialog.
In order to save time and to narrow the scope of this assignment, the delivery journey data can be deserialized from the [journey.json][journey-json-file] file to a local database.

This repository is already integrated with the following:
* [Google Maps SDK for Android][maps-sdk-api-url]
* [Directions API][directions-api-url]
* [Roads API][roads-api-url]

Basic requirements:
* Present the navigation route from the courier current location to the next destination point (pickup / drop-off) on a Google Map with the assistance of [Maps SDK][maps-sdk-api-url] and [Directions API][directions-api-url].
* Upon arrival at the pickup point, present the list of parcels to collect.
* Upon arrival at the drop off point, present the list of parcels to deliver.
* At the end of the journey present a simple delivery completed dialog.

Bonus:
* Make use of [Kotlin coroutines][kotlin-coroutines-url].
* Make use of reactive programming ([Kotlin flows][kotlin-flows-url]).
* Make use of declerative UI toolkit ([Jetpack Compose][jetpack-compose-url]).
* Snap the navigation route to the road driving directions with the help of [Roads API][roads-api-url].

Please see the app wireframe.
<p align="center">
  <img src="./delivery_mobile_assignment_wireframe.png" width="1080">
</p>

> Please pay attention to the app architecture, by taking into account the [SOLID principles][solid-url], [Clean Architecture][clean-arch-url], and other best practices. Make use of the latest proven external libraries to develop a robust app that complies with the current industry standards.

__Notice:__
__This assignment should take around 7 hours to complete, but not limited to that. Please make sure you monitor your development hours and report them back upon completion.__

### And most importantly enjoy the ride...
<p align="left">
  <img src="./courier.gif" width="416" height="212">
</p>

[maps-sdk-api-url]: https://developers.google.com/maps/documentation/android-sdk/overview
[directions-api-url]: https://developers.google.com/maps/documentation/directions/overview
[roads-api-url]: https://developers.google.com/maps/documentation/roads/overview
[kotlin-coroutines-url]: https://developer.android.com/kotlin/coroutines
[kotlin-flows-url]: https://developer.android.com/kotlin/flow
[jetpack-compose-url]: https://developer.android.com/jetpack/compose
[solid-url]: https://en.wikipedia.org/wiki/SOLID
[clean-arch-url]: https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html
[journey-json-file]: ./journey.json
