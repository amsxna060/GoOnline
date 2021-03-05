package com.amansiol.goonline.services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.amansiol.goonline.constant.Constants;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/*
This service class which extends intentservice class
this class used for creating background service
at the time we fetch address from internet
FusedLocationProviderClient api provided by google
of user with gps.

if we fetch address in foreground from internet then it block
the mainUI thread so it may create error.
 */
public class FetchAddressIntentService extends IntentService {

    private ResultReceiver resultReceiver;

    public FetchAddressIntentService() {
        super("FetchAddressIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String errorMessage = "";
            resultReceiver = intent.getParcelableExtra(Constants.RECEIVER);
            Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
            if (location == null) {
                return;
            }
            /*
            Here we get address and send it to
            receiver (Receiver is present in Location Access Activity downside)
            with the help of this function.
            func_Name : deliverResultToReceiver
             */
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = null;
            StringBuffer addressFragments = new StringBuffer();
            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            } catch (IOException e) {
                errorMessage = e.getMessage();
            }
            if (addresses == null || addresses.isEmpty()) {
                deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
            } else {
                Address address = addresses.get(0);
                if(address.getFeatureName()==null)
                {
                    addressFragments.append(" " + "#");
                }else {
                    addressFragments.append(address.getFeatureName() + "#");
                }
                if(address.getSubLocality()==null)
                {
                    addressFragments.append(" " + "#");
                }else {
                    addressFragments.append(address.getSubLocality() + "#");
                }
                if(address.getLocality()==null)
                {
                    addressFragments.append(" " + "#");
                }else {
                    addressFragments.append(address.getLocality() + "#");
                }
                if(address.getAdminArea()==null)
                {
                    addressFragments.append(" " + "#");
                }else {
                    addressFragments.append(address.getAdminArea() + "#");
                }
                if(address.getCountryName()==null)
                {
                    addressFragments.append(" " + "#");
                }else {
                    addressFragments.append(address.getCountryName() + "#");
                }
                if(address.getPostalCode()==null)
                {
                    addressFragments.append(" " + "#");
                }else {
                    addressFragments.append(address.getPostalCode() + "#");
                }
            }

            String saddress = addressFragments.toString();
            deliverResultToReceiver(Constants.SUCCESS_RESULT, saddress);
        }
    }
   /*
   This function we used to
   send data or address we
   fetched or get by api
   to Receiver class present  in
   LocationAccessActivity.
    */
    private void deliverResultToReceiver(int resultCode, String addressMessage) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, addressMessage);
        resultReceiver.send(resultCode, bundle);
    }


}
