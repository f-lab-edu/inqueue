package com.flab.inqueue.domain.queue.repository

import com.flab.inqueue.domain.queue.entity.Work
import org.springframework.data.repository.CrudRepository

interface QueueRedisRepository : CrudRepository<Work,String>