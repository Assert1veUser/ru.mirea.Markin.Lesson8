package ru.mirea.markinaa.yandexdriverfavoriteplace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;

import com.yandex.mapkit.RequestPoint;
import com.yandex.mapkit.RequestPointType;
import com.yandex.mapkit.directions.DirectionsFactory;
import com.yandex.mapkit.directions.driving.DrivingOptions;
import com.yandex.mapkit.directions.driving.DrivingRoute;
import com.yandex.mapkit.directions.driving.VehicleOptions;
import com.yandex.mapkit.geometry.Point;

import android.graphics.PointF;
import android.os.Bundle;
import android.widget.Toast;

import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.directions.driving.DrivingRouter;
import com.yandex.mapkit.directions.driving.DrivingSession;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CompositeIcon;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.map.RotationType;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;
import com.yandex.runtime.Error;
import com.yandex.runtime.image.ImageProvider;
import com.yandex.runtime.network.NetworkError;
import com.yandex.runtime.network.RemoteError;

import java.util.ArrayList;
import java.util.List;

import ru.mirea.markinaa.yandexdriverfavoriteplace.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements UserLocationObjectListener, DrivingSession.DrivingRouteListener {

    private ActivityMainBinding binding;
    private UserLocationLayer userLocationLayer;
    private	static	final	int REQUEST_CODE_PERMISSION = 200;
    private Boolean isWork = true;
    private final Point ROUTE_START_LOCATION = new Point(55.79439, 37.70016);
    private final Point ROUTE_END_LOCATION = new Point(55.79498, 37.71146);
    private MapObjectCollection mapObjects;
    private DrivingRouter drivingRouter;
    private DrivingSession drivingSession;
    private int[] colors = {0xFFFF0000, 0xFF00FF00, 0x00FFBBBB, 0xFF0000FF};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        int	mapsPermissionStatus	=	ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        if	(mapsPermissionStatus	==	PackageManager.PERMISSION_GRANTED)	{
            isWork	=	true;
        }	else	{
            ActivityCompat.requestPermissions(this,	new	String[]{android.Manifest.permission
                            .ACCESS_FINE_LOCATION},
                    REQUEST_CODE_PERMISSION);
        }
        loadUserLocationLayer();
        binding.mapview.getMap().setRotateGesturesEnabled(false);

        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter();
        mapObjects = binding.mapview.getMap().getMapObjects().addCollection();
        submitRequest();
        markerLayer();
    }
    @Override
    public	void onRequestPermissionsResult(int	requestCode, @NonNull String[]	permissions,
                                              @NonNull	int[]	grantResults)	{
        super.onRequestPermissionsResult(requestCode,	permissions,	grantResults);
        switch	(requestCode){
            case	REQUEST_CODE_PERMISSION:
                isWork		=	grantResults[0]	==	PackageManager.PERMISSION_GRANTED;
                break;
        }
        if	(!isWork){
            finish();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        binding.mapview.onStart();
        MapKitFactory.getInstance().onStart();
    }
    @Override
    protected void onStop() {
        super.onStop();
        binding.mapview.onStop();
        MapKitFactory.getInstance().onStop();
    }
    @Override
    public void onObjectAdded(UserLocationView userLocationView) {
        userLocationLayer.setAnchor(
                new PointF((float)(binding.mapview.getWidth() * 0.5),
                        (float)(binding.mapview.getHeight() * 0.5)),
                new PointF((float)(binding.mapview.getWidth() * 0.5),
                        (float)(binding.mapview.getHeight() * 0.83)));
        userLocationView.getArrow().setIcon(ImageProvider.fromResource(
                this, android.R.drawable.arrow_up_float));
        CompositeIcon pinIcon = userLocationView.getPin().useCompositeIcon();
        userLocationView.getAccuracyCircle().setFillColor(Color.BLUE & 0x99ffffff);

    }

    @Override
    public void onDrivingRoutes(@NonNull List<DrivingRoute> list) {
        int color;
        for (int i = 0; i < list.size(); i++) {
            color = colors[i];
            mapObjects.addPolyline(list.get(i).getGeometry()).setStrokeColor(color);
        }
    }

    @Override
    public void onDrivingRoutesError(@NonNull Error error) {
        String errorMessage = getString(R.string.unknown_error_message);
        if (error instanceof RemoteError) {
            errorMessage = getString(R.string.remote_error_message);
        } else if (error instanceof NetworkError) {
            errorMessage = getString(R.string.network_error_message);
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }
    private void submitRequest() {
        DrivingOptions drivingOptions = new DrivingOptions();
        VehicleOptions vehicleOptions = new VehicleOptions();

        drivingOptions.setRoutesCount(4);

        ArrayList<RequestPoint> requestPoints = new ArrayList<>();

        requestPoints.add(new RequestPoint(ROUTE_START_LOCATION,
                RequestPointType.WAYPOINT,
                null));
        requestPoints.add(new RequestPoint(ROUTE_END_LOCATION,
                RequestPointType.WAYPOINT,
                null));

        drivingSession = drivingRouter.requestRoutes(requestPoints, drivingOptions,
                vehicleOptions, this);

    }

    @Override
    public void onObjectRemoved(@NonNull UserLocationView userLocationView) {
    }
    @Override
    public void onObjectUpdated(@NonNull UserLocationView userLocationView,
                                @NonNull ObjectEvent objectEvent) {
    }
    private void loadUserLocationLayer(){
        MapKit mapKit = MapKitFactory.getInstance();
        mapKit.resetLocationManagerToDefault();
        userLocationLayer = mapKit.createUserLocationLayer(binding.mapview.getMapWindow());
        userLocationLayer.setVisible(true);
        userLocationLayer.setHeadingEnabled(true);
        userLocationLayer.setObjectListener(this);
    }
    public void markerLayer(){
        PlacemarkMapObject marker = binding.mapview.getMap().getMapObjects().addPlacemark(new
                Point(55.79498, 37.71146), ImageProvider.fromResource(this,
                R.drawable.ic_action_));
        marker.addTapListener(new MapObjectTapListener() {
            @Override
            public boolean onMapObjectTap(@NonNull MapObject mapObject, @NonNull Point point) {
                /*Toast.makeText(getApplication(),"Marker click",
                        Toast.LENGTH_SHORT).show();*/
                Intent intent = new Intent(MainActivity.this, InfoActivity.class);
                startActivity(intent);
                return false;
            }
        });
    }
}