package com.flab.inqueue.domain.queue.repository

import com.flab.inqueue.domain.queue.entity.Job
import org.springframework.data.repository.CrudRepository

interface QueueRedisRepository : CrudRepository<Job, String>