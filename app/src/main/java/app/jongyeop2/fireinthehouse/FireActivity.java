package app.jongyeop2.fireinthehouse;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

/**
 * Created by Mr.Han on 2016-09-19.
 */
public class FireActivity extends Activity implements OnMapReadyCallback {

    private LatLng position;
    private GoogleMap googleMap;
    private String firePlace;

    private BackPressCloseHandler backPressCloseHandler;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        position = findLatLng(firePlace);
        if(position == null)  {
            position = new LatLng(37.56, 126.97);
        }

        this.googleMap.setMyLocationEnabled(true);

        Marker marker = googleMap.addMarker(new MarkerOptions().position(position).title(firePlace));

        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
        this.googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire);

        backPressCloseHandler = new BackPressCloseHandler(this);

        TextView titleText = (TextView)findViewById(R.id.noti_title);
        TextView messageText = (TextView)findViewById(R.id.noti_message);
        TextView dateText = (TextView)findViewById(R.id.noti_date);

        titleText.setText(getIntent().getStringExtra("title"));
        messageText.setText(getIntent().getStringExtra("message"));
        dateText.setText(getIntent().getStringExtra("date"));

        String message = getIntent().getStringExtra("message");
        firePlace = message.substring(0, message.indexOf('('));

        MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

    private LatLng findLatLng(String address)
    {

        Geocoder geocoder = new Geocoder(this);
        Address addr;
        LatLng location = null;
        try {
            List<Address> listAddress = geocoder.getFromLocationName(address, 1);
            if(listAddress.size() > 0) {
                addr = listAddress.get(0);
                double lat = addr.getLatitude();
                double lng = addr.getLongitude();

                location = new LatLng(lat, lng);
            }
        } catch (IOException e) { e.printStackTrace(); }

        return location;
    }
}
