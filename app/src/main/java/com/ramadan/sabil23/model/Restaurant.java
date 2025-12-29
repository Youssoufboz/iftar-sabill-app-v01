package com.ramadan.sabil23.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class Restaurant implements ClusterItem {
    private String id;
    private String name;
    private String vicinity;
    private double rating;
    private String photoReference;
    private boolean isOpen;
    private String priceLevel;
    private LatLng position;
    private String placeId;

    public Restaurant(String id, String name, String vicinity, double rating,
                      String photoReference, boolean isOpen, String priceLevel,
                      LatLng position, String placeId) {
        this.id = id;
        this.name = name;
        this.vicinity = vicinity;
        this.rating = rating;
        this.photoReference = photoReference;
        this.isOpen = isOpen;
        this.priceLevel = priceLevel;
        this.position = position;
        this.placeId = placeId;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getVicinity() { return vicinity; }
    public double getRating() { return rating; }
    public String getPhotoReference() { return photoReference; }
    public boolean isOpen() { return isOpen; }
    public String getPriceLevel() { return priceLevel; }
    public String getPlaceId() { return placeId; }

    // ClusterItem implementation
    @NonNull
    @Override
    public LatLng getPosition() { return position; }

    @Override
    public String getTitle() { return name; }

    @Override
    public String getSnippet() { return vicinity; }

    @Nullable
    @Override
    public Float getZIndex() {
        return 0f;
    }
}

