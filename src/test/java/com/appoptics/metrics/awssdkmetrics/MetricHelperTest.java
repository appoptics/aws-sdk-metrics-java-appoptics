package com.appoptics.metrics.awssdkmetrics;

import com.codahale.metrics.Timer;
import org.junit.Assert;
import org.junit.Test;

public class MetricHelperTest {

    @Test
    public void lookupHashTest() throws Exception {
        MetricHelper metricHelper = new MetricHelper();

        Timer t1 = metricHelper.getTimer("foobar.metric", new MetricTag("blah", "bar"));

        Timer t2 = metricHelper.getTimer("foobar.metric", new MetricTag("blah", "bar"));

        Assert.assertEquals(t1, t2);

        t2 = metricHelper.getTimer("foobar.metric", new MetricTag("bar", "blah"));

        Assert.assertNotEquals(t1, t2);

        //
        // Multiple tags and ordering
        //

        t1 = metricHelper.getTimer("foobar.metric",
                new MetricTag("region", "us-east-1"),
                new MetricTag("az", "b"));

        t2 = metricHelper.getTimer("foobar.metric",
                new MetricTag("az", "b"),
                new MetricTag("region", "us-east-1"));

        Assert.assertEquals(t1, t2);

        t2 = metricHelper.getTimer("foobar.metric",
                new MetricTag("az", "c"),
                new MetricTag("region", "us-east-1"));

        Assert.assertNotEquals(t1, t2);

        t2 = metricHelper.getTimer("foobar.metric",
                new MetricTag("region", "us-east-1"));

        Assert.assertNotEquals(t1, t2);
    }
}