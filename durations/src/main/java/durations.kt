package com.redspace.durations

import java.util.concurrent.TimeUnit

fun nanoseconds(ns: Long) = if (ns == 0L) Duration.Nanoseconds.Zero else Duration.Nanoseconds(ns)
fun microseconds(us: Long) = if (us == 0L) Duration.Microseconds.Zero else Duration.Microseconds(us)
fun milliseconds(ms: Long) = if (ms == 0L) Duration.Milliseconds.Zero else Duration.Milliseconds(ms)
fun seconds(s: Long) = if (s == 0L) Duration.Seconds.Zero else Duration.Seconds(s)
fun minutes(m: Long) = if (m == 0L) Duration.Minutes.Zero else Duration.Minutes(m)
fun hours(h: Long) = if (h == 0L) Duration.Hours.Zero else Duration.Hours(h)
fun days(d: Long) = if (d == 0L) Duration.Days.Zero else Duration.Days(d)
val zero = Duration.Zero

private fun unitConstructor(unit: TimeUnit): (Long) -> Duration = when (unit) {
    TimeUnit.NANOSECONDS -> ::nanoseconds
    TimeUnit.MICROSECONDS -> ::microseconds
    TimeUnit.MILLISECONDS -> ::milliseconds
    TimeUnit.SECONDS -> ::seconds
    TimeUnit.MINUTES -> ::minutes
    TimeUnit.HOURS -> ::hours
    TimeUnit.DAYS -> ::days
}

/** Represents a time duration.
 *
 * Each instance of this class represents a different unit for measuring time.  You can use durations interchangeably
 * as long as you don't care about their internal numerical value; to get a number out, use one of the type-specific
 * accessors.
 */
sealed class Duration(
        private val duration: Long,
        val unit: TimeUnit,
        private val order: Int
) : Comparable<Duration> {
    open class Nanoseconds internal constructor(nanoseconds: Long) : Duration(nanoseconds, TimeUnit.NANOSECONDS, 0) {
        val value: Long get() = super.duration
        override val nanoseconds get() = this
        object Zero : Nanoseconds(0)
    }

    open class Microseconds internal constructor(microseconds: Long) : Duration(microseconds, TimeUnit.MICROSECONDS, 1) {
        val value: Long get() = super.duration
        override val microseconds get() = this
        object Zero : Microseconds(0)
    }

    open class Milliseconds internal constructor(milliseconds: Long) : Duration(milliseconds, TimeUnit.MILLISECONDS, 2) {
        val value: Long get() = super.duration
        override val milliseconds get() = this
        object Zero : Milliseconds(0)
    }

    open class Seconds internal constructor(seconds: Long) : Duration(seconds, TimeUnit.SECONDS, 3) {
        val value: Long get() = super.duration
        override val seconds get() = this
        object Zero : Seconds(0)
    }

    open class Minutes internal constructor(minutes: Long) : Duration(minutes, TimeUnit.MINUTES, 4) {
        val value: Long get() = super.duration
        override val minutes get() = this
        object Zero : Minutes(0)
    }

    open class Hours internal constructor(hours: Long) : Duration(hours, TimeUnit.HOURS, 5) {
        val value: Long get() = super.duration
        override val hours get() = this
        object Zero : Hours(0)
    }

    open class Days internal constructor(days: Long) : Duration(days, TimeUnit.DAYS, 6) {
        val value: Long get() = super.duration
        override val days get() = this
        object Zero : Days(0)
    }

    /** Represents the special duration 'zero'.
     *
     * This duration is the same for every unit and is specially optimized because of its commonality.
     */
    object Zero : Duration(0, TimeUnit.NANOSECONDS, 0) {
        override val nanoseconds get() = Nanoseconds.Zero
        override val microseconds get() = Microseconds.Zero
        override val milliseconds get() = Milliseconds.Zero
        override val seconds get() = Seconds.Zero
        override val minutes get() = Minutes.Zero
        override val hours get() = Hours.Zero
        override val days get() = Days.Zero
        override fun toString() = "duration IZero"

        override fun equals(other: Any?) =
                if (other is Duration && other.duration == 0L) true
                else super.equals(other)
    }

    open val nanoseconds: Duration.Nanoseconds
        get() = nanoseconds(unit.toNanos(duration))
    open val microseconds: Duration.Microseconds
        get() = microseconds(unit.toMicros(duration))
    open val milliseconds: Duration.Milliseconds
        get() = milliseconds(unit.toMillis(duration))
    open val seconds: Duration.Seconds
        get() = seconds(unit.toSeconds(duration))
    open val minutes: Duration.Minutes
        get() = minutes(unit.toMinutes(duration))
    open val hours: Duration.Hours
        get() = hours(unit.toHours(duration))
    open val days: Duration.Days
        get() = days(unit.toDays(duration))

    private fun toUnit(unit: TimeUnit): Duration {
        return if (unit == this.unit) this
        else when (unit) {
            TimeUnit.NANOSECONDS -> nanoseconds
            TimeUnit.MICROSECONDS -> microseconds
            TimeUnit.MILLISECONDS -> milliseconds
            TimeUnit.SECONDS -> seconds
            TimeUnit.MINUTES -> minutes
            TimeUnit.HOURS -> hours
            TimeUnit.DAYS -> days
        }
    }

    private fun reducedOperands(other: Duration): Triple<Duration, Duration, TimeUnit> {
        val smallestUnit = if (order < other.order) this.unit else other.unit
        return Triple(toUnit(smallestUnit), other.toUnit(smallestUnit), smallestUnit)
    }

    /** Adds two durations together.
     *
     * Minimizes loss of precision by first scaling to the smaller of the two units.
     */
    operator fun plus(other: Duration): Duration {
        val (lhs, rhs, smallestUnit) = reducedOperands(other)
        return unitConstructor(smallestUnit)(lhs.duration + rhs.duration)
    }

    /** Subtracts the other duration from this one.
     *
     * Minimizes loss of precision by first scaling to the smaller of the two units.
     */
    operator fun minus(other: Duration): Duration {
        val (lhs, rhs, smallestUnit) = reducedOperands(other)
        return unitConstructor(smallestUnit)(lhs.duration - rhs.duration)
    }

    override operator fun compareTo(other: Duration): Int {
        val (lhs, rhs, _) = reducedOperands(other)
        return lhs.duration.compareTo(rhs.duration)
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Duration) {
            return false
        }

        return duration == other.duration && unit == other.unit
    }

    override fun hashCode(): Int {
        var result = 17
        result = 31 * result + (duration xor (duration shr 32)).toInt()
        result = 31 * result + unit.hashCode()
        return result
    }

    override fun toString(): String {
        return "${this.duration} ${this.unit.name.toLowerCase().capitalize()}"
    }
}

fun TimeUnit.toDuration(duration: Long) = unitConstructor(this).invoke(duration)

fun Long.toNanoseconds() = nanoseconds(this)
fun Long.toMicroseconds() = microseconds(this)
fun Long.toMilliseconds() = milliseconds(this)
fun Long.toSeconds() = seconds(this)
fun Long.toMinutes() = minutes(this)
fun Long.toHours() = hours(this)
fun Long.toDays() = days(this)

fun Int.toNanoseconds() = this.toLong().toNanoseconds()
fun Int.toMicroseconds() = this.toLong().toMicroseconds()
fun Int.toMilliseconds() = this.toLong().toMilliseconds()
fun Int.toSeconds() = this.toLong().toSeconds()
fun Int.toMinutes() = this.toLong().toMinutes()
fun Int.toHours() = this.toLong().toHours()
fun Int.toDays() = this.toLong().toDays()