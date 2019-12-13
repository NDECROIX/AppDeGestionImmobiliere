package com.openclassrooms.realestatemanager.model;

import android.content.ContentValues;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.openclassrooms.realestatemanager.utils.Utils;

@Entity
public class Agent {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "first_name")
    private String firstName;

    @ColumnInfo(name = "last_name")
    private String lastName;

    @ColumnInfo(name = "email")
    private String email;

    @ColumnInfo(name = "phone")
    private String phone;

    public Agent(@NonNull String firstName, @NonNull String lastName, @NonNull String email, @NonNull String phone) {
        this.id = Utils.convertStringMd5(String.format("%s%s%s", firstName, lastName, email));
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }

    @Ignore
    public Agent() {

    }

    // --- UTILS ---

    public static Agent fromContentValues(ContentValues values) {
        final Agent agent = new Agent();
        if (values.containsKey("firstName")) agent.setFirstName(values.getAsString("firstName"));
        if (values.containsKey("lastName")) agent.setLastName(values.getAsString("lastName"));
        if (values.containsKey("email")) agent.setEmail(values.getAsString("email"));
        if (values.containsKey("phone")) agent.setPhone(values.getAsString("phone"));
        return agent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
