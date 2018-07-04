package com.redspace.startsnaphelper.demo

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.redspace.utils.rxbinding.RxBinding
import kotlinx.android.synthetic.main.binding_demo.*

class BindingDemo : Activity() {
    private val bindings = RxBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val buttonClickObservable = bindings.clicks(testButton)

        buttonClickObservable.subscribe { Log.w("MARK", "clicked") }
    }
}
