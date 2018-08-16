package com.redspace.durations

import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldEqual
import org.junit.Test
import java.util.concurrent.TimeUnit

class DurationTests {

    @Test
    fun `Given two equal durations, when I compare them, then I expect a 0`() {
        // GIVEN
        val a = days(4)
        val b = a.milliseconds

        // WHEN
        val result = a.compareTo(b)

        // THEN
        result shouldEqual 0
    }

    @Test
    fun `Given a small duration, when I compare it to a large duration, then I expect a -1`() {
        // GIVEN
        val large = days(4)
        val small = hours(5)

        // WHEN
        val result = small.compareTo(large)

        // THEN
        result shouldEqual -1
    }

    @Test
    fun `Given a large duration, when I compare it to a small duration, then I expect a 1`() {
        // GIVEN
        val large = days(4)
        val small = hours(5)

        // WHEN
        val result = large.compareTo(small)

        // THEN
        result shouldEqual 1
    }

    @Test
    fun `Given a negative duration, when I convert, then I should not crash`() {
        // GIVEN
        val neg = milliseconds(-100)

        // WHEN
        val result = neg.microseconds

        // THEN
        result shouldEqual microseconds(TimeUnit.MILLISECONDS.toMicros(-100))
    }

    @Test
    fun `When I add smaller unit to larger unit, then I get smaller unit`() {
        // GIVEN
        val small = nanoseconds(10)
        val large = microseconds(10)

        // WHEN
        val result = small + large

        // THEN
        result shouldBeInstanceOf Duration.Nanoseconds::class
    }

    @Test
    fun `When I add larger unit to smaller unit, then I get smaller unit`() {
        // GIVEN
        val small = nanoseconds(10)
        val large = microseconds(10)

        // WHEN
        val result = large + small

        // THEN
        result shouldBeInstanceOf Duration.Nanoseconds::class
    }

    @Test
    fun `When I subtract smaller unit from larger unit, then I get smaller unit`() {
        // GIVEN
        val small = nanoseconds(10)
        val large = microseconds(10)

        // WHEN
        val result = small + large

        // THEN
        result shouldBeInstanceOf Duration.Nanoseconds::class
    }

    @Test
    fun `When I subtract larger unit from smaller unit, then I get smaller unit`() {
        // GIVEN
        val small = nanoseconds(10)
        val large = microseconds(10)

        // WHEN
        val result = large + small

        // THEN
        result shouldBeInstanceOf Duration.Nanoseconds::class
    }

    @Test
    fun `When I convert less than 1us to us, then I get 0`() {
        // GIVEN
        val ns = nanoseconds(1)

        // WHEN
        val us = ns.microseconds

        // THEN
        us.duration shouldEqual 0
    }

    @Test
    fun `When I convert less than 1ms to ms, then I get 0`() {
        // GIVEN
        val us = microseconds(1)

        // WHEN
        val ms = us.milliseconds

        // THEN
        ms.duration shouldEqual 0
    }

    @Test
    fun `When I convert less than 1s to s, then I get 0`() {
        // GIVEN
        val ms = milliseconds(1)

        // WHEN
        val s = ms.seconds

        // THEN
        s.duration shouldEqual 0
    }

    @Test
    fun `When I convert less than 1m to m, then I get 0`() {
        // GIVEN
        val s = seconds(1)

        // WHEN
        val m = s.minutes

        // THEN
        m.duration shouldEqual 0
    }

    @Test
    fun `When I convert less than 1h to h, then I get 0`() {
        // GIVEN
        val m = minutes(1)

        // WHEN
        val h = m.hours

        // THEN
        h.duration shouldEqual 0
    }

    @Test
    fun `When I convert less than 1d to d, then I get 0`() {
        // GIVEN
        val h = hours(1)

        // WHEN
        val d = h.days

        // THEN
        d.duration shouldEqual 0
    }

    @Test
    fun `When I compare same duration in different units, then they are not equal`() {
        // GIVEN
        val a = milliseconds(100)
        val b = a.nanoseconds

        // WHEN
        val r = a.equals(b)

        // THEN
        r.shouldBeFalse()
    }

    @Test
    fun `When I convert an Int, then my Duration is of the right Unit`() {
        // GIVEN
        val i = 10

        // WHEN
        val results = listOf(
                Pair(i.toNanoseconds(), TimeUnit.NANOSECONDS),
                Pair(i.toMicroseconds(), TimeUnit.MICROSECONDS),
                Pair(i.toMilliseconds(), TimeUnit.MILLISECONDS),
                Pair(i.toSeconds(), TimeUnit.SECONDS),
                Pair(i.toMinutes(), TimeUnit.MINUTES),
                Pair(i.toHours(), TimeUnit.HOURS),
                Pair(i.toDays(), TimeUnit.DAYS)
        )

        // THEN
        results.forEach { (duration, unit) -> duration.unit shouldEqual unit }
    }

    @Test
    fun `When I convert a Long, then my Duration is of the right Unit`() {
        // GIVEN
        val i = 10L

        // WHEN
        val results = listOf(
                Pair(i.toNanoseconds(), TimeUnit.NANOSECONDS),
                Pair(i.toMicroseconds(), TimeUnit.MICROSECONDS),
                Pair(i.toMilliseconds(), TimeUnit.MILLISECONDS),
                Pair(i.toSeconds(), TimeUnit.SECONDS),
                Pair(i.toMinutes(), TimeUnit.MINUTES),
                Pair(i.toHours(), TimeUnit.HOURS),
                Pair(i.toDays(), TimeUnit.DAYS)
        )

        // THEN
        results.forEach { (duration, unit) -> duration.unit shouldEqual unit }
    }

    @Test
    fun `When I create a Duration via TimeUnit, then my Duration is of the right Unit`() {
        // WHEN
        val results = listOf(
                Pair(TimeUnit.NANOSECONDS.toDuration(10), TimeUnit.NANOSECONDS),
                Pair(TimeUnit.MICROSECONDS.toDuration(10), TimeUnit.MICROSECONDS),
                Pair(TimeUnit.MILLISECONDS.toDuration(10), TimeUnit.MILLISECONDS),
                Pair(TimeUnit.SECONDS.toDuration(10), TimeUnit.SECONDS),
                Pair(TimeUnit.MINUTES.toDuration(10), TimeUnit.MINUTES),
                Pair(TimeUnit.HOURS.toDuration(10), TimeUnit.HOURS),
                Pair(TimeUnit.DAYS.toDuration(10), TimeUnit.DAYS)
        )

        // THEN
        results.forEach { (duration, unit) -> duration.unit shouldEqual unit }
    }

}