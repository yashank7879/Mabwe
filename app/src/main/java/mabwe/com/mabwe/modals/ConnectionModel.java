package mabwe.com.mabwe.modals;

public class ConnectionModel {

    String connectionId;
    String requestBy;
    String requestFor;
    String fullname;
    String email;
    String main_image_url;
    String thumb_image_url;
    String profileImage;
    String country;
    String userId;
    String defaultImage ="";
private String requested_by_country ="";


    public String getRequested_by_country() {
        return requested_by_country;
    }

    public void setRequested_by_country(String requested_by_country) {
        this.requested_by_country = requested_by_country;
    }


    public String getDefaultImage() {
        return defaultImage;
    }

    public void setDefaultImage(String defaultImage) {
        this.defaultImage = defaultImage;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }



    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getConnectionId() {

        return connectionId;
    }

    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }

    public String getRequestBy() {
        return requestBy;
    }

    public void setRequestBy(String requestBy) {
        this.requestBy = requestBy;
    }

    public String getRequestFor() {
        return requestFor;
    }

    public void setRequestFor(String requestFor) {
        this.requestFor = requestFor;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMain_image_url() {
        return main_image_url;
    }

    public void setMain_image_url(String main_image_url) {
        this.main_image_url = main_image_url;
    }

    public String getThumb_image_url() {
        return thumb_image_url;
    }

    public void setThumb_image_url(String thumb_image_url) {
        this.thumb_image_url = thumb_image_url;
    }
}
