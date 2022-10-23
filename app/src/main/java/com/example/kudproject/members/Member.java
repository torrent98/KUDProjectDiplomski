package com.example.kudproject.members;

import android.os.Parcel;
import android.os.Parcelable;

public class Member implements Parcelable {

    private String key;
    private String ime;
    private String prezime;
    private String adresa;
    private String rank;

    public Member() {
    }

    protected Member(Parcel in) {
        key = in.readString();
        ime = in.readString();
        prezime = in.readString();
        adresa = in.readString();
        rank = in.readString();
    }

    public static final Creator<Member> CREATOR = new Creator<Member>() {
        @Override
        public Member createFromParcel(Parcel in) {
            return new Member(in);
        }

        @Override
        public Member[] newArray(int size) {
            return new Member[size];
        }
    };

    public Member(String key, String ime, String prezime, String adresa, String rank) {
        this.key = key;
        this.ime = ime;
        this.prezime = prezime;
        this.adresa = adresa;
        this.rank = rank;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(ime);
        dest.writeString(prezime);
        dest.writeString(adresa);
        dest.writeString(rank);
    }
}
