package com.redspace.utils.latestvaluecache

import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import io.reactivex.functions.Predicate
import io.reactivex.processors.PublishProcessor
import io.reactivex.subjects.BehaviorSubject

sealed class Cached<out V> {
    data class Error(val e: Throwable) : Cached<Nothing>()
    data class Value<out V>(val v: V) : Cached<V>()
}

/**
 * @return a Single whose value is cached as long as the given predicate returns true. If the
 * predicate returns false, the cached value is discarded and the upstream Single is triggered
 * again.
 * TODO: Not working yet
 */
fun <T> Single<T>.cacheWhile(predicate: Predicate<Cached<T>>): Single<T> {
    val that = this
    var cache = that.cache()
    var value: Cached<T>? = null

    return object : Single<T>() {
        override fun subscribeActual(observer: SingleObserver<in T>) {
            val oldValue = value

            if (oldValue != null) {
                if (predicate.test(oldValue)) {
                    observer.onSubscribe(Disposables.empty())
                    if (oldValue is Cached.Value<T>) observer.onSuccess(oldValue.v)
                    else if (oldValue is Cached.Error) observer.onError(oldValue.e)
                }
            }

            if (oldValue != null && !predicate.test(oldValue)) {
                cache = that.cache()
                value = null
            }

            cache.subscribe(object : SingleObserver<T> {
                override fun onSubscribe(d: Disposable) = observer.onSubscribe(d)

                override fun onSuccess(t: T) {
                    value = Cached.Value(t)
                    observer.onSuccess(t)
                }

                override fun onError(e: Throwable) {
                    value = Cached.Error(e)
                    observer.onError(e)
                }
            })
        }
    }
}


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
