package ru.telecor.gm.mobile.droid.ui.map

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.google.maps.android.data.geojson.GeoJsonLineStringStyle
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle
import kotlinx.android.synthetic.main.fragment_task.*
import org.json.JSONObject
import ru.telecor.gm.mobile.droid.R

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {

        fun createIntent(
            standGeoJson: String? = null,
            evacuationGeoJson: String? = null,
            routeToRoadGeoJson: String? = null,
            lat: Double,
            lon: Double,
            context: Context
        ) =
            Intent(context, MapActivity::class.java).apply {
                standGeoJson?.let { putExtra(STAND_GEO_JSON_EXTRA_KEY, it) }
                evacuationGeoJson?.let { putExtra(EVACUATION_GEO_JSON_EXTRA_KEY, it) }
                routeToRoadGeoJson?.let { putExtra(ROUTE_TO_ROAD_GEO_JSON_EXTRA_KEY, it) }
                putExtra(LATITUDE_EXTRA_KEY, lat)
                putExtra(LONGITUDE_EXTRA_KEY, lon)
            }

        private const val STAND_GEO_JSON_EXTRA_KEY = "standGeoJson"
        private const val EVACUATION_GEO_JSON_EXTRA_KEY = "evacuationGeoJson"
        private const val ROUTE_TO_ROAD_GEO_JSON_EXTRA_KEY = "routeToRoad"
        private const val LATITUDE_EXTRA_KEY = "lat"
        private const val LONGITUDE_EXTRA_KEY = "lon"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        gMap.onCreate(Bundle())
        gMap.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {

        val standGeoJson = intent.getStringExtra(STAND_GEO_JSON_EXTRA_KEY)
        val evacuationGeoJson = intent.getStringExtra(EVACUATION_GEO_JSON_EXTRA_KEY)
        val routeToRoad = intent.getStringExtra(ROUTE_TO_ROAD_GEO_JSON_EXTRA_KEY)
        val lat = intent.getDoubleExtra(LATITUDE_EXTRA_KEY, 0.0)
        val lon = intent.getDoubleExtra(LONGITUDE_EXTRA_KEY, 0.0)

        googleMap?.let {
            it.addMarker(MarkerOptions().position(LatLng(lat, lon)).title("Marker"))
            it.animateCamera(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.Builder()
                        .target(LatLng(lat, lon))
                        .zoom(16f)
                        .build()
                )
            )

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                it.isMyLocationEnabled = true
                it.uiSettings?.isMyLocationButtonEnabled = false
            }

            val redLineStyle = GeoJsonLineStringStyle()
            redLineStyle.color = Color.RED

            val greenLineStyle = GeoJsonLineStringStyle()
            greenLineStyle.color = Color.GREEN

            val blueLineStyle = GeoJsonLineStringStyle()
            blueLineStyle.color = Color.BLUE

            if (standGeoJson != null && evacuationGeoJson != null) {
                val standObj = JSONObject(standGeoJson)
                val standLayer = GeoJsonLayer(it, standObj)
                standLayer.features.forEach { item ->
                    item.lineStringStyle = redLineStyle
                    item.polygonStyle = GeoJsonPolygonStyle().apply { strokeColor = Color.RED }
                }

                val evacuationObj = JSONObject(evacuationGeoJson)
                val evacuationLayer = GeoJsonLayer(it, evacuationObj)
                evacuationLayer.features.forEach { item ->
                    item.lineStringStyle = greenLineStyle
                    item.polygonStyle = GeoJsonPolygonStyle().apply { strokeColor = Color.GREEN }

                    standLayer.addLayerToMap()
                    evacuationLayer.addLayerToMap()
                }

                if (routeToRoad != null) {
                    val obj = JSONObject(routeToRoad)
                    val layer = GeoJsonLayer(it, obj)
                    layer.features.forEach { item -> item.lineStringStyle = blueLineStyle }
                    layer.addLayerToMap()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        gMap.onStart()
    }

    override fun onStop() {
        gMap.onStop()

        super.onStop()
    }

    override fun onResume() {
        super.onResume()

        gMap.onResume()
    }

    override fun onPause() {
        gMap.onPause()

        super.onPause()
    }

    override fun onDestroy() {
        gMap.onDestroy()

        super.onDestroy()
    }
}
