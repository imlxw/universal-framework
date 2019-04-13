package com.microservices.schedule;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.cron4j.Cron4jPlugin;
import com.microservices.Microservices;
import com.microservices.exception.MicroservicesException;
import com.microservices.schedule.annotation.Cron;
import com.microservices.schedule.annotation.EnableDistributedRunnable;
import com.microservices.schedule.annotation.FixedDelay;
import com.microservices.schedule.annotation.FixedRate;
import com.microservices.utils.ClassKits;
import com.microservices.utils.ClassScanner;

import it.sauronsoftware.cron4j.ProcessTask;
import it.sauronsoftware.cron4j.Task;

public class MicroservicesScheduleManager {

	private static final Log LOG = Log.getLog(MicroservicesScheduleManager.class);

	private static MicroservicesScheduleManager manager;
	private Cron4jPlugin cron4jPlugin;
	private ScheduledThreadPoolExecutor fixedScheduler;
	private MicroservicesScheduleConfig config;

	public MicroservicesScheduleManager() {
		config = Microservices.config(MicroservicesScheduleConfig.class);
		fixedScheduler = new ScheduledThreadPoolExecutor(config.getPoolSize());

		Prop prop = null;
		try {
			prop = PropKit.use(config.getCron4jFile());
		} catch (Throwable ex) {
		}

		cron4jPlugin = prop == null ? new Cron4jPlugin() : new Cron4jPlugin(prop);
	}

	public static final MicroservicesScheduleManager me() {
		if (manager == null) {
			manager = ClassKits.singleton(MicroservicesScheduleManager.class);
		}
		return manager;
	}

	public void init() {
		initScheduledThreadPoolExecutor();
		initCron4jPlugin();
		cron4jPlugin.start();
	}

	private void initScheduledThreadPoolExecutor() {
		List<Class> fixedDelayClasses = ClassScanner.scanClassByAnnotation(FixedDelay.class, true);
		for (Class clazz : fixedDelayClasses) {
			if (!Runnable.class.isAssignableFrom(clazz)) {
				throw new RuntimeException(clazz.getName() + " must implements Runnable");
			}
			FixedDelay fixedDelayJob = (FixedDelay) clazz.getAnnotation(FixedDelay.class);
			Runnable runnable = (Runnable) ClassKits.newInstance(clazz);
			Runnable executeRunnable = clazz.getAnnotation(EnableDistributedRunnable.class) == null ? runnable : new MicroservicesDistributedRunnable(runnable, fixedDelayJob.period());
			try {
				fixedScheduler.scheduleWithFixedDelay(executeRunnable, fixedDelayJob.initialDelay(), fixedDelayJob.period(), TimeUnit.SECONDS);
			} catch (Exception e) {
				LOG.error(e.toString(), e);
			}
		}

		List<Class> fixedRateClasses = ClassScanner.scanClassByAnnotation(FixedRate.class, true);
		for (Class clazz : fixedRateClasses) {
			if (!Runnable.class.isAssignableFrom(clazz)) {
				throw new RuntimeException(clazz.getName() + " must implements Runnable");
			}
			FixedRate fixedDelayJob = (FixedRate) clazz.getAnnotation(FixedRate.class);
			Runnable runnable = (Runnable) ClassKits.newInstance(clazz);
			Runnable executeRunnable = clazz.getAnnotation(EnableDistributedRunnable.class) == null ? runnable : new MicroservicesDistributedRunnable(runnable, fixedDelayJob.period());
			try {
				fixedScheduler.scheduleAtFixedRate(executeRunnable, fixedDelayJob.initialDelay(), fixedDelayJob.period(), TimeUnit.SECONDS);
			} catch (Exception e) {
				LOG.error(e.toString(), e);
			}
		}
	}

	private void initCron4jPlugin() {
		List<Class> cronClasses = ClassScanner.scanClassByAnnotation(Cron.class, true);
		for (Class clazz : cronClasses) {
			Cron cron = (Cron) clazz.getAnnotation(Cron.class);
			if (Runnable.class.isAssignableFrom(clazz)) {
				Runnable runnable = (Runnable) ClassKits.newInstance(clazz);
				Runnable executeRunnable = clazz.getAnnotation(EnableDistributedRunnable.class) == null ? runnable : new MicroservicesDistributedRunnable(runnable);
				cron4jPlugin.addTask(cron.value(), executeRunnable, cron.daemon());
			} else if (ProcessTask.class.isAssignableFrom(clazz)) {
				cron4jPlugin.addTask(cron.value(), (ProcessTask) ClassKits.newInstance(clazz), cron.daemon());
			} else if (Task.class.isAssignableFrom(clazz)) {
				cron4jPlugin.addTask(cron.value(), (Task) ClassKits.newInstance(clazz), cron.daemon());
			} else {
				throw new MicroservicesException("annotation Cron can not use for class : " + clazz);
			}
		}
	}

}
