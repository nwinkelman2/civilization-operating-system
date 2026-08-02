package io.github.opencivilizationplatform.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApiVersionRequestCondition implements RequestCondition<ApiVersionRequestCondition> {

    private static final Pattern VERSION_PATTERN = Pattern.compile("/api/v(\\d+)/");
    private final String apiVersion;

    public ApiVersionRequestCondition(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    @Override
    public ApiVersionRequestCondition combine(ApiVersionRequestCondition other) {
        return new ApiVersionRequestCondition(other.apiVersion);
    }

    @Override
    public ApiVersionRequestCondition getMatchingCondition(HttpServletRequest request) {
        Matcher matcher = VERSION_PATTERN.matcher(request.getRequestURI());
        if (matcher.find()) {
            String requestVersion = "v" + matcher.group(1);
            if (requestVersion.equals(apiVersion)) {
                return this;
            }
        }
        return null;
    }

    @Override
    public int compareTo(ApiVersionRequestCondition other, HttpServletRequest request) {
        return other.apiVersion.compareTo(this.apiVersion);
    }
}
