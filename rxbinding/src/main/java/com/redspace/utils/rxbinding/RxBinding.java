package com.redspace.utils.rxbinding;

import android.view.View;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>Provides a fixed context to trigger Rx-based bindings to Android view components from.</p>
 * <p>Usage:
 {@code
    class MyActivity extends Activity {
        private RxBinding bindings = new RxBinding();

        @Resource(R.id.button)
        Button button;

        public void onCreate(...) {
            bindings.clicks(view).subscribe(new Observer<>() {
                // do something
            });
        }
    }
 }
 * </p>
 *
 * <p>Various observable elements of the view can be bound using this binder; check the listed methods
 * in Javadoc to see which are supported.</p>
 *
 * <p>Observers can be bound to a view at any time, and live either until disposed, or until RxBinding is
 * disposed/GCed. Observers can be bound/unbound at any time.</p>
 * <p>For each view/property combination, the associated observer is created lazily on first bind, and
 * retained until this object is disposed.</p>
 */
public class RxBinding implements Disposable {
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final Map<View, ClickHandler> clickHandlers = new ConcurrentHashMap<>();

    public Observable<Empty> clicks(View view) {
        return clickHandlers.computeIfAbsent(view, ClickHandler::new).clicks;
    }

    @Override
    public void dispose() {
        compositeDisposable.dispose();
    }

    @Override
    public boolean isDisposed() {
        return compositeDisposable.isDisposed();
    }

    private class ClickHandler {
        final Observable<Empty> clicks;

        ClickHandler(View view) {
            clicks = Observable
                    .<Empty>create(emitter ->
                            view.setOnClickListener(v -> emitter.onNext(Empty.EMPTY)))
                    .publish()
                    .autoConnect(1, compositeDisposable::add);
        }
    }
}
