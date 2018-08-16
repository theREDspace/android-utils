import java.util.concurrent.TimeUnit

fun nanoseconds(ns: Long) = Duration.Nanoseconds(ns)
fun microseconds(us: Long) = Duration.Microseconds(us)
fun milliseconds(ms: Long) = Duration.Milliseconds(ms)
fun seconds(s: Long) = Duration.Seconds(s)
fun minutes(m: Long) = Duration.Minutes(m)
fun hours(h: Long) = Duration.Hours(h)
fun days(d: Long) = Duration.Days(d)

private fun unitConstructor(unit: TimeUnit): (Long) -> Duration = when (unit) {
    TimeUnit.NANOSECONDS -> ::nanoseconds
    TimeUnit.MICROSECONDS -> ::microseconds
    TimeUnit.MILLISECONDS -> ::milliseconds
    TimeUnit.SECONDS -> ::seconds
    TimeUnit.MINUTES -> ::minutes
    TimeUnit.HOURS -> ::hours
    TimeUnit.DAYS -> ::days
}

sealed class Duration(
        val duration: Long,
        val unit: TimeUnit,
        private val order: Int
) : Comparable<Duration> {
    class Nanoseconds(nanoseconds: Long) : Duration(nanoseconds, TimeUnit.NANOSECONDS, 0)
    class Microseconds(microseconds: Long) : Duration(microseconds, TimeUnit.MICROSECONDS, 1)
    class Milliseconds(milliseconds: Long) : Duration(milliseconds, TimeUnit.MILLISECONDS, 2)
    class Seconds(seconds: Long) : Duration(seconds, TimeUnit.SECONDS, 3)
    class Minutes(minutes: Long) : Duration(minutes, TimeUnit.MINUTES, 4)
    class Hours(hours: Long) : Duration(hours, TimeUnit.HOURS, 5)
    class Days(days: Long) : Duration(days, TimeUnit.DAYS, 6)

    val nanoseconds: Duration.Nanoseconds
        get() = nanoseconds(unit.toNanos(duration))
    val microseconds: Duration.Microseconds
        get() = microseconds(unit.toMicros(duration))
    val milliseconds: Duration.Milliseconds
        get() = milliseconds(unit.toMillis(duration))
    val seconds: Duration.Seconds
        get() = seconds(unit.toSeconds(duration))
    val minutes: Duration.Minutes
        get() = minutes(unit.toMinutes(duration))
    val hours: Duration.Hours
        get() = hours(unit.toHours(duration))
    val days: Duration.Days
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

    operator fun plus(other: Duration): Duration {
        val (lhs, rhs, smallestUnit) = reducedOperands(other)
        return unitConstructor(smallestUnit)(lhs.duration + rhs.duration)
    }

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