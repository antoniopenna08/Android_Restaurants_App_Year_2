package Modules;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import Modules.Route;


public interface DirectionFinderListener {

    void onConnected(@Nullable Bundle bundle);


    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}
