package com.kedzie.vbox.metrics;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Metric data queried from server
 */
public class MetricQuery implements Parcelable {
	
	public static Parcelable.Creator<MetricQuery> CREATOR = new Parcelable.Creator<MetricQuery>() {
        @Override
        public MetricQuery createFromParcel(Parcel source) {
            MetricQuery q = new MetricQuery();
            q.name=source.readString();
            q.unit=source.readString();
            q.scale=source.readInt();
            q.object=source.readString();
            q.values=source.createIntArray();
            return q;
        }
        @Override
        public MetricQuery[] newArray(int size) {
            return new MetricQuery[size];
        }
	};
	
	public String name;
    public String unit;
    public int scale;
    public String object;
    public int[] values;
	
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(unit);
        dest.writeInt(scale);
        dest.writeString(object);
        dest.writeIntArray(values);
    }
    
    public String toString() {
        return "Metric Query - "+name+" - "+unit+" - "+scale+" - "+object+" - # values: " + values.length;
    }
}