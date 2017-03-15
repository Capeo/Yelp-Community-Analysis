package Models;

import java.util.HashMap;
import java.util.List;
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
        "business_id",
        "city",
        "latitude",
        "longitude",
        "stars",
        "result_join"
})

/**
 * Created by oddca on 15/03/2017.
 */
public class Business {

    @JsonProperty("_id")
    private Id id;
    @JsonProperty("business_id")
    private String businessId;
    @JsonProperty("city")
    private String city;
    @JsonProperty("latitude")
    private Double latitude;
    @JsonProperty("longitude")
    private Double longitude;
    @JsonProperty("stars")
    private Double stars;
    @JsonProperty("result_join")
    private List<ResultJoin> resultJoin = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("_id")
    public Id getId() {
        return id;
    }

    @JsonProperty("_id")
    public void setId(Id id) {
        this.id = id;
    }

    @JsonProperty("business_id")
    public String getBusinessId() {
        return businessId;
    }

    @JsonProperty("business_id")
    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    @JsonProperty("city")
    public String getCity() {
        return city;
    }

    @JsonProperty("city")
    public void setCity(String city) {
        this.city = city;
    }

    @JsonProperty("latitude")
    public Double getLatitude() {
        return latitude;
    }

    @JsonProperty("latitude")
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    @JsonProperty("longitude")
    public Double getLongitude() {
        return longitude;
    }

    @JsonProperty("longitude")
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @JsonProperty("stars")
    public Double getStars() {
        return stars;
    }

    @JsonProperty("stars")
    public void setStars(Double stars) {
        this.stars = stars;
    }

    @JsonProperty("result_join")
    public List<ResultJoin> getResultJoin() {
        return resultJoin;
    }

    @JsonProperty("result_join")
    public void setResultJoin(List<ResultJoin> resultJoin) {
        this.resultJoin = resultJoin;
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
