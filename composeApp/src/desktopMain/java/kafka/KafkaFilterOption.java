package kafka;

import com.kafkaui.models.JsonFilter;

import java.util.List;

public class KafkaFilterOption {

     private String searchText = "";
    private   boolean isJsonFilter = false;
    private List<JsonFilter> filters;

    public KafkaFilterOption(String searchText, boolean isJsonFilter, List<JsonFilter> filters) {
        this.searchText = searchText;
        this.isJsonFilter = isJsonFilter;
        this.filters = filters;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public boolean isJsonFilter() {
        return isJsonFilter;
    }

    public void setJsonFilter(boolean jsonFilter) {
        isJsonFilter = jsonFilter;
    }

    public List<JsonFilter> getFilters() {
        return filters;
    }

    public void setFilters(List<JsonFilter> filters) {
        this.filters = filters;
    }
}
