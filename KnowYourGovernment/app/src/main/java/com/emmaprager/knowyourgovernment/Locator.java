package com.emmaprager.knowyourgovernment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import static android.content.Context.LOCATION_SERVICE;
import static com.emmaprager.knowyourgovernment.MainActivity.LOCATION_REQUEST_CODE;

public class Locator {

    private MainActivity mainAct;
    private LocationManager lm;
    private LocationListener ll;

    public Locator(MainActivity activity) {
        mainAct = activity;

        if (checkPermission()) {
            setUpLocationManager();
            determineLocation();
        }
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(mainAct, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mainAct,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    public void setUpLocationManager() {
        if (!checkPermission())
            return;

        lm = (LocationManager) mainAct.getSystemService(LOCATION_SERVICE);
        ll = new LocationListener() {
            public void onLocationChanged(Location location) {
                mainAct.doLocationWork(location.getLatitude(), location.getLongitude());
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}
        };
        lm.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 1000, 0, ll);
    }

    public void determineLocation() {
        if (!checkPermission())
            return;

        if (lm == null)
            setUpLocationManager();

        if (lm != null) {
            Location loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (loc != null) {
                mainAct.doLocationWork(loc.getLatitude(), loc.getLongitude());
                Toast.makeText(mainAct, "Network Location Provider Chosen", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (lm != null) {
            Location loc = lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            if (loc != null) {
                mainAct.doLocationWork(loc.getLatitude(), loc.getLongitude());
                Toast.makeText(mainAct, "Passive Location Provider Chosen", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (lm != null) {
            Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (loc != null) {
                mainAct.doLocationWork(loc.getLatitude(), loc.getLongitude());
                Toast.makeText(mainAct, "GPS Location Provider Chosen", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        mainAct.noLocationAvailable();
        return;
    }

    public void shutdown() {
        lm.removeUpdates(ll);
        lm = null;
    }
}