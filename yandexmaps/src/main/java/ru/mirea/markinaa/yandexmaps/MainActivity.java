package ru.mirea.markinaa.yandexmaps;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;

import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CompositeIcon;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.RotationType;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;
import com.yandex.runtime.image.ImageProvider;

import ru.mirea.markinaa.yandexmaps.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements UserLocationObjectListener {


    private ActivityMainBinding binding;
    private UserLocationLayer userLocationLayer;
    private	static	final	int REQUEST_CODE_PERMISSION = 200;
    private Boolean isWork = true;

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
        /*MapKitFactory.initialize(this);
        binding.mapview.getMap().move(
                new CameraPosition(new Point(55.751574, 37.573856), 11.0f,
                        0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0),
                null);*/
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
    public void onObjectAdded(UserLocationView userLocationView) {
        userLocationLayer.setAnchor(
                new PointF((float)(binding.mapview.getWidth() * 0.5),
                        (float)(binding.mapview.getHeight() * 0.5)),
                new PointF((float)(binding.mapview.getWidth() * 0.5),
                        (float)(binding.mapview.getHeight() * 0.83)));
        userLocationView.getArrow().setIcon(ImageProvider.fromResource(
                this, android.R.drawable.arrow_up_float));
        CompositeIcon pinIcon = userLocationView.getPin().useCompositeIcon();
        pinIcon.setIcon(
                "pin",
                ImageProvider.fromResource(this, R.drawable.searh_result),
                new IconStyle().setAnchor(new PointF(0.5f, 0.5f))
                        .setRotationType(RotationType.ROTATE)
                        .setZIndex(1f)
                        .setScale(0.5f)
        );

        userLocationView.getAccuracyCircle().setFillColor(Color.BLUE & 0x99ffffff);
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

    private void loadUserLocationLayer(){
        MapKit mapKit = MapKitFactory.getInstance();
        mapKit.resetLocationManagerToDefault();
        userLocationLayer = mapKit.createUserLocationLayer(binding.mapview.getMapWindow());
        userLocationLayer.setVisible(true);
        userLocationLayer.setHeadingEnabled(true);
        userLocationLayer.setObjectListener(this);
    }

    @Override
    public void onObjectRemoved(@NonNull UserLocationView userLocationView) {
    }
    @Override
    public void onObjectUpdated(@NonNull UserLocationView userLocationView,
                                @NonNull ObjectEvent objectEvent) {
    }
}