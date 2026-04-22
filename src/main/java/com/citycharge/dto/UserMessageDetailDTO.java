package com.citycharge.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class UserMessageDetailDTO {
    private Long id;
    private String title;
    private String content;
    private String category;
    private String source;
    private Integer priority;
    private Boolean isRead;
    private LocalDateTime createTime;
    private DisplayData displayData;
    private String extraData;
    
    @Data
    public static class DisplayData {
        private String icon;
        private String categoryText;
        private List<Highlight> highlights;
        private List<Action> actions;
    }
    
    @Data
    public static class Highlight {
        private String label;
        private String value;
        private String color;
    }
    
    @Data
    public static class Action {
        private String text;
        private String type;
        private String url;
        private Long stationId;
        private String action;
    }
}
