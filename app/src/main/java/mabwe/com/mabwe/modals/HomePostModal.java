package mabwe.com.mabwe.modals;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mindiii on 12/7/18.
 */

public class HomePostModal implements Serializable {


    public List<Tags> tags;
    public String postId = "";
    public String title = "";
    public String video = "";
    public String video_thumb = "";
    public String description = "";
    public String category_id = "";
    public String Crd = "";
    public String Upd = "";
    public String CurrentTime = "";
    public String country = "";
    public String address = "";
    public String state = "";
    public String like_count = "";
    public String comment_count = "";
    public String post_attachment = "";
    public String tag_id = "";
    public String categoryName = "";
    public String name = "";
    public String tagName = "";
    public String user_like_status = "";
    public String authorisedToWork = "";
    public String willingToRelocate = "";
    public String whilingToship = "";
    public String email = "";
    public String contact = "";
    public String availability = "";
    public String isHired = "";
    public List<Tags> getTags() {
        return tags;
    }

    public void setTags(List<Tags> tags) {
        this.tags = tags;
    }
    public static class Tags{
        public String tag_id="";
        public String tagName="";

    }
}
