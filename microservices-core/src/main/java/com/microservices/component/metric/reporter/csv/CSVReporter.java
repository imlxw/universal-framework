package com.microservices.component.metric.reporter.csv;

import java.io.File;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.CsvReporter;
import com.codahale.metrics.MetricRegistry;
import com.microservices.Microservices;
import com.microservices.component.metric.MicroservicesMetricReporter;
import com.microservices.utils.StringUtils;

/**
 * @version V1.0
 * @Package com.microservices.component.metric.reporter.csv
 */
public class CSVReporter implements MicroservicesMetricReporter {

	@Override
	public void report(MetricRegistry metricRegistry) {

		MicroservicesMetricCVRReporterConfig cvrReporterConfig = Microservices.config(MicroservicesMetricCVRReporterConfig.class);

		if (StringUtils.isBlank(cvrReporterConfig.getPath())) {
			throw new NullPointerException("csv reporter path must not be null, please config microservices.metrics.reporter.cvr.path in you properties.");
		}

		final CsvReporter reporter = CsvReporter.forRegistry(metricRegistry).formatFor(Locale.US).convertRatesTo(TimeUnit.SECONDS).convertDurationsTo(TimeUnit.MILLISECONDS).build(new File(cvrReporterConfig.getPath()));

		reporter.start(1, TimeUnit.SECONDS);
	}
}