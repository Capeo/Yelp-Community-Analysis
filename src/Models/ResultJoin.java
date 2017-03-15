package Models;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "_id",
        "review_id",
        "user_id",
        "business_id",
        "stars",
        "useful"
})

/**
 * Created by oddca on 15/03/2017.
 */
public class ResultJoin {
    @JsonProperty("_id")
    private Id_ id;
    @JsonProperty("review_id")
    private String reviewId;
    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("business_id")
    private String businessId;
    @JsonProperty("stars")
    private Integer stars;
    @JsonProperty("useful")
    private Integer useful;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("_id")
    public Id_ getId() {
        return id;
    }

    @JsonProperty("_id")
    public void setId(Id_ id) {
        this.id = id;
    }

    @JsonProperty("review_id")
    public String getReviewId() {
        return reviewId;
    }

    @JsonProperty("review_id")
    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    @JsonProperty("user_id")
    public String getUserId() {
        return userId;
    }

    @JsonProperty("user_id")
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @JsonProperty("business_id")
    public String getBusinessId() {
        return businessId;
    }

    @JsonProperty("business_id")
    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    @JsonProperty("stars")
    public Integer getStars() {
        return stars;
    }

    @JsonProperty("stars")
    public void setStars(Integer stars) {
        this.stars = stars;
    }

    @JsonProperty("useful")
    public Integer getUseful() {
        return useful;
    }

    @JsonProperty("useful")
    public void setUseful(Integer useful) {
        this.useful = useful;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
