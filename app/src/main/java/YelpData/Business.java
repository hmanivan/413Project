package YelpData;

import java.util.List;

public class Business {
    public String display_phone;
    public String id;
    public String image_url;
    public String mobile_url;
    public String name;
    public String phone;
    public String rating_img_url;
    public String rating_img_url_large;
    public String rating_img_url_small;
    public String snippet_image_url;
    public String snippet_text;
    public String url;

    public Boolean is_claimed;
    public Boolean is_closed;

    public float rating;
    public int review_count;

    public List<Deal> deals;
    public List<List<String>> categories;

    public Location location;

    public double distanceToUser;
}

