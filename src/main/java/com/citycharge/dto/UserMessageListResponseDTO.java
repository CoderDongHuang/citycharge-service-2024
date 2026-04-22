package com.citycharge.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class UserMessageListResponseDTO {
    private List<UserMessageDTO> content;
    private Pagination pagination;
    private Statistics statistics;
    
    @Data
    public static class Pagination {
        private Integer currentPage;
        private Integer pageSize;
        private Long totalElements;
        private Integer totalPages;
    }
    
    @Data
    public static class Statistics {
        private Long totalCount;
        private Long unreadCount;
        private Map<String, CategoryStats> byCategory;
    }
    
    @Data
    public static class CategoryStats {
        private Long total;
        private Long unread;
    }
}
