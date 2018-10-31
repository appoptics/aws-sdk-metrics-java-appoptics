package com.appoptics.metrics.awssdkmetrics;

import java.util.Objects;

public class MetricTag {
    public static final String awsServiceTag = "aws_service";

    public final String name;
    public final String value;

    public MetricTag(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static MetricTag awsServiceTag(String service) {
        return new MetricTag(awsServiceTag, service);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MetricTag)) return false;
        MetricTag that = (MetricTag) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }
}
