# Java AWS SDK Metrics Reporter for AppOptics

Reports [AWS SDK
metrics](https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/generating-sdk-metrics.html)
to [AppOptics](https://appoptics.com). Useful to debug application
performance for any Java app that interacts with AWS via the AWS SDK.

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

Plus additional service-specific metrics. All metrics are tagged with
the AWS service name and service operation name (if applicable).


# Dependency Setup

Add the following to your pom.xml in your Java project that uses the AWS
SDK.

```
<dependency>
  <groupId>com.appoptics.metrics</groupId>
  <artifactId>aws-sdk-metrics-appoptics</artifactId>
  <version>1.0.3</version>
</dependency>
```

This library depends on the
[metrics-appoptics](https://github.com/appoptics/metrics-appoptics)
reporter, ensure you have included that in your pom.xml. Follow the
instructions at
[metrics-appoptics](https://github.com/appoptics/metrics-appoptics) to
configure the credentials pointed to your AppOptics account.

```
<dependency>
  <groupId>com.appoptics.metrics</groupId>
  <artifactId>metrics-appoptics</artifactId>
  <version>1.0.2</version>
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
