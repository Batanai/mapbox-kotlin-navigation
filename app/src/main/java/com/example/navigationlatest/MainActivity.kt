package com.example.navigationlatest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.navigationlatest.ui.theme.NavigationLatestTheme
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.bindgen.ExpectedFactory
import com.mapbox.common.location.DeviceLocationProviderFactory
import com.mapbox.common.location.Location
import com.mapbox.maps.ImageHolder
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.navigation.base.options.LocationOptions
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp
import com.mapbox.navigation.core.trip.session.LocationMatcherResult
import com.mapbox.navigation.core.trip.session.LocationObserver
import com.mapbox.navigation.ui.maps.camera.NavigationCamera
import com.mapbox.navigation.ui.maps.camera.data.MapboxNavigationViewportDataSource

class MainActivity : ComponentActivity() {

    private lateinit var mapView: MapView
    private lateinit var navigationOptions: NavigationOptions
    private lateinit var viewportDataSource: MapboxNavigationViewportDataSource
    private lateinit var camera: NavigationCamera
    private lateinit var myObserver: MyMapboxNavigationObserver


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navigationOptions = NavigationOptions.Builder(this)
            // additional options
            .build()

        MapboxNavigationApp
            .setup(navigationOptions)
            .attach(this)

        setContentView(R.layout.activity_main)
        // Initialize MapView
        mapView = findViewById(R.id.mapView)

        viewportDataSource = MapboxNavigationViewportDataSource(mapView.mapboxMap)
        camera = NavigationCamera(mapView.mapboxMap, mapView.camera, viewportDataSource)

        // Initialize MyMapboxNavigationObserver
        myObserver = MyMapboxNavigationObserver(viewportDataSource, camera, this)

        // Example of requesting camera to follow
        camera.requestNavigationCameraToFollowing()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
        MapboxNavigationApp
            .registerObserver(myObserver)
        camera.requestNavigationCameraToFollowing()
    }

    override fun onResume() {
        super.onResume()
        mapView.onStart()
        MapboxNavigationApp
            .registerObserver(myObserver)
        camera.requestNavigationCameraToFollowing()
    }

    override fun onPause() {
        super.onPause()
        mapView.onStop()
        MapboxNavigationApp
            .unregisterObserver(myObserver)
        camera.requestNavigationCameraToOverview()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
        MapboxNavigationApp
            .unregisterObserver(myObserver)
        camera.requestNavigationCameraToIdle()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
        camera.requestNavigationCameraToIdle()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
        MapboxNavigationApp
            .unregisterObserver(myObserver)
        camera.requestNavigationCameraToIdle()
    }

}

