package com.flab.inqueue.domain.queue.exception

import org.springframework.dao.DataAccessException

class RedisDataAccessException(message: String) : DataAccessException(message)