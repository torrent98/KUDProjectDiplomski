package com.example.kudproject;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Performance implements Parcelable{

    private String key;
    private String naslov;
    private String datumOdrzavanja;
    private String lokacija;
    private String potrebanRank;
    private ArrayList<Member> memberList;
    private ArrayList<Games> gamesList;


    public Performance() {
    }

    public Performance(String key, String naslov, String datumOdrzavanja, String lokacija, String potrebanRank, ArrayList<Games> gamesList, ArrayList<Member> memberList) {
        this.key = key;
        this.naslov = naslov;
        this.datumOdrzavanja = datumOdrzavanja;
        this.lokacija = lokacija;
        this.potrebanRank = potrebanRank;
        this.gamesList = gamesList;
        this.memberList = memberList;
    }

    protected Performance(Parcel in) {
        key = in.readString();
        naslov = in.readString();
        datumOdrzavanja = in.readString();
        lokacija = in.readString();
        potrebanRank = in.readString();
        gamesList = in.readArrayList(Games.class.getClassLoader());
        memberList = in.readArrayList(Member.class.getClassLoader());

    }

    public static final Creator<Performance> CREATOR = new Creator<Performance>() {
        @Override
        public Performance createFromParcel(Parcel in) {
            return new Performance(in);
        }

        @Override
        public Performance[] newArray(int size) {
            return new Performance[size];
        }
    };

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getNaslov() {
        return naslov;
    }

    public void setNaslov(String naslov) {
        this.naslov = naslov;
    }

    public String getDatumOdrzavanja() {
        return datumOdrzavanja;
    }

    public void setDatumOdrzavanja(String datumOdrzavanja) {
        this.datumOdrzavanja = datumOdrzavanja;
    }

    public String getLokacija() {
        return lokacija;
    }

    public void setLokacija(String lokacija) {
        this.lokacija = lokacija;
    }

    public String getPotrebanRank() {
        return potrebanRank;
    }

    public void setPotrebanRank(String potrebanRank) {
        this.potrebanRank = potrebanRank;
    }

    public ArrayList<Member> getMemberList() {
        return memberList;
    }

    public void setMemberList(ArrayList<Member> memberList) {
        this.memberList = memberList;
    }

    public ArrayList<Games> getGamesList() {
        return gamesList;
    }

    public void setGamesList(ArrayList<Games> gamesList) {
        this.gamesList = gamesList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(naslov);
        dest.writeString(datumOdrzavanja);
        dest.writeString(lokacija);
        dest.writeString(potrebanRank);
        dest.writeList(gamesList);
        dest.writeList(memberList);
    }

}
