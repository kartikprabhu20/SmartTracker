package com.component.smarttracker;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

import static com.component.smarttracker.MainActivity.SMART_TRACKER;
import static com.component.smarttracker.MainActivity.USER_EMAIL;
import static com.component.smarttracker.MainActivity.USER_UID;
import static com.component.smarttracker.OptionsActivity.BORROWER;
import static com.component.smarttracker.OptionsActivity.LENDER;

public class ComponentTracker implements Parcelable {
    private String componentKey, componentName;
    private String ownerID;
    private String lender, lenderTeam,lenderEmail, borrower, borrowerTeam, borrowerEmail;
    private String photoUrl;
    private String type;

    public ComponentTracker(){
    }

    @Override
    public String toString() {
        return "ComponentTracker{" +
                "componentKey='" + componentKey + '\'' +
                ", componentName='" + componentName + '\'' +
                ", ownerID='" + ownerID + '\'' +
                ", lender='" + lender + '\'' +
                ", lenderTeam='" + lenderTeam + '\'' +
                ", borrower='" + borrower + '\'' +
                ", borrowerTeam='" + borrowerTeam + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    public ComponentTracker(Context context,String owner, String ownerTeam, String componentType) {

        this.ownerID = context.getSharedPreferences(SMART_TRACKER, Context.MODE_PRIVATE).getString(USER_UID, "UNKNOWN");
        if (LENDER.equalsIgnoreCase(componentType)) {
            this.lender = owner;
            this.lenderTeam = ownerTeam;
            this.lenderEmail = context.getSharedPreferences(SMART_TRACKER, Context.MODE_PRIVATE).getString(USER_EMAIL, "UNKNOWN");
        }else if (BORROWER.equalsIgnoreCase(componentType)){
            this.borrower = owner;
            this.borrowerTeam = ownerTeam;
            this.borrowerEmail = context.getSharedPreferences(SMART_TRACKER, Context.MODE_PRIVATE).getString(USER_EMAIL, "UNKNOWN");
        }

    }

    protected ComponentTracker(Parcel in) {
        componentKey = in.readString();
        componentName = in.readString();
        ownerID = in.readString();
        lender = in.readString();
        lenderTeam = in.readString();
        lenderEmail = in.readString();
        borrower = in.readString();
        borrowerTeam = in.readString();
        borrowerEmail = in.readString();
        photoUrl = in.readString();
        type = in.readString();
    }

    public static final Creator<ComponentTracker> CREATOR = new Creator<ComponentTracker>() {
        @Override
        public ComponentTracker createFromParcel(Parcel in) {
            return new ComponentTracker(in);
        }

        @Override
        public ComponentTracker[] newArray(int size) {
            return new ComponentTracker[size];
        }
    };

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public String getComponentKey() {
        return componentKey;
    }

    public void setComponentKey(String componentKey) {
        this.componentKey = componentKey;
    }

    public String getLender() {
        return lender;
    }

    public void setLender(String lender) {
        this.lender = lender;
    }

    public String getLenderTeam() {
        return lenderTeam;
    }

    public void setLenderTeam(String lenderTeam) {
        this.lenderTeam = lenderTeam;
    }

    public String getBorrower() {
        return borrower;
    }

    public void setBorrower(String borrower) {
        this.borrower = borrower;
    }

    public String getBorrowerTeam() {
        return borrowerTeam;
    }

    public void setBorrowerTeam(String borrowerTeam) {
        this.borrowerTeam = borrowerTeam;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setComponentType(String componentType) {
        type = componentType;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComponentTracker that = (ComponentTracker) o;
        return Objects.equals(componentKey, that.componentKey) &&
                Objects.equals(componentName, that.componentName) &&
                Objects.equals(ownerID, that.ownerID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(componentKey, componentName, ownerID);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(componentKey);
        dest.writeString(componentName);
        dest.writeString(ownerID);
        dest.writeString(lender);
        dest.writeString(lenderTeam);
        dest.writeString(lenderEmail);
        dest.writeString(borrower);
        dest.writeString(borrowerTeam);
        dest.writeString(borrowerEmail);
        dest.writeString(photoUrl);
        dest.writeString(type);
    }

    public String getLenderEmail() {
        return lenderEmail;
    }

    public void setLenderEmail(String lenderEmail) {
        this.lenderEmail = lenderEmail;
    }

    public String getBorrowerEmail() {
        return borrowerEmail;
    }

    public void setBorrowerEmail(String borrowerEmail) {
        this.borrowerEmail = borrowerEmail;
    }
}
