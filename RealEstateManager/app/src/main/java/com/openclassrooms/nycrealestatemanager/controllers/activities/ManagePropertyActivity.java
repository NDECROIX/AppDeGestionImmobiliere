package com.openclassrooms.nycrealestatemanager.controllers.activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.openclassrooms.nycrealestatemanager.R;
import com.openclassrooms.nycrealestatemanager.base.BaseActivity;
import com.openclassrooms.nycrealestatemanager.controllers.fragments.DatePickerFragment;
import com.openclassrooms.nycrealestatemanager.database.AppDatabase;
import com.openclassrooms.nycrealestatemanager.injections.Injection;
import com.openclassrooms.nycrealestatemanager.injections.ViewModelFactory;
import com.openclassrooms.nycrealestatemanager.model.Agent;
import com.openclassrooms.nycrealestatemanager.model.Photo;
import com.openclassrooms.nycrealestatemanager.model.Poi;
import com.openclassrooms.nycrealestatemanager.model.PoiNextProperty;
import com.openclassrooms.nycrealestatemanager.model.Property;
import com.openclassrooms.nycrealestatemanager.model.Type;
import com.openclassrooms.nycrealestatemanager.utils.UtilsPhoto;
import com.openclassrooms.nycrealestatemanager.view.adapters.EditActivityPhotoRecyclerViewAdapter;
import com.openclassrooms.nycrealestatemanager.view.holders.ManagePropertyActivityViewHolder;
import com.openclassrooms.nycrealestatemanager.viewmodel.PropertyViewModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static com.openclassrooms.nycrealestatemanager.notification.NotificationHelper.displayNotification;

public class ManagePropertyActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener, EditActivityPhotoRecyclerViewAdapter.OnClickPhotoListener, ManagePropertyActivityViewHolder.CheckFieldsListener {

    /**
     * Callback that allows a fragment to start this fragment by its activity
     */
    public interface startEditActivityListener {
        void createProperty();
    }

    /**
     * Edit activity intent
     *
     * @param context  Activity context
     * @param property Property to update
     * @return Intent
     */
    public static Intent newIntent(Context context, @Nullable Property property) {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(context, ManagePropertyActivity.class);
        if (property != null) {
            bundle.putParcelable(PROPERTY, property);
        }
        intent.putExtras(bundle);
        return intent;
    }

    // Views
    private ManagePropertyActivityViewHolder viewHolder;

    // View Model
    private PropertyViewModel propertyViewModel;

    // Photo recycler view
    private EditActivityPhotoRecyclerViewAdapter adapter;

    // Path where the current photo is located
    private String currentPhotoPath;

    // Pass too true if data has been modified
    private boolean dataHasBeenChanged;

    // Property data
    private String type;
    private String borough;
    private List<Poi> pois;
    private Property property;
    private List<Property> properties;
    private List<Agent> agents;
    private Agent agent;
    private int calendarId;

    // Property update
    private Property propertyToUpdate;
    private List<PoiNextProperty> poisNextProperty;
    private List<String> poisPropertyName;
    private List<Photo> propertyPhotos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        viewHolder = new ManagePropertyActivityViewHolder(this);
        ButterKnife.bind(this);
        getExtras();
        pois = new ArrayList<>();
        property = new Property();
        configViewModel();
        configRecyclerView();
        configToolbar();
        configObserver();
        configViews();
    }

    /**
     * Retrieve property data in the extras
     */
    private void getExtras() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            propertyToUpdate = bundle.getParcelable(PROPERTY);
        }
    }

    // Complete view
    private void configViews() {
        updateTypeRadioBtn();
        displayBoroughRadioBtn();
        if (propertyToUpdate != null) {
            completeFieldsWithProperty(propertyToUpdate);
        }
    }

    /**
     * Complete fields with property data
     *
     * @param property Property to show
     */
    private void completeFieldsWithProperty(Property property) {
        viewHolder.completeFieldsWithProperty(property, propertyViewModel, this);
    }

    private void configRecyclerView() {
        viewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapter = new EditActivityPhotoRecyclerViewAdapter(this);
        viewHolder.recyclerView.setAdapter(adapter);
        if (propertyToUpdate != null) {
            propertyViewModel.getPropertyPhotos(propertyToUpdate.getId()).observe(this, photos -> {
                adapter.setPhotos(photos);
                propertyPhotos = new ArrayList<>(photos);
            });
        }
    }

    private void configToolbar() {
        setSupportActionBar(viewHolder.toolbar);
        if (getSupportActionBar() != null) {
            if (propertyToUpdate != null) {
                getSupportActionBar().setTitle("Edit property");
            } else {
                getSupportActionBar().setTitle("Create property");
            }
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Display agents in the dialogue box
     */
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
                    viewHolder.tvAgent.setText(agents[which]);
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
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Validate the property and add it to the local database
     */
    private void editProperty() {
        if (champNotEmpty()) {
            if (insertPropertyInDatabase()) {
                insertPoiInDatabase();
                insertPhotoInDatabase();
                if (dataHasBeenChanged) {
                    if (propertyToUpdate == null) {
                        displayNotification(property, this);
                    } else {
                        customToast(this, type + " updated!");
                    }
                } else {
                    customToast(this, "No change");
                }
                this.finish();
            } else {
                showToastMessage(this, "Property already exist!");
            }
        }
    }

    private void configViewModel() {
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory(AppDatabase.getInstance(this));
        this.propertyViewModel = new ViewModelProvider(this, viewModelFactory).get(PropertyViewModel.class);
    }

    private void configObserver() {
        if (propertyToUpdate != null) {
            propertyViewModel.getPoisNextProperty(this.propertyToUpdate.getId()).observe(this, poiNextProperties -> {
                this.poisNextProperty = new ArrayList<>(poiNextProperties);
                this.poisPropertyName = new ArrayList<>();
                for (PoiNextProperty poi : this.poisNextProperty) {
                    this.poisPropertyName.add(poi.getPoiName());
                }
                updatePoiCheckBox();
            });
        } else {
            updatePoiCheckBox();
        }
        propertyViewModel.getProperties().observe(this, properties -> this.properties = properties);
        propertyViewModel.getAgents().observe(this, agents -> this.agents = agents);
    }

    /**
     * Display all types as a radio button in the group radio and
     * switch between left and right
     */
    private void updateTypeRadioBtn() {
        boolean left = true;
        for (Type type : Type.getAllTypes()) {
            RadioButton radioButton = new RadioButton(getApplicationContext());
            radioButton.setText(type.getName());
            radioButton.setOnClickListener(l -> {
                if (l.getParent() == viewHolder.radioGroupTypeRight) {
                    viewHolder.radioGroupTypeLeft.clearCheck();
                } else {
                    viewHolder.radioGroupTypeRight.clearCheck();
                }
                this.type = type.getName();
            });
            if (left) {
                viewHolder.radioGroupTypeLeft.addView(radioButton);
            } else {
                viewHolder.radioGroupTypeRight.addView(radioButton);
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
                if (l.getParent() == viewHolder.radioGroupBoroughRight) {
                    viewHolder.radioGroupBoroughLeft.clearCheck();
                } else {
                    viewHolder.radioGroupBoroughRight.clearCheck();
                }
                this.borough = borough;
            });
            if (left) {
                viewHolder.radioGroupBoroughLeft.addView(radioButton);
            } else {
                viewHolder.radioGroupBoroughRight.addView(radioButton);
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
     */
    private void updatePoiCheckBox() {
        boolean left = true;
        for (Poi poi : Poi.getAllPoi()) {
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
                viewHolder.checkBoxGrpLeft.addView(checkBox);
            } else {
                viewHolder.checkBoxGrpRight.addView(checkBox);
            }
            left = !left;
            if ((poisPropertyName != null) && poisPropertyName.contains(poi.getName())) {
                checkBox.setChecked(true);
            }
        }
    }

    @OnClick(R.id.activity_edit_ib_photo)
    public void onClickPhoto() {
        choosePhotoFromDevice();
    }

    /**
     * Get the photo from the device
     */
    @AfterPermissionGranted(RC_IMAGE_PERMS)
    private void choosePhotoFromDevice() {
        if (EasyPermissions.hasPermissions(this, READ_EXTERNAL_STORAGE)) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, RC_CHOOSE_PHOTO);
        } else {
            getReadExternalStoragePermission();
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
                Bitmap bitmap = getPic();
                startPhotoDialog(null, bitmap);
            } else {
                showToastMessage(this, "No photo!");
            }
        }
    }

    /**
     * Get the bitmap from the currentPhotoPath
     *
     * @return Photo
     */
    private Bitmap getPic() {
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inPurgeable = true;

        return BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
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
                    String name = Photo.getName(currentPhotoPath);
                    Photo photo = new Photo(name, null, (title.getText() == null) ? "" : title.getText().toString());
                    adapter.setPhoto(photo);
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());
        builder.create().show();
    }

    /**
     * Move the bitmap in a new directory
     *
     * @param data Bitmap uri
     */
    private void saveBitmapToThePath(Uri data) {
        currentPhotoPath = UtilsPhoto.saveBitmapToThePath(this, data);
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
    @AfterPermissionGranted(RC_CAMERA_PERMS)
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
                            getString(R.string.activity_edit_authority_uri),
                            photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(intent, RC_TAKE_PHOTO);
                }
            }
        } else {
            getCameraPermission();
        }
    }

    /**
     * Create path for the photo
     *
     * @return Image file
     * @throws IOException Create temp file
     */
    private File createImageFile() throws IOException {
        File image = UtilsPhoto.createImageFile(this);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * Add the current picture to the gallery
     */
    private void galleryAddPic() {
        UtilsPhoto.galleryAddPic(this, currentPhotoPath);
    }

    /**
     * Check if this device has a camera
     */
    private boolean checkCameraHardware() {
        return this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    @OnClick(R.id.activity_edit_id_calendar)
    public void onClickCalendar() {
        calendarId = R.id.activity_edit_id_calendar;
        DatePickerFragment datePickerFragment = new DatePickerFragment(this);
        datePickerFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @OnClick(R.id.activity_edit_id_calendar_sell)
    public void onClickCalendarSell() {
        calendarId = R.id.activity_edit_id_calendar_sell;
        DatePickerFragment datePickerFragment = new DatePickerFragment(this);
        datePickerFragment.show(getSupportFragmentManager(), "datePickerSell");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        if (calendarId == R.id.activity_edit_id_calendar) {
            viewHolder.tieEntryDate.setText(String.format("%s/%s/%s", month + 1, dayOfMonth, year));
        } else {
            viewHolder.tieSaleDate.setText(String.format("%s/%s/%s", month + 1, dayOfMonth, year));
        }
    }

    /**
     * Check if all fields are correctly filled in
     *
     * @return true if all ok
     */
    private boolean champNotEmpty() {
        return viewHolder.champNotEmpty(this, adapter.getItemCount(), agent, type, borough);
    }

    /**
     * Return error on fields
     *
     * @param message Message to show
     */
    @Override
    public void error(String message) {
        customToast(this, message);
    }

    /**
     * Property return if all fields are correctly filled in
     *
     * @param property Property created from fields
     */
    @Override
    public void property(Property property) {
        this.property = property;
    }

    /**
     * Insert or update property in the local database
     *
     * @return True if property is added
     */
    private boolean insertPropertyInDatabase() {
        if (propertyToUpdate == null) {
            property.setId(property.getHash());
        } else {
            property.setId(propertyToUpdate.getId());
        }
        if (!properties.contains(property)) {
            propertyViewModel.insertProperty(property);
            return true;
        } else if (propertyToUpdate != null) {
            if (propertyToUpdate.notEquals(property)) {
                dataHasBeenChanged = true;
                Calendar dateUpdate = Calendar.getInstance();
                property.setUpdateDate(dateUpdate.getTimeInMillis());
                propertyViewModel.updateProperty(property);
            }
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
                dataHasBeenChanged = true;
                propertyViewModel.insertPoiNextProperty(poiNextProperty);
            }
        }
        if (this.poisNextProperty != null && !this.poisNextProperty.isEmpty())
            for (PoiNextProperty poiNextProperty : poisNextProperty) {
                dataHasBeenChanged = true;
                propertyViewModel.deletePoiNextProperty(poiNextProperty);
            }
    }

    /**
     * Insert all photos in the local database and delete the old ones
     */
    private void insertPhotoInDatabase() {
        for (Photo photo : adapter.getPhotos()) {
            photo.setPropertyID(property.getId());
            if (propertyPhotos == null || !propertyPhotos.remove(photo)) {
                dataHasBeenChanged = true;
                propertyViewModel.insertPropertyPhoto(photo);
            }
        }
        if (this.propertyPhotos != null && !this.propertyPhotos.isEmpty()) {
            dataHasBeenChanged = true;
            for (Photo photo : propertyPhotos) {
                propertyViewModel.deletePhoto(photo);
                File fileToDelete = new File(photo.getName());
                fileToDelete.deleteOnExit();
            }
        }
    }

    @Override
    public void onClickDeletePhoto(Photo photo) {
        if (propertyToUpdate != null) {
            File photoToDelete = new File(photo.getName());
            photoToDelete.deleteOnExit();
        }
    }

    @Override
    public void onBackPressed() {
        this.finish();
        super.onBackPressed();
    }
}