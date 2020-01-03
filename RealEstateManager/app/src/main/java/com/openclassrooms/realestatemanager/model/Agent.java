package com.openclassrooms.realestatemanager.model;

import android.content.ContentValues;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.openclassrooms.realestatemanager.utils.Utils;

import java.util.Objects;

/**
 * Representation of an agent
 */
@Entity
public class Agent {

    // unique id create by first name, last name and email in the constructor
    @SuppressWarnings("NullableProblems")
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    // Agent first name
    @ColumnInfo(name = "first_name")
    private String firstName;

    // Agent last name
    @ColumnInfo(name = "last_name")
    private String lastName;

    // Agent email
    @ColumnInfo(name = "email")
    private String email;

    // Agent phone number
    @ColumnInfo(name = "phone")
    private String phone;

    // Date of his update
    @ColumnInfo(name = "update_date")
    private long updateDate;

    /**
     * Constructor
     *
     * @param firstName Agent first name
     * @param lastName  Agent last name
     * @param email     Agent email
     * @param phone     Agent phone number
     */
    public Agent(@NonNull String firstName, @NonNull String lastName, @NonNull String email, @NonNull String phone) {
        this.id = Utils.convertStringMd5(String.format("%s%s%s", firstName, lastName, email));
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.updateDate = 0;
    }

    @Ignore
    public Agent() {

    }

    /**
     * Create the id if the empty constructor has been used.
     * Id can be null if created by the empty constructor
     */
    @SuppressWarnings("ConstantConditions")
    public void createId() {
        if (this.id != null && !this.id.isEmpty()) {
            return;
        }
        this.id = Utils.convertStringMd5(String.format("%s%s%s", firstName, lastName, email));
    }

    // Content provider
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

    public long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(long updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Agent agent = (Agent) o;
        return id.equals(agent.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
