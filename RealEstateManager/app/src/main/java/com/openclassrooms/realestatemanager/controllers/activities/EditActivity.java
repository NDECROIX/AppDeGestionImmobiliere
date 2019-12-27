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
import android.os.Parcelable;
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
import android.widget.TextView;

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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.textfield.TextInputEditText;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.base.BaseActivity;
import com.openclassrooms.realestatemanager.controllers.fragments.DatePickerFragment;
import com.openclassrooms.realestatemanager.injections.Injection;
import com.openclassrooms.realestatemanager.injections.ViewModelFactory;
import com.openclassrooms.realestatemanager.model.Agent;
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
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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

public class EditActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener, EditActivityPhotoRecyclerViewAdapter.OnClickPhotoListener {

    private static final String PROPERTY = "property";
    private static final String POIS = "pois";
    private static final String PHOTOS = "photos";

    public interface startEditActivityListener {
        void createProperty();
    }

    public static Intent newIntent(Context context, @Nullable Property property, @Nullable List<PoiNextProperty> pois,
                                   @Nullable List<Photo> photos) {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(context, EditActivity.class);
        if (property != null) {
            bundle.putSerializable(PROPERTY, property);
        }
        if (pois != null) {
            bundle.putParcelableArrayList(POIS, (ArrayList<? extends Parcelable>) pois);
        }
        if (photos != null) {
            bundle.putParcelableArrayList(PHOTOS, (ArrayList<? extends Parcelable>) photos);
        }
        intent.putExtras(bundle);
        return intent;
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
    @BindView(R.id.activity_edit_tie_date_sell)
    TextInputEditText tieSaleDate;
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
    @BindView(R.id.activity_edit_tv_agent)
    TextView tvAgent;

    private PropertyViewModel propertyViewModel;
    private EditActivityPhotoRecyclerViewAdapter adapter;
    private String currentPhotoPath;

    private String type;
    private String borough;
    private List<Poi> pois;
    private Property property;
    private List<Property> properties;
    private List<Agent> agents;
    private Agent agent;
    private boolean entryDate;

    // Update
    private Property propertyToUpdate;
    private List<PoiNextProperty> poisNextProperty;
    private List<String> poisPropertyName;
    private List<Photo> propertyPhotos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ButterKnife.bind(this);
        getExtras();
        pois = new ArrayList<>();
        property = new Property();
        configRecyclerView();
        configViewModel();
        configToolbar();
        configObserver();
        configViews();
    }

    private void getExtras() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            propertyToUpdate = (Property) bundle.getSerializable(PROPERTY);
            List<PoiNextProperty> bundlePois = bundle.getParcelableArrayList(POIS);
            if (bundlePois != null) {
                poisNextProperty = new ArrayList<>(bundlePois);
                poisPropertyName = new ArrayList<>();
                for (PoiNextProperty poi : poisNextProperty) {
                    poisPropertyName.add(poi.getPoiName());
                }
            }
            List<Photo> bundlePhotos = bundle.getParcelableArrayList(PHOTOS);
            if (bundlePhotos != null) {
                propertyPhotos = new ArrayList<>(bundlePhotos);
            }
        }
    }

    private void completeFieldsWithProperty(Property property) {
        DecimalFormat df = new DecimalFormat("#.##");
        tiePrice.setText(df.format(property.getPrice()));
        tieSurface.setText(String.valueOf(property.getSurface()));
        tieRooms.setText(String.valueOf(property.getRooms()));
        tieBedroom.setText(String.valueOf(property.getBedrooms()));
        tieBathroom.setText(String.valueOf(property.getBathrooms()));
        tieDescription.setText(property.getDescription());
        tieStreetNumber.setText(String.valueOf(property.getStreetNumber()));
        tieStreetName.setText(String.valueOf(property.getStreetName()));
        if (property.getAddressSupplement() != null)
            tieStreetSupplement.setText(property.getAddressSupplement());
        tieCity.setText(property.getCity());
        tieZipCode.setText(String.valueOf(property.getZip()));
        tieCountry.setText(property.getCountry());
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(property.getEntryDate());
        tieEntryDate.setText(String.format(Locale.getDefault(), "%d/%d/%d",
                calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR)));
        if (property.isSold()) {
            calendar = new GregorianCalendar();
            calendar.setTimeInMillis(property.getSaleDate());
            tieSaleDate.setText(String.format(Locale.getDefault(), "%d/%d/%d",
                    calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR)));
        }
        if (property.getAgentID() != null && !property.getAgentID().isEmpty())
            propertyViewModel.getAgent(property.getAgentID()).observe(this, agent ->
                    tvAgent.setText(String.format("%s %s", agent.getFirstName(), agent.getLastName())));
    }

    private void configViews() {
        displayBoroughRadioBtn();
        if (propertyToUpdate != null) {
            completeFieldsWithProperty(propertyToUpdate);
        }
    }

    private void configRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapter = new EditActivityPhotoRecyclerViewAdapter(this);
        recyclerView.setAdapter(adapter);
        if (propertyPhotos != null) {
            adapter.setPhotos(propertyPhotos);
        }
    }

    private void configToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Edit property");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @OnClick(R.id.activity_edit_ib_agent)
    public void onClickAgent() {
        if (this.agents.size() == 0) {
            showToastMessage(this, "No agents");
            return;
        }
        String[] agents = new String[this.agents.size()];
        int count = 0;
        for (Agent agent : this.agents) {
            agents[count] = String.format("%s %s", agent.getFirstName(), agent.getLastName());
            count++;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Agents")
                .setItems(agents, (dialog, which) -> {
                    tvAgent.setText(agents[which]);
                    agent = this.agents.get(which);
                });
        builder.create().show();
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
                customToast(this, type + ((propertyToUpdate == null) ? " added!" : " updated!"));
                startActivity(new Intent(this, MainActivity.class));
            } else {
                showToastMessage(this, "Property already exist!");
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
        propertyViewModel.getAgents().observe(this, agents -> this.agents = agents);
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
            if (propertyToUpdate != null && propertyToUpdate.getType().equals(type.getName())) {
                radioButton.setChecked(true);
                this.type = type.getName();
            }

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
            if (propertyToUpdate != null && propertyToUpdate.getBorough().equals(borough)) {
                radioButton.setChecked(true);
                this.borough = borough;
            }
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
            if (poisPropertyName != null && poisPropertyName.contains(poi.getName())) {
                checkBox.setChecked(true);
            }
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
                showToastMessage(this, "No photo chosen");
            }
        }
        if (requestCode == RC_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(currentPhotoPath));
                    startPhotoDialog(null, bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    showToastMessage(this, "No photo!");
                }
            } else {
                showToastMessage(this, "No photo!");
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
            showToastMessage(this, "No camera found!");
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
        entryDate = true;
        DatePickerFragment datePickerFragment = new DatePickerFragment(this);
        datePickerFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @OnClick(R.id.activity_edit_id_calendar_sell)
    public void onClickCalendarSell() {
        entryDate = false;
        DatePickerFragment datePickerFragment = new DatePickerFragment(this);
        datePickerFragment.show(getSupportFragmentManager(), "datePickerSell");
    }

    @Override
    @SuppressLint("SetTextI18n")
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        if (entryDate) {
            tieEntryDate.setText((month + 1) + "/" + dayOfMonth + "/" + year);
        } else {
            tieSaleDate.setText((month + 1) + "/" + dayOfMonth + "/" + year);
        }
    }

    //--------------------
    // FIELD VERIFICATION
    //--------------------

    private boolean champNotEmpty() {
        // Check than at least one type is selected and if it does not exist in the database, add it.
        if (this.type == null) {
            showToastMessage(this, "Please add a type");
            return false;
        } else {
            property.setType(type);
        }

        // Check if the price is correct
        if (tiePrice.getText() == null || tiePrice.getText().toString().isEmpty()) {
            showToastMessage(this, "Please add a price");
            return false;
        }
        try {
            Double price = Double.parseDouble(tiePrice.getText().toString());
            property.setPrice(price);
        } catch (NumberFormatException e) {
            showToastMessage(this, "Error in the price format");
            return false;
        }

        // Check if the surface is correct
        if (tieSurface.getText() == null || tieSurface.getText().toString().isEmpty()) {
            showToastMessage(this, "Please add a surface");
            return false;
        }
        try {
            Double surface = Double.parseDouble(tieSurface.getText().toString());
            property.setSurface(surface);
        } catch (NumberFormatException e) {
            showToastMessage(this, "Error on the surface");
            return false;
        }

        // Check if the number of rooms is correct
        if (tieRooms.getText() == null || tieRooms.getText().toString().isEmpty()) {
            showToastMessage(this, "Please add a number of rooms");
            return false;
        }
        try {
            int rooms = Integer.parseInt(tieRooms.getText().toString());
            property.setRooms(rooms);
        } catch (NumberFormatException e) {
            showToastMessage(this, "Error on the number of rooms");
            return false;
        }

        // Check if the number of bathrooms is correct
        if (tieBathroom.getText() != null && !tieBathroom.getText().toString().isEmpty()) {
            try {
                int bathrooms = Integer.parseInt(tieBathroom.getText().toString());
                property.setBathrooms(bathrooms);
            } catch (NumberFormatException e) {
                showToastMessage(this, "Error on the number of bathrooms");
                return false;
            }
        }

        // Check if the number of bedrooms is correct
        if (tieBedroom.getText() != null && !tieBedroom.getText().toString().isEmpty()) {
            try {
                int bedrooms = Integer.parseInt(tieBedroom.getText().toString());
                property.setBedrooms(bedrooms);
            } catch (NumberFormatException e) {
                showToastMessage(this, "Error on the number of bedrooms");
                return false;
            }
        }

        // Check if the number of bedrooms and bathrooms is less than the number of rooms
        if (property.getBathrooms() + property.getBedrooms() >= property.getRooms()) {
            showToastMessage(this, "Number of room less than bedrooms + bathrooms");
            return false;
        }


        // Check description
        if (tieDescription.getText() != null && !tieDescription.getText().toString().isEmpty()) {
            property.setDescription(tieDescription.getText().toString());
        }

        // Check if we have at least one photo
        if (adapter.getItemCount() == 0) {
            showToastMessage(this, "You need at least one photo");
            return false;
        }

        // Check street number
        if (tieStreetNumber.getText() == null || tieStreetNumber.getText().toString().isEmpty()) {
            showToastMessage(this, "Please add a street number");
            return false;
        }
        try {
            int streetNumber = Integer.parseInt(tieStreetNumber.getText().toString());
            property.setStreetNumber(streetNumber);
        } catch (NumberFormatException e) {
            showToastMessage(this, "Error on the street number");
            return false;
        }

        // Check the street name
        if (tieStreetName.getText() == null || tieStreetName.getText().toString().isEmpty()) {
            showToastMessage(this, "Please add a street name");
            return false;
        } else {
            property.setStreetName(tieStreetName.getText().toString());
        }

        // Check the address supplement
        if (tieStreetSupplement.getText() != null && !tieStreetSupplement.getText().toString().isEmpty()) {
            property.setAddressSupplement(tieStreetSupplement.getText().toString());
        }

        // Check the city
        if (tieCity.getText() == null || tieCity.getText().toString().isEmpty()) {
            showToastMessage(this, "Please add a city");
            return false;
        } else {
            property.setCity(tieCity.getText().toString());
        }

        // Check the Zip code
        if (tieZipCode.getText() == null || tieZipCode.getText().toString().isEmpty()) {
            showToastMessage(this, "Please add a Zip code");
            return false;
        }
        try {
            int zip = Integer.parseInt(tieZipCode.getText().toString());
            property.setZip(zip);
        } catch (NumberFormatException e) {
            showToastMessage(this, "Error on the zip code");
            return false;
        }

        // Check the country
        if (tieCountry.getText() == null || tieCountry.getText().toString().isEmpty()) {
            showToastMessage(this, "Please add a country");
            return false;
        } else {
            property.setCountry(tieCountry.getText().toString());
        }

        // Check the borough
        if (borough == null) {
            showToastMessage(this, "Please add a borough");
            return false;
        } else {
            property.setBorough(borough);
        }

        // Check entry date
        if (tieEntryDate.getText() == null || tieEntryDate.getText().toString().isEmpty()) {
            showToastMessage(this, "Please choose a date");
            return false;
        }
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            Date date = simpleDateFormat.parse(tieEntryDate.getText().toString());
            property.setEntryDate(date.getTime());
        } catch (ParseException p) {
            showToastMessage(this, "Error on the date");
            return false;
        }

        // Check sell date
        if (tieSaleDate.getText() != null && !tieSaleDate.getText().toString().isEmpty()) {
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                Date date = simpleDateFormat.parse(tieSaleDate.getText().toString());
                property.setSaleDate(date.getTime());
                property.setSold(true);
            } catch (ParseException p) {
                showToastMessage(this, "Error on the sale date");
                return false;
            }
        } else {
            property.setSold(false);
        }

        // Check date of sale against date of entry
        if (property.getSaleDate() != 0 && property.getEntryDate() > property.getSaleDate()) {
            showToastMessage(this, "Error between dates");
            return false;
        }

        // Check agent
        if (agent != null) {
            property.setAgentID(agent.getId());
        }

        String address = String.format("%s %s, %s, %s, %s", property.getStreetNumber(),
                property.getStreetName(), property.getCity(), property.getCountry(),
                property.getZip());
        LatLng latLng = Utils.getLocationFromAddress(this, address);
        if (latLng != null) {
            property.setLatitude(latLng.latitude);
            property.setLongitude(latLng.longitude);
            propertyViewModel.updateProperty(property);
        }
        return true;
    }

    private boolean insertPropertyInDatabase() {
        if (propertyToUpdate == null) {
            property.setId(Utils.convertStringMd5(property.getStringToHash()));
        } else {
            property.setId(propertyToUpdate.getId());
        }
        if (!properties.contains(property)) {
            propertyViewModel.insertProperty(property);
            return true;
        } else if (propertyToUpdate != null) {
            propertyViewModel.updateProperty(property);
            return true;
        }
        return false;
    }

    /**
     * Insert points of interests next the property and delete the old ones if they exist
     */
    private void insertPoiInDatabase() {
        for (Poi poi : pois) {
            PoiNextProperty poiNextProperty = new PoiNextProperty();
            poiNextProperty.setPoiName(poi.getName());
            poiNextProperty.setPropertyID(property.getId());
            if (this.poisNextProperty != null && this.poisNextProperty.contains(poiNextProperty)) {
                this.poisNextProperty.remove(poiNextProperty);
            } else {
                propertyViewModel.insertPoiNextProperty(poiNextProperty);
            }
        }
        if (this.poisNextProperty != null && !this.poisNextProperty.isEmpty())
            for (PoiNextProperty poiNextProperty : poisNextProperty) {
                propertyViewModel.deletePoiNextProperty(poiNextProperty);
            }
    }

    private void insertPhotoInDatabase() {
        for (Photo photo : adapter.getPhotos()) {
            photo.setPropertyID(property.getId());
            if (propertyPhotos == null || !propertyPhotos.remove(photo)) {
                propertyViewModel.insertPropertyPhoto(photo);
            }
        }
        if (this.propertyPhotos != null && !this.propertyPhotos.isEmpty())
            for (Photo photo : propertyPhotos) {
                propertyViewModel.deletePhoto(photo);
                File fileToDelete = new File(photo.getUri());
                fileToDelete.deleteOnExit();
            }
    }

    @Override
    public void onClickDeletePhoto(Photo photo) {
        if (propertyToUpdate != null) {
            File photoToDelete = new File(photo.getUri());
            photoToDelete.deleteOnExit();
        }
    }
}