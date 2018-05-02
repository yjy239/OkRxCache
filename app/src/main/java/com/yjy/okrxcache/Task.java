package com.yjy.okrxcache;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zcj on 17-5-9.
 */
public class Task implements Parcelable {
    public String title;//type
    public String task_no ;
    public int status;//如:1001
    public String status_name;
    public String content;
    public String created;
    public long appointment_start_time;//ms
    public long appointment_end_time;//ms
    public String route;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.task_no);
        dest.writeInt(this.status);
        dest.writeString(this.status_name);
        dest.writeString(this.content);
        dest.writeString(this.created);
        dest.writeString(this.title);
        dest.writeLong(appointment_start_time);
        dest.writeLong(appointment_end_time);
        dest.writeString(route);
    }

    public Task() {
    }

    protected Task(Parcel in) {
        this.task_no = in.readString();
        this.status = in.readInt();
        this.status_name = in.readString();
        this.content = in.readString();
        this.created = in.readString();
        this.title=in.readString();
        this.appointment_start_time=in.readLong();
        this.appointment_end_time=in.readLong();
        this.route=in.readString();
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel source) {
            return new Task(source);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };
}
