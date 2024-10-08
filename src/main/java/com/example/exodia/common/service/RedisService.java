package com.example.exodia.common.service;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class RedisService {

	private final RedisTemplate<String, Object> redisTemplate;
	private final RedisTemplate<String, Object> docViewdRedisTemplate;
	private final RedisTemplate<String, Object> docUpdatedRedisTemplate;
	private static final int MAX_LIST_SIZE = 50;
	private static final String RESERVATION_LOCK_PREFIX = "reservation:lock:";

	@Autowired
	public RedisService(
		RedisTemplate<String, Object> redisTemplate,
		@Qualifier("7") RedisTemplate<String, Object> docViewdRedisTemplate,
		@Qualifier("8") RedisTemplate<String, Object> docUpdatedRedisTemplate) {
		this.redisTemplate = redisTemplate;
		this.docViewdRedisTemplate = docViewdRedisTemplate;
		this.docUpdatedRedisTemplate = docUpdatedRedisTemplate;
	}

	public void setValues(String key, String data, Duration duration) {
		ValueOperations<String, Object> values = redisTemplate.opsForValue();
		values.set(key, data, duration);
	}

	@Transactional(readOnly = true)
	public String getValues(String key) {
		ValueOperations<String, Object> values = redisTemplate.opsForValue();
		if (values.get(key) == null) {
			return "false";
		}
		return (String)values.get(key);
	}

	public void deleteValues(String key) {
		redisTemplate.delete(key);
	}

	public boolean checkExistsValue(String value) {
		// 조회하려는 데이터가 존재하는지
		return !value.equals("false");
	}

	public void setViewdListValue(String key, Long value) {
		ListOperations<String, Object> listValues = docViewdRedisTemplate.opsForList();
		listValues.leftPush(key, value);
		trimListToMaxSize(docViewdRedisTemplate, key);

	}

	public List<Object> getViewdListValue(String key) {
		ListOperations<String, Object> listOps = docViewdRedisTemplate.opsForList();
		Long size = listOps.size(key);
		if (size == null || size == 0) {
			return Collections.emptyList();
		}
		return listOps.range(key, 0, size - 1);
	}

	public void removeViewdListValue(String key, Long value) {
		ListOperations<String, Object> listOps = docViewdRedisTemplate.opsForList();
		Long size = listOps.size(key);

		for (long i = 0; i < size; i++) {
			Object listValue = listOps.index(key, i);
			if (listValue != null && listValue.equals(value.intValue())) {
				listOps.remove(key, 1, value);
				break;
			}
		}
	}

	public void setUpdatedListValue(String key, Long value) {
		ListOperations<String, Object> listValues = docUpdatedRedisTemplate.opsForList();
		listValues.leftPush(key, value);
		trimListToMaxSize(docUpdatedRedisTemplate, key);
	}

	public List<Object> getUpdatedListValue(String key) {
		ListOperations<String, Object> listOps = docUpdatedRedisTemplate.opsForList();
		Long size = listOps.size(key);
		if (size == null || size == 0) {
			return Collections.emptyList();
		}
		return listOps.range(key, 0, size - 1);
	}

	public void removeUpdatedListValue(String key, Long value) {
		ListOperations<String, Object> listOps = docUpdatedRedisTemplate.opsForList();
		Long size = listOps.size(key);

		for (long i = 0; i < size; i++) {
			Object listValue = listOps.index(key, i);
			if (listValue != null && listValue.equals(value.intValue())) {
				listOps.remove(key, 1, value);
				break;
			}
		}
	}

	private void trimListToMaxSize(RedisTemplate<String, Object> redisTemplate, String key) {
		ListOperations<String, Object> listOps = redisTemplate.opsForList();
		Long size = listOps.size(key);

		if (size != null && size > MAX_LIST_SIZE) {
			listOps.trim(key, 0, MAX_LIST_SIZE - 1);
		}
	}
}

