package swarm.swarmcomposer.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.util.ArrayList;

public class Product {


    private String name;
    private String organisation;
    private String version;
    private ArrayList<String> tags;
    private String picture;

    private Bitmap logo;

    private ArrayList<FormatVersion> formatInList;
    private ArrayList<FormatVersion> formatOutList;
    private long releaseDate;
    private int id;
    private boolean full = false;

    public Product() {
    }

    public Product(String name, String organisation, String version, long date, int id) {
        this.name = name;
        this.organisation = organisation;
        this.version = version;
        this.releaseDate = date;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }


    public long getDate() {
        return releaseDate;
    }

    public void setDate(long date) {
        this.releaseDate = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public boolean isFull() {
        return full;
    }

    public void setFull(boolean full) {
        this.full = full;
    }

    public ArrayList<String> getFormatIn() {
        ArrayList<String> returnList = new ArrayList<>();
        for (FormatVersion iter : formatInList) {
            returnList.add(iter.getFormat().getName() + " : " + iter.getName());
        }
        return returnList;
    }

    public ArrayList<String> getFormatOut() {
        ArrayList<String> returnList = new ArrayList<>();
        for (FormatVersion iter : formatOutList) {
            returnList.add(iter.getFormat().getName() + " : " + iter.getName());
        }
        return returnList;
    }

    public void setFormatIn(ArrayList<FormatVersion> formatIn) {
        this.formatInList = formatIn;
    }

    public void setFormatOut(ArrayList<FormatVersion> formatOut) {
        this.formatOutList = formatOut;
    }

    public Bitmap getLogo() {
        return logo;
    }

    public void setLogo(Bitmap logo) {
        this.logo = logo;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;

    }

    public void createLogo() {

        byte[] logoBytes = Base64.decode(picture, Base64.DEFAULT);
        logo = BitmapFactory.decodeByteArray(logoBytes, 0, logoBytes.length);
    }
}