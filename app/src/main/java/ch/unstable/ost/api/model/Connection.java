package ch.unstable.ost.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.base.MoreObjects;

import java.util.Arrays;
import java.util.Date;

import ch.unstable.ost.utils.ParcelUtils;

import static ch.unstable.ost.utils.ObjectsCompat.requireNonNull;

public class Connection implements Parcelable {

    public static final Creator<Connection> CREATOR = new Creator<Connection>() {
        @Override
        public Connection createFromParcel(Parcel in) {
            return new Connection(in);
        }

        @Override
        public Connection[] newArray(int size) {
            return new Connection[size];
        }
    };
    private final Section[] sections;

    public Connection(Section[] sections) {
        this.sections = requireNonNull(sections, "sections");
    }

    protected Connection(Parcel in) {
        sections = in.createTypedArray(Section.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedArray(sections, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Section[] getSections() {
        return Arrays.copyOf(sections, sections.length);
    }

    public Date getDepartureDate() {
        return sections[0].getDepartureDate();
    }

    public Date getArrivalDate() {
        return sections[sections.length - 1].getArrivalDate();
    }


    public DepartureCheckpoint getDeparture() {
        return sections[0].getDeparture();
    }

    public ArrivalCheckpoint getArrival() {
        return sections[sections.length - 1].getArrival();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("sections", sections)
                .toString();
    }
}
