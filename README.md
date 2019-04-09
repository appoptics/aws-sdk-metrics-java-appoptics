# Java AWS SDK Metrics Reporter for AppOptics

Reports [AWS SDK
metrics](https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/generating-sdk-metrics.html)
to [AppOptics](https://appoptics.com). Useful to debug application
performance for any Java app that interacts with AWS APIs via the AWS SDK. The
[AWS tuning guide](https://aws.amazon.com/blogs/developer/tuning-the-aws-sdk-for-java-to-improve-resiliency/) is
a good reference for how to interpret these metrics and improve the performance of your application.

![AWS SDK Dashboard](https://github.com/appoptics/aws-sdk-metrics-java-appoptics/blob/master/awssdk_dashboard.png "AWS SDK Dashboard")


Metrics reported:

* ClientExecuteTime
* Exception counts
* HttpClientGetConnectionTime
* HttpClientPoolAvailableCount
* HttpClientPoolLeasedCount
* HttpClientReceiveResponseTime
* HttpClientSendRequestTime
* HttpRequestTime
* RequestCount
* RetryCapacityConsumed
* RetryCount

Plus additional service-specific metrics (eg. `S3UploadThroughput` and 
`S3UploadByteCount`). Metrics are tagged with the following values when
applicable:

* aws_service (amazons3, amazonsqs, etc)
* request_type (putobjectrequest, sendmessagebatchrequest, etc)


# Dependency Setup

Add the following to your pom.xml in your Java project that uses the AWS
SDK.

```xml
<dependency>
  <groupId>com.appoptics.metrics</groupId>
  <artifactId>aws-sdk-metrics-appoptics</artifactId>
  <version>1.0.3</version>
</dependency>
```

This library depends on the
[metrics-appoptics](https://github.com/appoptics/metrics-appoptics)
reporter to publish Dropwizard metrics to AppOptics. Make sure to include the 
[latest version](https://search.maven.org/search?q=g:com.appoptics.metrics%20AND%20a:metrics-appoptics&core=gav) 
in your project.

```xml
<dependency>
  <groupId>com.appoptics.metrics</groupId>
  <artifactId>metrics-appoptics</artifactId>
  <version>1.0.2</version>
</dependency>
```

The following example is the basic initialization required for starting the 
metrics-appoptics reporter with your AppOptics API token. Follow the instructions 
at [metrics-appoptics](https://github.com/appoptics/metrics-appoptics) for complete
configuration options. The 
[dropwizard-metrics-appoptics](https://github.com/appoptics/dropwizard-metrics-appoptics) 
package also provides simple integration for Dropwizard projects.

```java
MetricRegistry registry = environment.metrics();
Appoptics.reporter(registry, "<AppOptics API Token>")
    .addTag("tier", "web")
    .addTag("environment", "staging")
    .start(10, TimeUnit.SECONDS);
```

Similarly, you need to include the `aws-java-sdk-cloudwatchmetrics`
package to get the `SdkMetrics` support. Make sure to include the version
that matches your current AWS SDK version.

```xml
<dependency>
  <groupId>com.amazonaws</groupId>
  <artifactId>aws-java-sdk-cloudwatchmetrics</artifactId>
</dependency>
```

# Enabling

In your application startup, add the AppOptics MetricsCollector to
your application:

```java
AwsSdkMetrics.setMetricCollector(new AppopticsMetricCollector());
```

That should be all you need to start reporting request and service
metrics from AWS SDK calls to your AppOptics account.
