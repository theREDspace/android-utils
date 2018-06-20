package com.redspace.utils.latestvaluecache

import io.reactivex.Single
import io.reactivex.processors.PublishProcessor
import io.reactivex.subjects.BehaviorSubject

class LatestValueCache<V>(valueSingle: Single<V>) {

    private sealed class Cached<out V> {
        object Empty : Cached<Nothing>()
        data class Error(val e: Throwable) : Cached<Nothing>()
        data class Value<out V>(val v: V) : Cached<V>()
    }

    private val requestProcessor = PublishProcessor.create<Unit>()
    private val responseContainer = BehaviorSubject.createDefault<Cached<V>>(Cached.Empty)

    init {
        requestProcessor
                .onBackpressureDrop()
                .flatMap({
                    valueSingle
                            .map<Cached<V>> { Cached.Value(it) }
                            .onErrorReturn { Cached.Error(it) }
                            .toFlowable()
                }, 1)
                .subscribe {
                    synchronized(this@LatestValueCache) {
                        responseContainer.onNext(it)
                    }
                }
    }

    fun get(): Single<V> = synchronized(this) {
        val cachedObject = responseContainer.value
        when (cachedObject) {
            is Cached.Value -> Single.just(cachedObject.v)
            else -> requestValue()
        }
    }

    fun clear() = synchronized(this) {
        responseContainer.onNext(Cached.Empty)
    }

    fun hasValue(): Boolean = synchronized(this) {
        responseContainer.value is Cached.Value
    }

    private fun requestValue(): Single<V> {
        request()
        return responseContainer
                .filter { it !== Cached.Empty }.firstOrError().map {
                    when (it) {
                        Cached.Empty -> error("This should never happen!")
                        is Cached.Value -> it.v
                        is Cached.Error -> throw it.e
                    }
                }
    }

    private fun request() {
        responseContainer.onNext(Cached.Empty)
        requestProcessor.onNext(Unit)
    }

}
