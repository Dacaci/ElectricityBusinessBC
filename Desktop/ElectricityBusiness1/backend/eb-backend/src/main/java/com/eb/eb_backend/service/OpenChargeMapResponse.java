package com.eb.eb_backend.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class OpenChargeMapResponse {
    @JsonProperty("value")
    private List<Map<String, Object>> value;
    
    @JsonProperty("Count")
    private Integer count;
}

