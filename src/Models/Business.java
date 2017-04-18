package Models;

import java.util.ArrayList;
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
        "name",
        "neighborhood",
        "address",
        "city",
        "state",
        "postal_code",
        "latitude",
        "longitude",
        "stars",
        "review_count",
        "is_open",
        "attributes",
        "categories",
        "hours",
        "type",
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
    @JsonProperty("name")
    private String name;
    @JsonProperty("neighborhood")
    private String neighborhood;
    @JsonProperty("address")
    private String address;
    @JsonProperty("city")
    private String city;
    @JsonProperty("state")
    private String state;
    @JsonProperty("postal_code")
    private String postalCode;
    @JsonProperty("latitude")
    private Double latitude;
    @JsonProperty("longitude")
    private Double longitude;
    @JsonProperty("stars")
    private Double stars;
    @JsonProperty("review_count")
    private Integer reviewCount;
    @JsonProperty("is_open")
    private Integer isOpen;
    @JsonProperty("attributes")
    private List<String> attributes = null;
    @JsonProperty("categories")
    private List<String> categories = null;
    @JsonProperty("hours")
    private List<String> hours = null;
    @JsonProperty("type")
    private String type;
    @JsonProperty("result_join")
    private List<ResultJoin> resultJoin = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    private ArrayList<String> attributeNames = new ArrayList<String>();

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

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("neighborhood")
    public String getNeighborhood() {
        return neighborhood;
    }

    @JsonProperty("neighborhood")
    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    @JsonProperty("address")
    public String getAddress() {
        return address;
    }

    @JsonProperty("address")
    public void setAddress(String address) {
        this.address = address;
    }

    @JsonProperty("city")
    public String getCity() {
        return city;
    }

    @JsonProperty("city")
    public void setCity(String city) {
        this.city = city;
    }

    @JsonProperty("state")
    public String getState() {
        return state;
    }

    @JsonProperty("state")
    public void setState(String state) {
        this.state = state;
    }

    @JsonProperty("postal_code")
    public String getPostalCode() {
        return postalCode;
    }

    @JsonProperty("postal_code")
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
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

    @JsonProperty("review_count")
    public Integer getReviewCount() {
        return reviewCount;
    }

    @JsonProperty("review_count")
    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

    @JsonProperty("is_open")
    public Integer getIsOpen() {
        return isOpen;
    }

    @JsonProperty("is_open")
    public void setIsOpen(Integer isOpen) {
        this.isOpen = isOpen;
    }

    @JsonProperty("attributes")
    public List<String> getAttributes() {
        return attributes;
    }

    @JsonProperty("attributes")
    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }

    @JsonProperty("categories")
    public List<String> getCategories() {
        return categories;
    }

    @JsonProperty("categories")
    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    @JsonProperty("hours")
    public List<String> getHours() {
        return hours;
    }

    @JsonProperty("hours")
    public void setHours(List<String> hours) {
        this.hours = hours;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
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

    public ArrayList<String> getAttributeNames(){
        return attributeNames;
    }

    public void setAttributeNames(ArrayList<String> attributeNames){
        this.attributeNames = attributeNames;
    }

}
