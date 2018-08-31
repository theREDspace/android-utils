package com.redspace.durations

import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeLessThan
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.util.concurrent.TimeUnit

private val CONSTRUCTORS = listOf(::nanoseconds, ::microseconds, ::milliseconds, ::seconds, ::minutes, ::hours, ::days)

object InternedZerosSpec : Spek({
    val cases = mapOf(
            TimeUnit.NANOSECONDS to zero,
            TimeUnit.MICROSECONDS to zero,
            TimeUnit.MILLISECONDS to zero,
            TimeUnit.SECONDS to zero,
            TimeUnit.MINUTES to zero,
            TimeUnit.HOURS to zero,
            TimeUnit.DAYS to zero
    )

    cases.forEach { unit, zeroType ->
        describe("zero constant for the unit $unit") {
            val duration = unit.toDuration(0)
            it("should equal $zeroType") {
                duration shouldBe zeroType
            }
        }
    }
})

object ConstructionMethodSpec : Spek({
    val ones = CONSTRUCTORS.map { it(1) }

    CONSTRUCTORS.forEachIndexed { index, ctor ->
        describe("the result of a specific constructor (constructors[$index])") {
            val one = ctor(1)
            it("should only equal the output of that specific constructor (ones[$index])") {
                one shouldEqual ones[index]
                ones.filterNot { it == one }.size shouldEqual ones.size - 1
            }
        }
    }
})

object ComparableSpec : Spek({
    val values = CONSTRUCTORS.flatMap { ctor -> listOf(1L, 2).map(ctor) }

    values.forEachIndexed { index, value ->
        describe("the duration $value") {
            it("should be larger than than all values preceeding it") {
                values.subList(0, index).none { it >= value }.shouldBeTrue()
            }
            it("should be smaller than all values after it") {
                values.subList(index + 1, values.size).none { it <= value }.shouldBeTrue()
            }
        }
    }

    val unsorted = values.shuffled()
    it("should sort itself correctly") {
        unsorted.sorted() shouldEqual values
    }

    val a = nanoseconds(1000)
    val b = microseconds(1)
    describe("when comparing the same duration in different units") {
        val result = a.compareTo(b)
        it("should be equivalent") {
            result shouldEqual 0
        }
    }
})

object NegativeDurationsSpec : Spek({
    val neg = milliseconds(-100)

    it("should not crash when I ask for a different unit") {
        neg.days shouldEqual 0L
    }

    it("should maintain sign if converted") {
        neg.microseconds shouldBeLessThan 0
    }

    it("should become positive if unary minus is applied") {
        -neg shouldEqual milliseconds(100)
    }
})

object AdditionOperatorSpec : Spek({
    val small = nanoseconds(10)
    val large = microseconds(1)
    val sum = 1010L

    describe("when adding a smaller unit to a larger unit") {
        val result = small + large
        it("should not lose precision") {
            result.nanoseconds shouldEqual sum
        }
    }

    describe("when adding a larger unit to a smaller unit") {
        val result = large + small
        it("should not lose precision") {
            result.nanoseconds shouldEqual sum
        }
    }
})

object SubtractionOperatorSpec : Spek({
    val small = nanoseconds(10)
    val large = microseconds(1)
    val differenceSmallMinusLarge = -990L
    val differenceLargeMinusSmall = 990L

    describe("when subtracting a smaller unit from a larger unit") {
        val result = small - large
        it("should not lose precision") {
            result.nanoseconds shouldEqual differenceSmallMinusLarge
        }
    }

    describe("when subtracting a larger unit from a smaller unit") {
        val result = large - small
        it("should not lose precision") {
            result.nanoseconds shouldEqual differenceLargeMinusSmall
        }
    }
})

object ConvertUpSpec : Spek({

    val data = mapOf(
            nanoseconds(1).microseconds to "us",
            microseconds(1).milliseconds to "ms",
            milliseconds(1).seconds to "s",
            seconds(1).minutes to "m",
            minutes(1).hours to "h",
            hours(1).days to "d"
    )

    data.forEach { (duration, unit) ->
        describe("when I convert less than 1$unit to $unit") {
            it("should equal 0") {
                duration shouldEqual 0L
            }
        }

    }
})
