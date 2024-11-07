package kafka;

import com.kafkaui.models.JsonFilter;
import org.json.JSONObject;

import java.util.List;

public class KafkaFilterOption {


    public static int TEXT_FILTER = 1;
    public static int JSON_FILTER = 2;
    public static int JSON_OBJECT_FILTER = 3;

    private String searchText = "";
    private int filterType;
    private List<JsonFilter> jsonFilters;

    private JSONObject jsonObjectFilters;


    public KafkaFilterOption(String searchText, int filterType, List<JsonFilter> jsonFilters) {
        this.searchText = searchText;
        this.filterType = filterType;
        this.jsonFilters = jsonFilters;
    }

    public KafkaFilterOption(String searchText, int filterType, JSONObject jsonObjectFilters) {
        this.searchText = searchText;
        this.filterType = filterType;
        this.jsonObjectFilters = jsonObjectFilters;
    }

    public static int getTextFilter() {
        return TEXT_FILTER;
    }

    public static void setTextFilter(int textFilter) {
        TEXT_FILTER = textFilter;
    }

    public static int getJsonFilter() {
        return JSON_FILTER;
    }

    public static void setJsonFilter(int jsonFilter) {
        JSON_FILTER = jsonFilter;
    }

    public static int getJsonObjectFilter() {
        return JSON_OBJECT_FILTER;
    }

    public static void setJsonObjectFilter(int jsonObjectFilter) {
        JSON_OBJECT_FILTER = jsonObjectFilter;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public int getFilterType() {
        return filterType;
    }

    public void setFilterType(int filterType) {
        this.filterType = filterType;
    }

    public List<JsonFilter> getJsonFilters() {
        return jsonFilters;
    }

    public void setJsonFilters(List<JsonFilter> jsonFilters) {
        this.jsonFilters = jsonFilters;
    }

    public JSONObject getJsonObjectFilters() {
        return jsonObjectFilters;
    }

    public void setJsonObjectFilters(JSONObject jsonObjectFilters) {
        this.jsonObjectFilters = jsonObjectFilters;
    }
}
