package com.appoptics.awssdkmetrics;

import com.amazonaws.metrics.*;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class AppopticsServiceMetricCollector extends ServiceMetricCollector {
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
            double durationNano = provider.getDurationNano();
            Double bytesPerSec = bytesPerSecond(byteCount, durationNano);

            Histogram h = metricHelper.getHistogram(throughputType.name(),
                    MetricTag.awsServiceTag(throughputType.getServiceName()));

            h.update(bytesPerSec.longValue());
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

    /**
     * Returns the number of bytes per second, given the byte count and
     * duration in nano seconds.  Duration of zero nanosecond will be treated
     * as 1 nanosecond.
     */
    private double bytesPerSecond(double byteCount, double durationNano) {
        if (byteCount < 0 || durationNano < 0)
            throw new IllegalArgumentException();
        if (durationNano == 0) {
            durationNano = 1.0;   // defend against division by zero
        }
        return (byteCount / durationNano) * NANO_PER_SEC;
    }
}
