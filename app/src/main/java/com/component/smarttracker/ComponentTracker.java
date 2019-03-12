package com.component.smarttracker;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

import static com.component.smarttracker.OptionsActivity.BORROWER;
import static com.component.smarttracker.OptionsActivity.LENDER;

public class ComponentTracker implements Parcelable {
    private String componentKey, componentName;
    private String ownerID;
    private String lender, lenderTeam, borrower, borrowerTeam;
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

    public ComponentTracker(String member, String team, String componetType) {

        if (LENDER.equalsIgnoreCase(componetType)) {
            this.lender = member;
            this.lenderTeam = team;
        }else if (BORROWER.equalsIgnoreCase(componetType)){
            this.borrower = member;
            this.borrowerTeam = team;
        }
    }

    protected ComponentTracker(Parcel in) {
        componentKey = in.readString();
        componentName = in.readString();
        ownerID = in.readString();
        lender = in.readString();
        lenderTeam = in.readString();
        borrower = in.readString();
        borrowerTeam = in.readString();
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
        dest.writeString(borrower);
        dest.writeString(borrowerTeam);
        dest.writeString(photoUrl);
        dest.writeString(type);
    }
}
