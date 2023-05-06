package com.flab.inqueue.domain.queue.scheduler

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

@Configuration
class ExecutorsConfiguration {

    // TODO: 추후 테스트를 통해 thread pool size 정함
    @Bean
    fun threadPoolTaskExecutor(): ThreadPoolTaskExecutor {
        val taskExecutor = ThreadPoolTaskExecutor()
        taskExecutor.corePoolSize = 10
        taskExecutor.maxPoolSize = 100
        taskExecutor.setAllowCoreThreadTimeOut(true)
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true)
        return taskExecutor
    }
}