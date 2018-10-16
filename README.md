# Java AWS SDK Metrics Reporter for AppOptics

Reports [AWS SDK
metrics](https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/generating-sdk-metrics.html)
to [AppOptics](https://appoptics.com). Useful to debug application
performance for any Java app that interacts with AWS via the AWS SDK.

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

Plus additional service-specific metrics. All metrics are tagged with
the AWS service name and service operation name (if applicable).


# Dependency Setup

Add the following to your pom.xml in your Java project that uses the AWS
SDK:

```
<dependency>
  <groupId>com.appoptics</groupId>
  <artifactId>aws-sdk-metrics-appoptics</artifactId>
  <version>1.0.1</version>
</dependency>
```

This library depends on the
[metrics-librato](https://github.com/librato/metrics-librato)
reporter, ensure you have included that in your pom.xml. Follow the
instructions at
[metrics-librato](https://github.com/librato/metrics-librato) to
configure the credentials pointed to your AppOptics account.

```
<dependency>
  <groupId>com.librato.metrics</groupId>
  <artifactId>metrics-librato</artifactId>
  <version>5.1.3</version>
</dependency>
```

Similarly, you need to include the `aws-java-sdk-cloudwatchmetrics`
package to get the `SdkMetrics` support, so include the version
matching your current AWS SDK version:

```
<dependency>
  <groupId>com.amazonaws</groupId>
  <artifactId>aws-java-sdk-cloudwatchmetrics</artifactId>
</dependency>
```

# Enabling

In your application startup, add the AppOptics MetricsCollector to
your application:

```
AwsSdkMetrics.setMetricCollector(new AppopticsMetricCollector());
```

That should be all you need to start reporting request and service
metrics from AWS SDK calls to your AppOptics account.
