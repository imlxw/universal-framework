package com.microservices.component.redis;

import java.util.HashSet;
import java.util.Set;

import com.microservices.config.annotation.PropertyConfig;
import com.microservices.utils.StringUtils;

import redis.clients.jedis.HostAndPort;

@PropertyConfig(prefix = "microservices.redis")
public class MicroservicesRedisConfig {

	public static final String TYPE_JEDIS = "jedis";
	public static final String TYPE_REDISSON = "redisson";
	public static final String TYPE_LETTUCE = "lettuce";

	private String host;
	private Integer port = 6379;
	private Integer timeout = 2000;
	private String password;
	private Integer database;
	private String clientName;
	private Boolean testOnCreate;
	private Boolean testOnBorrow;
	private Boolean testOnReturn;
	private Boolean testWhileIdle;
	private Long minEvictableIdleTimeMillis;
	private Long timeBetweenEvictionRunsMillis;
	private Integer numTestsPerEvictionRun;
	private Integer maxAttempts;
	private String type = TYPE_JEDIS;
	private Integer maxTotal;
	private Integer maxIdle;
	private Integer minIdle;
	private Integer maxWaitMillis;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getDatabase() {
		return database;
	}

	public void setDatabase(Integer database) {
		this.database = database;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public Boolean getTestOnCreate() {
		return testOnCreate;
	}

	public void setTestOnCreate(Boolean testOnCreate) {
		this.testOnCreate = testOnCreate;
	}

	public Boolean getTestOnBorrow() {
		return testOnBorrow;
	}

	public void setTestOnBorrow(Boolean testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
	}

	public Boolean getTestOnReturn() {
		return testOnReturn;
	}

	public void setTestOnReturn(Boolean testOnReturn) {
		this.testOnReturn = testOnReturn;
	}

	public Boolean getTestWhileIdle() {
		return testWhileIdle;
	}

	public void setTestWhileIdle(Boolean testWhileIdle) {
		this.testWhileIdle = testWhileIdle;
	}

	public Long getMinEvictableIdleTimeMillis() {
		return minEvictableIdleTimeMillis;
	}

	public void setMinEvictableIdleTimeMillis(Long minEvictableIdleTimeMillis) {
		this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
	}

	public Long getTimeBetweenEvictionRunsMillis() {
		return timeBetweenEvictionRunsMillis;
	}

	public void setTimeBetweenEvictionRunsMillis(Long timeBetweenEvictionRunsMillis) {
		this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
	}

	public Integer getNumTestsPerEvictionRun() {
		return numTestsPerEvictionRun;
	}

	public void setNumTestsPerEvictionRun(Integer numTestsPerEvictionRun) {
		this.numTestsPerEvictionRun = numTestsPerEvictionRun;
	}

	public Integer getMaxAttempts() {
		return maxAttempts;
	}

	public void setMaxAttempts(Integer maxAttempts) {
		this.maxAttempts = maxAttempts;
	}

	public boolean isCluster() {
		return host != null && host.indexOf(",") > 0;
	}

	public boolean isConfigOk() {
		return StringUtils.isNotBlank(host);
	}

	public boolean isClusterConfig() {
		return isConfigOk() && host.contains(",");
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getMaxTotal() {
		return maxTotal;
	}

	public void setMaxTotal(Integer maxTotal) {
		this.maxTotal = maxTotal;
	}

	public Integer getMaxIdle() {
		return maxIdle;
	}

	public void setMaxIdle(Integer maxIdle) {
		this.maxIdle = maxIdle;
	}

	public Integer getMinIdle() {
		return minIdle;
	}

	public void setMinIdle(Integer minIdle) {
		this.minIdle = minIdle;
	}

	public Integer getMaxWaitMillis() {
		return maxWaitMillis;
	}

	public void setMaxWaitMillis(Integer maxWaitMillis) {
		this.maxWaitMillis = maxWaitMillis;
	}

	public Set<HostAndPort> getHostAndPorts() {
		Set<HostAndPort> haps = new HashSet<>();
		String[] hostAndPortStrings = host.split(",");
		for (String hostAndPortString : hostAndPortStrings) {
			String[] hostAndPorts = hostAndPortString.split(":");

			HostAndPort hap = new HostAndPort(hostAndPorts[0], Integer.valueOf(hostAndPorts[1]));
			haps.add(hap);
		}

		return haps;
	}
}
