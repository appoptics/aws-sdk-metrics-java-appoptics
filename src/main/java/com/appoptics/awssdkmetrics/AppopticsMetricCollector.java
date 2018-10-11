package com.appoptics.awssdkmetrics;

import com.amazonaws.metrics.MetricCollector;

public class AppopticsMetricCollector extends MetricCollector {
    private final com.amazonaws.metrics.RequestMetricCollector requestMetricCollector;
    private final com.amazonaws.metrics.ServiceMetricCollector serviceMetricCollector;

    public AppopticsMetricCollector() {
        MetricHelper metricHelper = new MetricHelper();

        requestMetricCollector = new AppopticsRequestMetricCollector(metricHelper);
        serviceMetricCollector = new AppopticsServiceMetricCollector(metricHelper);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean start() {
        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }

    @Override
    public com.amazonaws.metrics.RequestMetricCollector getRequestMetricCollector() {
        return requestMetricCollector;
    }

    @Override
    public com.amazonaws.metrics.ServiceMetricCollector getServiceMetricCollector() {
        return serviceMetricCollector;
    }
}
