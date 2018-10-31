package com.appoptics.metrics.awssdkmetrics;

import com.amazonaws.metrics.*;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class AppopticsServiceMetricCollector extends ServiceMetricCollector {
    private static final Logger log = LoggerFactory.getLogger(AppopticsServiceMetricCollector.class);

    private static final double NANO_PER_SEC = TimeUnit.SECONDS.toNanos(1);

    private final MetricHelper metricHelper;

    public AppopticsServiceMetricCollector(MetricHelper metricHelper) {
        this.metricHelper = metricHelper;
    }

    @Override
    public void collectByteThroughput(ByteThroughputProvider provider) {
        final ThroughputMetricType throughputType = provider.getThroughputMetricType();
        final ServiceMetricType byteCountType = throughputType.getByteCountMetricType();
        final Set<MetricType> metrics = AwsSdkMetrics.getPredefinedMetrics();
        final double byteCount = provider.getByteCount();

        if (metrics.contains(throughputType)) {
            try {
                double durationNano = provider.getDurationNano();
                Double bytesPerSec = bytesPerSecond(byteCount, durationNano);

                Histogram h = metricHelper.getHistogram(throughputType.name(),
                        MetricTag.awsServiceTag(throughputType.getServiceName()));

                h.update(bytesPerSec.longValue());
            } catch (IllegalArgumentException e) {
                log.warn("Got invalid byte count or duration for {}", throughputType.name());
            }
        }

        if (metrics.contains(byteCountType)) {
            Meter m = metricHelper.getMeter(byteCountType.name(),
                    MetricTag.awsServiceTag(byteCountType.getServiceName()));

            m.mark(provider.getByteCount());
        }
    }

    @Override
    public void collectLatency(ServiceLatencyProvider provider) {
        final ServiceMetricType type = provider.getServiceMetricType();
        final Set<MetricType> metrics = AwsSdkMetrics.getPredefinedMetrics();

        if (metrics.contains(type)) {
            Double duration = provider.getDurationMilli();

            Timer t = metricHelper.getTimer(type.name(),
                    MetricTag.awsServiceTag(type.getServiceName()));

            t.update(duration.longValue(), TimeUnit.MILLISECONDS);
        }
    }

    // duration less than 1.0 is converted to 1 nanosec
    private double bytesPerSecond(double byteCount, double durationNano) {
        if (byteCount < 0 || durationNano < 0)
            throw new IllegalArgumentException();
        if (durationNano < 1.0) {
            durationNano = 1.0;
        }
        return (byteCount / durationNano) * NANO_PER_SEC;
    }
}
