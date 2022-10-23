package com.example.kudproject.games;

import android.os.Parcel;
import android.os.Parcelable;

public class Games implements Parcelable {

    int gameID;
    String gameName;
    String gameRank;

    public Games() {
    }

    public Games(int gameID, String gameName, String gameRank) {
        this.gameID = gameID;
        this.gameName = gameName;
        this.gameRank = gameRank;
    }

    protected Games(Parcel in) {
        gameID = in.readInt();
        gameName = in.readString();
        gameRank = in.readString();
    }

    public static final Creator<Games> CREATOR = new Creator<Games>() {
        @Override
        public Games createFromParcel(Parcel in) {
            return new Games(in);
        }

        @Override
        public Games[] newArray(int size) {
            return new Games[size];
        }
    };

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getGameRank() {
        return gameRank;
    }

    public void getGameRank(String gameRank) {
        this.gameRank = gameRank;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(gameID);
        dest.writeString(gameName);
        dest.writeString(gameRank);

    }

    @Override
    public String toString() {
        return "Games{" +
                ", gameName='" + gameName + '\'' + '}';
    }
}
