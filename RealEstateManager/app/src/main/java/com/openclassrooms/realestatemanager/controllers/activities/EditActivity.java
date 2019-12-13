package com.openclassrooms.realestatemanager.controllers.activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.base.BaseActivity;
import com.openclassrooms.realestatemanager.controllers.fragments.DatePickerFragment;
import com.openclassrooms.realestatemanager.injections.Injection;
import com.openclassrooms.realestatemanager.injections.ViewModelFactory;
import com.openclassrooms.realestatemanager.model.Photo;
import com.openclassrooms.realestatemanager.model.Poi;
import com.openclassrooms.realestatemanager.model.PoiNextProperty;
import com.openclassrooms.realestatemanager.model.Property;
import com.openclassrooms.realestatemanager.model.Type;
import com.openclassrooms.realestatemanager.utils.Utils;
import com.openclassrooms.realestatemanager.view.adapters.EditActivityPhotoRecyclerViewAdapter;
import com.openclassrooms.realestatemanager.viewmodels.PropertyViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class EditActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener {

    public interface startEditActivityListener {
        void createProperty();
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, EditActivity.class);
    }

    private static final int RC_IMAGE_PERMS = 666;
    private static final int RC_CHOOSE_PHOTO = 333;
    private static final int RC_CAMERA_PERMS = 777;
    private static final int RC_TAKE_PHOTO = 999;

    @BindView(R.id.activity_edit_toolbar)
    Toolbar toolbar;
    @BindView(R.id.activity_edit_radio_grp_type_left)
    RadioGroup radioGroupTypeLeft;
    @BindView(R.id.activity_edit_radio_grp_type_right)
    RadioGroup radioGroupTypeRight;
    @BindView(R.id.activity_edit_radio_grp_borough_left)
    RadioGroup radioGroupBoroughLeft;
    @BindView(R.id.activity_edit_radio_grp_borough_right)
    RadioGroup radioGroupBoroughRight;
    @BindView(R.id.activity_edit_check_box_grp_poi_left)
    LinearLayout checkBoxGrpLeft;
    @BindView(R.id.activity_edit_check_box_grp_poi_right)
    LinearLayout checkBoxGrpRight;
    @BindView(R.id.activity_edit_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.activity_edit_tie_date)
    TextInputEditText tieEntryDate;
    @BindView(R.id.activity_edit_tie_price)
    TextInputEditText tiePrice;
    @BindView(R.id.activity_edit_tie_surface)
    TextInputEditText tieSurface;
    @BindView(R.id.activity_edit_tie_rooms)
    TextInputEditText tieRooms;
    @BindView(R.id.activity_edit_tie_bathroom)
    TextInputEditText tieBathroom;
    @BindView(R.id.activity_edit_tie_bedroom)
    TextInputEditText tieBedroom;
    @BindView(R.id.activity_edit_tie_description)
    TextInputEditText tieDescription;
    @BindView(R.id.activity_edit_tie_street_number)
    TextInputEditText tieStreetNumber;
    @BindView(R.id.activity_edit_tie_street_name)
    TextInputEditText tieStreetName;
    @BindView(R.id.activity_edit_tie_street_supplement)
    TextInputEditText tieStreetSupplement;
    @BindView(R.id.activity_edit_tie_city)
    TextInputEditText tieCity;
    @BindView(R.id.activity_edit_tie_zip)
    TextInputEditText tieZipCode;
    @BindView(R.id.activity_edit_tie_country)
    TextInputEditText tieCountry;
    @BindView(R.id.activity_edit_tie_agent)
    TextInputEditText tieAgent;

    private PropertyViewModel propertyViewModel;
    private EditActivityPhotoRecyclerViewAdapter adapter;
    private String currentPhotoPath;

    private String type;
    private String borough;
    private List<Poi> pois;
    private Property property;
    private List<Property> properties;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ButterKnife.bind(this);
        pois = new ArrayList<>();
        property = new Property();
        configRecyclerView();
        configViewModel();
        configToolbar();
        configObserver();
        configViews();
    }

    private void configViews() {
        displayBoroughRadioBtn();
    }

    private void configRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapter = new EditActivityPhotoRecyclerViewAdapter();
        recyclerView.setAdapter(adapter);
    }

    private void configToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Edit property");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_edit_toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_activity_edit_validate) {
            editProperty();
        }
        return super.onOptionsItemSelected(item);
    }

    private void editProperty() {
        if (champNotEmpty()) {
            if (insertPropertyInDatabase()) {
                insertPoiInDatabase();
                insertPhotoInDatabase();
                startActivity(new Intent(this, MainActivity.class));
            } else {
                showToastMessage("Property already exist!");
            }
        }
    }

    private void configViewModel() {
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory(this);
        this.propertyViewModel = ViewModelProviders.of(this, viewModelFactory).get(PropertyViewModel.class);
    }

    private void configObserver() {
        propertyViewModel.getTypes().observe(this, this::updateTypeRadioBtn);
        propertyViewModel.getAllPoi().observe(this, this::updatePoiCheckBox);
        propertyViewModel.getProperties().observe(this, properties -> this.properties = properties);
    }

    /**
     * Display all types as a radio button in the group radio and
     * switch between left and right
     *
     * @param types all types from the database
     */
    private void updateTypeRadioBtn(List<Type> types) {
        boolean left = true;
        for (Type type : types) {
            RadioButton radioButton = new RadioButton(getApplicationContext());
            radioButton.setText(type.getName());
            radioButton.setOnClickListener(l -> {
                if (l.getParent() == radioGroupTypeRight) {
                    radioGroupTypeLeft.clearCheck();
                } else {
                    radioGroupTypeRight.clearCheck();
                }
                this.type = type.getName();
            });
            if (left) {
                radioGroupTypeLeft.addView(radioButton);
            } else {
                radioGroupTypeRight.addView(radioButton);
            }
            left = !left;
        }
    }

    /**
     * Display all borough as a radio button in the group radio and
     * switch between left and right
     */
    private void displayBoroughRadioBtn() {
        boolean left = true;
        for (String borough : Property.getBoroughs()) {
            RadioButton radioButton = new RadioButton(getApplicationContext());
            radioButton.setText(borough);
            radioButton.setOnClickListener(l -> {
                if (l.getParent() == radioGroupBoroughRight) {
                    radioGroupBoroughLeft.clearCheck();
                } else {
                    radioGroupBoroughRight.clearCheck();
                }
                this.borough = borough;
            });
            if (left) {
                radioGroupBoroughLeft.addView(radioButton);
            } else {
                radioGroupBoroughRight.addView(radioButton);
            }
            left = !left;
        }
    }

    /**
     * Display all poi as check box and switch between left and right
     *
     * @param POIs All POIs from the database
     */
    private void updatePoiCheckBox(List<Poi> POIs) {
        boolean left = true;
        for (Poi poi : POIs) {
            CheckBox checkBox = new CheckBox(getApplicationContext());
            checkBox.setText(poi.getName());
            checkBox.setOnCheckedChangeListener((btn, checked) -> {
                if (checked) {
                    pois.add(poi);
                } else {
                    pois.remove(poi);
                }
            });
            if (left) {
                checkBoxGrpLeft.addView(checkBox);
            } else {
                checkBoxGrpRight.addView(checkBox);
            }
            left = !left;
        }
    }

    @OnClick(R.id.activity_edit_ib_photo)
    @AfterPermissionGranted(RC_IMAGE_PERMS)
    public void onClickPhoto() {
        choosePhotoFromDevice();
    }

    /**
     * Get the photo from the device
     */
    private void choosePhotoFromDevice() {
        if (EasyPermissions.hasPermissions(this, READ_EXTERNAL_STORAGE)) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, RC_CHOOSE_PHOTO);
        } else {
            getPermission(READ_EXTERNAL_STORAGE);
        }
    }

    /**
     * Get the read external storage permission.
     */
    private void getPermission(String permission) {
        if (permission.equals(READ_EXTERNAL_STORAGE)) {
            if (ContextCompat.checkSelfPermission(this,
                    READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{READ_EXTERNAL_STORAGE},
                        RC_IMAGE_PERMS);
            }
        } else if (permission.equals(CAMERA)) {
            if (ContextCompat.checkSelfPermission(this,
                    CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{CAMERA, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE},
                        RC_CAMERA_PERMS);
            }
        }
    }

    private void handleResponse(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_CHOOSE_PHOTO) {
            if (resultCode == RESULT_OK) {
                startPhotoDialog(data.getData(), null);
            } else {
                showToastMessage("No photo chosen");
            }
        }
        if (requestCode == RC_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(currentPhotoPath));
                    startPhotoDialog(null, bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    showToastMessage("No photo!");
                }
            } else {
                showToastMessage("No photo!");
            }
        }
    }

    /**
     * Display a dialog to add title.
     * The photo is not added to database so we don't need to add the property id.
     * We inflate the view in the dialog.
     *
     * @param data   Uri
     * @param bitmap photo from the camera
     */
    @SuppressLint("InflateParams")
    @SuppressWarnings("ConstantConditions")
    private void startPhotoDialog(Uri data, Bitmap bitmap) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.activity_edit_dialog, null);
        ImageView imageView = view.findViewById(R.id.activity_edit_dialog_preview);
        if (imageView != null) {
            Glide.with(this).load((data == null) ? bitmap : data).into(imageView);
        }
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                .setPositiveButton("ADD", (dialog, id) -> {
                    if (bitmap != null) {
                        galleryAddPic();
                    } else {
                        saveBitmapToThePath(data);
                    }
                    TextInputEditText title = view.findViewById(R.id.activity_edit_dialog_tie_title);
                    Photo photo = new Photo(currentPhotoPath, null, (title.getText() == null) ? "" : title.getText().toString());
                    adapter.setPhoto(photo);
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());
        builder.create().show();
    }

    private void saveBitmapToThePath(Uri data) {
        try {
            InputStream iStream = getContentResolver().openInputStream(data);
            byte[] inputData = new byte[0];
            if (iStream != null) {
                inputData = Utils.getBytes(iStream);
            }
            FileOutputStream out = new FileOutputStream(createImageFile());
            out.write(inputData);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        handleResponse(requestCode, resultCode, data);
    }

    @OnClick(R.id.activity_edit_ib_camera)
    @AfterPermissionGranted(RC_CAMERA_PERMS)
    public void onClickCamera() {
        if (checkCameraHardware()) {
            takePhotoFromCamera();
        } else {
            showToastMessage("No camera found!");
        }
    }

    /**
     * Get the photo from the device camera
     */
    private void takePhotoFromCamera() {
        if (EasyPermissions.hasPermissions(this, CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "com.openclassrooms.realestatemanager.fileprovider",
                            photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(intent, RC_TAKE_PHOTO);
                }
            }
        } else {
            getPermission(CAMERA);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    /**
     * Add the current picture to the gallery
     */
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    /**
     * Check if this device has a camera
     */
    private boolean checkCameraHardware() {
        return this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    @OnClick(R.id.activity_edit_id_calendar)
    public void onClickCalendar() {
        DatePickerFragment datePickerFragment = new DatePickerFragment(this);
        datePickerFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    @SuppressLint("SetTextI18n")
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        tieEntryDate.setText((month + 1) + "/" + dayOfMonth + "/" + year);
    }

    //--------------------
    // FIELD VERIFICATION
    //--------------------

    private boolean champNotEmpty() {
        // Check than at least one type is selected and if it does not exist in the database, add it.
        if (this.type == null) {
            showToastMessage("Please add a type");
            return false;
        } else {
            property.setType(type);
        }

        // Check if the price is correct
        if (tiePrice.getText() == null || tiePrice.getText().toString().isEmpty()) {
            showToastMessage("Please add a price");
            return false;
        }
        try {
            Double price = Double.parseDouble(tiePrice.getText().toString());
            property.setPrice(price);
        } catch (NumberFormatException e) {
            showToastMessage("Error in the price format");
            return false;
        }

        // Check if the surface is correct
        if (tieSurface.getText() == null || tieSurface.getText().toString().isEmpty()) {
            showToastMessage("Please add a surface");
            return false;
        }
        try {
            Double surface = Double.parseDouble(tieSurface.getText().toString());
            property.setSurface(surface);
        } catch (NumberFormatException e) {
            showToastMessage("Error on the surface");
            return false;
        }

        // Check if the number of rooms is correct
        if (tieRooms.getText() == null || tieRooms.getText().toString().isEmpty()) {
            showToastMessage("Please add a number of rooms");
            return false;
        }
        try {
            int rooms = Integer.parseInt(tieRooms.getText().toString());
            property.setRooms(rooms);
        } catch (NumberFormatException e) {
            showToastMessage("Error on the number of rooms");
            return false;
        }

        // Check if the number of bathrooms is correct
        if (tieBathroom.getText() != null && !tieBathroom.getText().toString().isEmpty()) {
            try {
                int bathrooms = Integer.parseInt(tieBathroom.getText().toString());
                property.setBathrooms(bathrooms);
            } catch (NumberFormatException e) {
                showToastMessage("Error on the number of bathrooms");
                return false;
            }
        }

        // Check if the number of bedrooms is correct
        if (tieBedroom.getText() != null && !tieBedroom.getText().toString().isEmpty()) {
            try {
                int bedrooms = Integer.parseInt(tieBedroom.getText().toString());
                property.setBedrooms(bedrooms);
            } catch (NumberFormatException e) {
                showToastMessage("Error on the number of bedrooms");
                return false;
            }
        }

        // Check description
        if (tieDescription.getText() != null && !tieDescription.getText().toString().isEmpty()) {
            property.setDescription(tieDescription.getText().toString());
        }

        // Check if we have at least one photo
        if (adapter.getItemCount() == 0) {
            showToastMessage("You need at least one photo");
            return false;
        }

        // Check street number
        if (tieStreetNumber.getText() == null || tieStreetNumber.getText().toString().isEmpty()) {
            showToastMessage("Please add a street number");
            return false;
        }
        try {
            int streetNumber = Integer.parseInt(tieStreetNumber.getText().toString());
            property.setStreetNumber(streetNumber);
        } catch (NumberFormatException e) {
            showToastMessage("Error on the street number");
            return false;
        }

        // Check the street name
        if (tieStreetName.getText() == null || tieStreetName.getText().toString().isEmpty()) {
            showToastMessage("Please add a street name");
            return false;
        } else {
            property.setStreetName(tieStreetName.getText().toString());
        }

        // Check the address supplement
        if (tieStreetSupplement.getText() != null && tieStreetSupplement.getText().toString().isEmpty()) {
            property.setAddressSupplement(tieStreetSupplement.getText().toString());
        }

        // Check the city
        if (tieCity.getText() == null || tieCity.getText().toString().isEmpty()) {
            showToastMessage("Please add a city");
            return false;
        } else {
            property.setCity(tieCity.getText().toString());
        }

        // Check the Zip code
        if (tieZipCode.getText() == null || tieZipCode.getText().toString().isEmpty()) {
            showToastMessage("Please add a Zip code");
            return false;
        }
        try {
            int zip = Integer.parseInt(tieZipCode.getText().toString());
            property.setZip(zip);
        } catch (NumberFormatException e) {
            showToastMessage("Error on the zip code");
            return false;
        }

        // Check the country
        if (tieCountry.getText() == null || tieCountry.getText().toString().isEmpty()) {
            showToastMessage("Please add a country");
            return false;
        } else {
            property.setCountry(tieCountry.getText().toString());
        }

        // Check the borough
        if (borough == null) {
            showToastMessage("Please add a borough");
            return false;
        } else {
            property.setBorough(borough);
        }

        // Check date
        if (tieEntryDate.getText() == null || tieEntryDate.getText().toString().isEmpty()) {
            showToastMessage("Please choose a date");
            return false;
        }
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            Date date = simpleDateFormat.parse(tieEntryDate.getText().toString());
            property.setEntryDate(date.getTime());
        } catch (ParseException p) {
            showToastMessage("Error on the date");
            return false;
        }

        // Check agent
        if (tieAgent.getText() == null || tieAgent.getText().toString().isEmpty()) {
            showToastMessage("Pleas add a real estate manager");
            return false;
        } else {
            property.setAgentID(tieAgent.getText().toString());
        }

        showToastMessage("Verification done! all is ok.");
        return true;
    }

    private boolean insertPropertyInDatabase() {
        property.setId(Utils.convertStringMd5(property.getStringToHash()));
        if (!properties.contains(property)) {
            propertyViewModel.insertProperty(property);
            return true;
        }
        return false;
    }

    private void insertPoiInDatabase() {
        for (Poi poi : pois) {
            PoiNextProperty poiNextProperty = new PoiNextProperty();
            poiNextProperty.setPoiName(poi.getName());
            poiNextProperty.setPropertyID(property.getId());
            propertyViewModel.insertPoiNextProperty(poiNextProperty);
        }
    }

    private void insertPhotoInDatabase() {
        for (Photo photo : adapter.getPhotos()) {
            photo.setPropertyID(property.getId());
            propertyViewModel.insertPropertyPhoto(photo);
        }
    }
}