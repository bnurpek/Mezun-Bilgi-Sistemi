package com.example.mbs.database;

public class User {
    private String profilePhotoUrl;
    private String uID;
    private String email, phone;
    private String fullName;
    private String yüksek, doktora, comp, country, city;
    private String start, startYüksek, startDoktora;
    private String graduate, gradYüksek, gradDoktora;
    private boolean phoneStatue, mailStatue;

    public User(){}

    public User(String profilePhotoUrl, String uID, String email, String fullName,
                String start, String graduate) {
        this.profilePhotoUrl = profilePhotoUrl;
        this.uID = uID;             this.email = email;
        this.start = start;         this.graduate = graduate;
        this.fullName = fullName;
        mailStatue = false;       phoneStatue = false;
        this.yüksek = "";         this.doktora = "";
        this.comp = "";           this.country = "";
        this.city = "";           this.startYüksek = "";
        this.gradYüksek = "";     this.startDoktora = "";
        this.gradDoktora = "";
    }

    public User(String profilePhotoUrl, String uID, String email, String fullName,
                String yüksek, String doktora, String comp, String country, String city,
                String start, String startYüksek, String startDoktora, String graduate,
                String gradYüksek, String gradDoktora, String phone, boolean tel, boolean mail) {
        this.profilePhotoUrl = profilePhotoUrl;
        this.uID = uID;             this.email = email;
        this.fullName = fullName;   this.mailStatue = mail;
        this.yüksek = yüksek;       this.doktora = doktora;
        this.comp = comp;           this.country = country;
        this.city = city;           this.start = start;
        this.startYüksek = startYüksek;
        this.startDoktora = startDoktora;
        this.graduate = graduate;   this.gradYüksek = gradYüksek;
        this.gradDoktora = gradDoktora;
        this.phone = phone;         this.phoneStatue = tel;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getYüksek() {
        return yüksek;
    }

    public void setYüksek(String yüksek) {
        this.yüksek = yüksek;
    }

    public String getDoktora() {
        return doktora;
    }

    public void setDoktora(String doktora) {
        this.doktora = doktora;
    }

    public String getComp() {
        return comp;
    }

    public void setComp(String comp) {
        this.comp = comp;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStartYüksek() {
        return startYüksek;
    }

    public void setStartYüksek(String startYüksek) {
        this.startYüksek = startYüksek;
    }

    public String getStartDoktora() {
        return startDoktora;
    }

    public void setStartDoktora(String startDoktora) {
        this.startDoktora = startDoktora;
    }

    public String getGradYüksek() {
        return gradYüksek;
    }

    public void setGradYüksek(String gradYüksek) {
        this.gradYüksek = gradYüksek;
    }

    public String getGradDoktora() {
        return gradDoktora;
    }

    public void setGradDoktora(String gradDoktora) {
        this.gradDoktora = gradDoktora;
    }
    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public String getuID() { return uID; }

    public void setuID(String uID) { this.uID = uID; }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getGraduate() {
        return graduate;
    }

    public void setGraduate(String graduate) {
        this.graduate = graduate;
    }

    public boolean getPhoneStatue() {
        return phoneStatue;
    }

    public void setPhoneStatue(boolean phoneStatue) {
        this.phoneStatue = phoneStatue;
    }

    public boolean getMailStatue() {
        return mailStatue;
    }

    public void setMailStatue(boolean mailStatue) {
        this.mailStatue = mailStatue;
    }
}
