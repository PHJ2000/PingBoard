package com.pingboard.monitor.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.List;

public record UpdateMonitorRequest(
        @NotBlank String name,
        @NotBlank @Pattern(regexp = "https?://.+", message = "url must start with http:// or https://") String url,
        @Min(30) @Max(3600) Integer intervalSeconds,
        @NotBlank String environment,
        List<String> tags
) {
}
