package com.redspace.durations

import java.util.concurrent.TimeUnit

fun nanoseconds(ns: Long) = if (ns == 0L) zero else Duration(ns, TimeUnit.NANOSECONDS)
fun microseconds(us: Long) = if (us == 0L) zero else Duration(us, TimeUnit.MICROSECONDS)
fun milliseconds(ms: Long) = if (ms == 0L) zero else Duration(ms, TimeUnit.MILLISECONDS)
fun seconds(s: Long) = if (s == 0L) zero else Duration(s, TimeUnit.SECONDS)
fun minutes(m: Long) = if (m == 0L) zero else Duration(m, TimeUnit.MINUTES)
fun hours(h: Long) = if (h == 0L) zero else Duration(h, TimeUnit.HOURS)
fun days(d: Long) = if (d == 0L) zero else Duration(d, TimeUnit.DAYS)
val zero = Duration(0, TimeUnit.DAYS)

/** Represents a time duration.
 *
 * Each instance of this class represents a different unit for measuring time.  You can use durations interchangeably
 * as long as you don't care about their internal numerical value; to get a number out, use one of the type-specific
 * accessors.
 */
data class Duration internal constructor(
        private val duration: Long,
        private val unit: TimeUnit
) : Comparable<Duration> {

    val nanoseconds: Long
        get() = convert(::nanoseconds, TimeUnit.NANOSECONDS)
    val microseconds: Long
        get() = convert(::microseconds, TimeUnit.MICROSECONDS)
    val milliseconds: Long
        get() = convert(::milliseconds, TimeUnit.MILLISECONDS)
    val seconds: Long
        get() = convert(::seconds, TimeUnit.SECONDS)
    val minutes: Long
        get() = convert(::minutes, TimeUnit.MINUTES)
    val hours: Long
        get() = convert(::hours, TimeUnit.HOURS)
    val days: Long
        get() = convert(::days, TimeUnit.DAYS)

    private fun convert(durationWrapper: (Long) -> Duration, convertTo: TimeUnit): Long {
        return when {
            duration == 0L -> 0L
            unit == convertTo -> duration
            else -> convertTo.convert(duration, unit)
        }
    }

    private fun reducedOperands(other: Duration): Triple<Long, Long, TimeUnit> {
        val smallestUnit = if (unit.ordinal < other.unit.ordinal) this.unit else other.unit
        return Triple(
                smallestUnit.convert(duration, unit),
                smallestUnit.convert(other.duration, other.unit),
                smallestUnit)
    }

    /** Adds two durations together.
     *
     * Minimizes loss of precision by first scaling to the smaller of the two units.
     */
    operator fun plus(other: Duration): Duration {
        if (this == zero) return other
        if (other == zero) return this

        val (lhs, rhs, smallestUnit) = reducedOperands(other)

        val result = lhs + rhs
        if (result == 0L) return zero
        return Duration(result, smallestUnit)
    }

    /** Subtracts the other duration from this one.
     *
     * Minimizes loss of precision by first scaling to the smaller of the two units.
     */
    operator fun minus(other: Duration): Duration {
        if (this == zero) return -other
        if (other == zero) return this

        val (lhs, rhs, smallestUnit) = reducedOperands(other)

        val result = lhs - rhs
        if (result == 0L) return zero
        return Duration(result, smallestUnit)
    }

    operator fun unaryMinus(): Duration {
        if (this == zero) return this
        return Duration(-this.duration, this.unit)
    }

    override operator fun compareTo(other: Duration): Int {
        val (lhs, rhs, _) = reducedOperands(other)
        return lhs.compareTo(rhs)
    }
}

fun TimeUnit.toDuration(duration: Long) = if (duration == 0L) zero else Duration(duration, this)

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