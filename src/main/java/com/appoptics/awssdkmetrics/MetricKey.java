package com.appoptics.awssdkmetrics;


import java.util.List;
import java.util.Objects;

public class MetricKey {
    public final String name;
    public final List<MetricTag> tags;

    public MetricKey(String name, List<MetricTag> tags) {
        this.name = name;
        this.tags = tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MetricKey)) return false;
        MetricKey that = (MetricKey) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(tags, that.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, tags);
    }
}
