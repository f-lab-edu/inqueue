package com.flab.inqueue.domain.event.entity

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class WaitQueuePeriodTest {

    @DisplayName("현재 시간이 시작 시간과 종료 시간 사이에 있다.")
    @Test
    fun currentTimeContainsBetweenStartDateTimeAndEndDateTime() {
        // given
        val currentDateTime = LocalDateTime.of(2023, 8, 3, 10, 0, 0)

        val startDateTime = LocalDateTime.of(2023, 8, 3, 10, 0, 0)
        val endDateTime = LocalDateTime.of(2023, 8, 3, 12, 0, 0)
        val waitQueuePeriod = WaitQueuePeriod(startDateTime, endDateTime)

        // when
        val result = waitQueuePeriod.contains(currentDateTime)

        // then
        assertThat(result).isTrue
    }

    @DisplayName("현재 시간이 시작시간 이전이다.")
    @Test
    fun currentTimeBeforeStartDateTime() {
        // given
        val currentDateTime = LocalDateTime.of(2023, 8, 3, 9, 59, 59)

        val startDateTime = LocalDateTime.of(2023, 8, 3, 10, 0, 0)
        val endDateTime = LocalDateTime.of(2023, 8, 3, 12, 0, 0)
        val waitQueuePeriod = WaitQueuePeriod(startDateTime, endDateTime)

        // when
        val result = waitQueuePeriod.contains(currentDateTime)

        // then
        assertThat(result).isFalse
    }

    @DisplayName("현재 시간이 종료 시간 이후이다.")
    @Test
    fun currentTimeAfterEndDateTime() {
        // given
        val currentDateTime = LocalDateTime.of(2023, 8, 3, 12, 0, 1)

        val startDateTime = LocalDateTime.of(2023, 8, 3, 10, 0, 0)
        val endDateTime = LocalDateTime.of(2023, 8, 3, 12, 0, 0)
        val waitQueuePeriod = WaitQueuePeriod(startDateTime, endDateTime)

        // when
        val result = waitQueuePeriod.contains(currentDateTime)

        // then
        assertThat(result).isFalse
    }

}