package com.example.navigationlatest

import android.content.Context
import com.mapbox.common.location.Location
import com.mapbox.maps.EdgeInsets
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.directions.session.RoutesObserver
import com.mapbox.navigation.core.lifecycle.MapboxNavigationObserver
import com.mapbox.navigation.core.trip.session.LocationMatcherResult
import com.mapbox.navigation.core.trip.session.LocationObserver
import com.mapbox.navigation.core.trip.session.RouteProgressObserver
import com.mapbox.navigation.ui.maps.camera.NavigationCamera
import com.mapbox.navigation.ui.maps.camera.data.MapboxNavigationViewportDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class MyMapboxNavigationObserver(
    private val viewportDataSource: MapboxNavigationViewportDataSource,
    private val camera: NavigationCamera,
    context: Context
) : MapboxNavigationObserver {
    private val mutableLocation = MutableStateFlow<LocationMatcherResult?>(null)
    val locationFlow: Flow<LocationMatcherResult?> = mutableLocation

    init {
        val pixelDensity = context.resources.displayMetrics.density
        viewportDataSource.followingPadding = EdgeInsets(
            180.0 * pixelDensity,
            40.0 * pixelDensity,
            150.0 * pixelDensity,
            40.0 * pixelDensity
        )
        viewportDataSource.overviewPadding = EdgeInsets(
            140.0 * pixelDensity,
            40.0 * pixelDensity,
            120.0 * pixelDensity,
            40.0 * pixelDensity
        )
    }


    private val locationObserver = object : LocationObserver {
        override fun onNewLocationMatcherResult(locationMatcherResult: LocationMatcherResult) {
            mutableLocation.value = locationMatcherResult
            viewportDataSource.onLocationChanged(locationMatcherResult.enhancedLocation)
            camera.requestNavigationCameraToFollowing()
        }

        override fun onNewRawLocation(rawLocation: Location) {
            // no op
        }
    }


    private val routesObserver = RoutesObserver { routes ->
        viewportDataSource.onRouteChanged(routes.navigationRoutes.first())
        camera.requestNavigationCameraToOverview()
    }

    private val routeProgressObserver = RouteProgressObserver { routeProgress ->
        viewportDataSource.onRouteProgressChanged(routeProgress)
        camera.requestNavigationCameraToFollowing()
    }


    override fun onAttached(mapboxNavigation: MapboxNavigation) {
        mapboxNavigation.registerLocationObserver(locationObserver)
        mapboxNavigation.registerRoutesObserver(routesObserver)
        mapboxNavigation.registerRouteProgressObserver(routeProgressObserver)
    }

    override fun onDetached(mapboxNavigation: MapboxNavigation) {
        mapboxNavigation.unregisterLocationObserver(locationObserver)
        mapboxNavigation.unregisterRoutesObserver(routesObserver)
        mapboxNavigation.unregisterRouteProgressObserver(routeProgressObserver)
    }
}