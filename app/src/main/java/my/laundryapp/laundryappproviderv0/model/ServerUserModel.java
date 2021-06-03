package my.laundryapp.laundryappproviderv0.model;

public class ServerUserModel {
    private String uid,name,phone,capital,email,laundryname;
    private boolean active;

    public ServerUserModel() {
    }

    public ServerUserModel(String uid, String name, String phone, String capital, String email, String laundryname, boolean active) {
        this.uid = uid;
        this.name = name;
        this.phone = phone;
        this.capital = capital;
        this.email = email;
        this.laundryname = laundryname;
        this.active = active;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLaundryname() {
        return laundryname;
    }

    public void setLaundryname(String laundryname) {
        this.laundryname = laundryname;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
