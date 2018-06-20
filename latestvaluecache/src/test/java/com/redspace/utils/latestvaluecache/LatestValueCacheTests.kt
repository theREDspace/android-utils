package com.redspace.utils.latestvaluecache

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be false`
import org.amshove.kluent.`should be true`
import org.junit.Test
import java.util.Random
import java.util.concurrent.TimeUnit

private const val CACHED_THING = "Cached Thing"

class LatestValueCacheTests {

    private var incr = 0
    private val successWithIncrement = Single.fromCallable {
        incr++
        CACHED_THING
    }

    @Test
    fun `Given a cached value, when I clear, then I should request again on next call`() {
        val testSubject = LatestValueCache(successWithIncrement)
        testSubject.get().blockingGet()

        testSubject.clear()
        testSubject.get().blockingGet()

        incr `should be equal to` 2
    }

    @Test
    fun `Given two quick requests, I should only actually execute the fromCallable body once`() {
        val testSubject = LatestValueCache(successWithIncrement.delay(1, TimeUnit.SECONDS))

        testSubject.get()
        testSubject.get()

        incr `should be equal to` 1
    }

    @Test
    fun `Given 100 quick requests, I should always get Cached Thing`() {
        val testSubject = LatestValueCache(successWithIncrement)

        (1..100).forEach {
            testSubject.get().test().assertValue(CACHED_THING)
        }
    }

    @Test
    fun `Given 2 requests, when first errors, second retries`() {
        val err = Exception()
        val testSubject = LatestValueCache(Single.fromCallable {
            incr++
            if (incr == 1) throw err
            else CACHED_THING
        })

        testSubject.get().test().assertError(err)
        testSubject.get().test().assertValue(CACHED_THING)
        incr `should be equal to` 2
    }

    @Test
    fun `When I attack this cache from a bunch of different threads, I expect correct behaviour`() {
        val random = Random()
        val testSubject = LatestValueCache(successWithIncrement)

        Observable.range(0, 100).flatMap {
            Observable.just(it)
                    .delay { Observable.timer(random.nextInt(4).toLong(), TimeUnit.SECONDS) }
                    .subscribeOn(Schedulers.newThread())
                    .flatMap { testSubject.get().toObservable() }
        }.blockingSubscribe()

        incr `should be equal to` 1
    }

    @Test
    fun `Given I have a value, when I hasValue, then I expect true`() {
        val testSubject = LatestValueCache(successWithIncrement)
        testSubject.get().blockingGet()

        testSubject.hasValue().`should be true`()
    }

    @Test
    fun `Given I do not have a value, when I hasValue, then I expect false`() {
        val testSubject = LatestValueCache(successWithIncrement)

        testSubject.hasValue().`should be false`()
    }

    @Test
    fun `Given I do have an error, when I hasValue, then I expect false`() {
        val testSubject = LatestValueCache(Single.error<String>(Exception()))

        testSubject.hasValue().`should be false`()
    }
}