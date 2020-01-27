package com.openclassrooms.nycrealestatemanager.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.openclassrooms.nycrealestatemanager.database.AppDatabase;
import com.openclassrooms.nycrealestatemanager.model.PoiNextProperty;

/**
 * Provide access to the PoiNextProperty from the local database
 */
public class PoiNextPropertyContentProvider extends ContentProvider {

    // FOR DATA
    public static final String AUTHORITY = "com.openclassrooms.nycrealestatemanager.PoiNextPropertyContentProvider";
    public static final String TABLE_NAME = PoiNextProperty.class.getSimpleName();
    public static final Uri URI_POI_NEXT_PROPERTY = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);

    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        if (getContext() != null) {
            final Cursor cursor = AppDatabase.getInstance(getContext()).poiNextPropertyDAO().getAllWithCursor();
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
            return cursor;
        }
        throw new IllegalArgumentException("Failed to query row for uri " + uri);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return "vnd.android.cursor.poinextproperty/" + AUTHORITY + "." + TABLE_NAME;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}