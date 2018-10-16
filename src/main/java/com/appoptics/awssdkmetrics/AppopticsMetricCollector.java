package com.appoptics.awssdkmetrics;

import com.amazonaws.metrics.MetricCollector;
import com.amazonaws.metrics.RequestMetricCollector;
import com.amazonaws.metrics.ServiceMetricCollector;

public class AppopticsMetricCollector extends MetricCollector {
    private final RequestMetricCollector requestMetricCollector;
    private final ServiceMetricCollector serviceMetricCollector;

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
    public RequestMetricCollector getRequestMetricCollector() {
        return requestMetricCollector;
    }

    @Override
    public ServiceMetricCollector getServiceMetricCollector() {
        return serviceMetricCollector;
    }
}
