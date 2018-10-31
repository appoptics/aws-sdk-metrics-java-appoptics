package com.appoptics.metrics.awssdkmetrics;

import com.appoptics.metrics.client.Tag;
import com.appoptics.metrics.reporter.Appoptics;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;
import org.mpierce.metrics.reservoir.hdrhistogram.HdrHistogramResetOnSnapshotReservoir;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

class MetricHelper {
    private final ConcurrentHashMap<MetricKey, Histogram> histograms;
    private final ConcurrentHashMap<MetricKey, Timer> timers;
    private final ConcurrentHashMap<MetricKey, Meter> meters;

    MetricHelper() {
        this.histograms = new ConcurrentHashMap<>();
        this.timers = new ConcurrentHashMap<>();
        this.meters = new ConcurrentHashMap<>();
    }

    Timer getTimer(String name, MetricTag... tags) {
        List<MetricTag> tagList = new ArrayList<>(tags.length);

        Collections.addAll(tagList, tags);

        return getTimer(name, tagList);
    }

    Timer getTimer(String name, List<MetricTag> tags) {
        MetricKey key = makeKey(name, tags);

        return timers.computeIfAbsent(key, this::makeTimer);
    }

    Histogram getHistogram(String name, MetricTag... tags) {
        List<MetricTag> tagList = new ArrayList<>(tags.length);

        Collections.addAll(tagList, tags);

        return getHistogram(name, tagList);
    }

    Histogram getHistogram(String name, List<MetricTag> tags) {
        MetricKey key = makeKey(name, tags);

        return histograms.computeIfAbsent(key, this::makeHistogram);
    }

    Meter getMeter(String name, MetricTag... tags) {
        List<MetricTag> tagList = new ArrayList<>(tags.length);

        Collections.addAll(tagList, tags);

        return getMeter(name, tagList);
    }

    Meter getMeter(String name, List<MetricTag> tags) {
        MetricKey key = makeKey(name, tags);

        return meters.computeIfAbsent(key, this::makeMeter);
    }

    private MetricKey makeKey(String name, List<MetricTag> tags) {
        return new MetricKey(name, tags);
    }

    private Timer makeTimer(MetricKey k) {
        return makeMetric(k).timer(new Timer(new HdrHistogramResetOnSnapshotReservoir()));
    }

    private Histogram makeHistogram(MetricKey k) {
        return makeMetric(k).histogram(new Histogram(new HdrHistogramResetOnSnapshotReservoir()));
    }

    private Meter makeMeter(MetricKey k) {
        return makeMetric(k).meter();
    }

    private Appoptics makeMetric(MetricKey k) {
        String name = String.format("aws-sdk.%s", k.name);

        Appoptics metric = Appoptics.metric(name);
        if (k.tags.isEmpty()) {
            return metric;
        }

        List<Tag> tags = k.tags.stream()
                .sorted(Comparator.comparing(t -> t.name))
                .map(t -> new Tag(t.name, t.value.replace(" ", "")))
                .collect(Collectors.toList());

        return metric.tags(tags);
    }
}
