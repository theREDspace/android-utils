import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject

/**
 * ComponentProducer produces a component via a Single.
 */
interface ComponentProducer<C> {
    val component: Single<C>
}

/**
 * ComponentConsumer consumes a component, and allows that consumed component to then be cleared.
 */
interface ComponentConsumer<C> {
    fun consume(component: C)
    fun clear()
}

/**
 * ComponentManager allows you to manage a 'temporary' component, and make sure that parts of your
 * code don't try to access it when it is not available.
 *
 * For example, a common problem in using fragments is that getActivity() returns null sometimes,
 * and it isn't always super predictable.  Using this class, we can make sure we accurately manage
 * where and when we are providing this:
 *
 * ```
 * val mgr = ComponentManager<Activity>()
 *
 * fun onAttach(...) { mgr.consume(getActivity()) }
 * fun onDestroy(...) { mgr.clear() }
 * ```
 *
 * We can then safely allow other pieces of code to rely on it:
 *
 * ```
 * mgr.component.subscribe { doSomethingWithActivity(it) }
 * ```
 *
 * Note: it is up to the user of this class to manage their own Disposables.
 */
class ComponentManager<C> : ComponentProducer<C>, ComponentConsumer<C> {

    private sealed class ComponentHolder<out C> {
        object Empty : ComponentHolder<Nothing>()
        data class Value<out C>(val component: C) : ComponentHolder<C>()
    }

    private val componentSubject = BehaviorSubject
            .createDefault<ComponentHolder<C>>(ComponentHolder.Empty)

    override val component = componentSubject
            .flatMap<C> {
                when (it) {
                    ComponentHolder.Empty -> Observable.empty()
                    is ComponentHolder.Value<C> -> Observable.just(it.component)
                }
            }
            .firstOrError()

    override fun consume(component: C) = componentSubject.onNext(ComponentHolder.Value(component))

    override fun clear() = componentSubject.onNext(ComponentHolder.Empty)
}
