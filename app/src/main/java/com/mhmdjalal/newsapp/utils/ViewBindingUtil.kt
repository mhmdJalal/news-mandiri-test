package com.mhmdjalal.newsapp.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * single-liner initialization for use view binding in activity
 */
inline fun <T : ViewBinding> AppCompatActivity.viewBinding(
    crossinline bindingInflater: (LayoutInflater) -> T
) =
    lazy(LazyThreadSafetyMode.NONE) {
        bindingInflater.invoke(layoutInflater)
    }

/**
 * clear binding value when the views are destroyed.
 */
class FragmentViewBindingDelegate<T : ViewBinding>(
    val fragment: Fragment,
    val viewBindingFactory: (View) -> T
) : ReadOnlyProperty<Fragment, T> {
    private var binding: T? = null

    init {
        fragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
            val viewLifecycleOwnerLiveDataObserver = Observer<LifecycleOwner?> {
                val viewLifecycleOwner = it ?: return@Observer

                viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                    override fun onDestroy(owner: LifecycleOwner) {
                        binding = null
                    }
                })
            }

            override fun onCreate(owner: LifecycleOwner) {
                fragment.viewLifecycleOwnerLiveData.observeForever(
                    viewLifecycleOwnerLiveDataObserver
                )
            }

            override fun onDestroy(owner: LifecycleOwner) {
                fragment.viewLifecycleOwnerLiveData.removeObserver(
                    viewLifecycleOwnerLiveDataObserver
                )
            }
        })
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        try {
            val binding = binding
            if (binding != null) {
                return binding
            }
            val lifecycle = thisRef.viewLifecycleOwner.lifecycle
            if (!lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
                error(
                    IllegalStateException(
                        "Should not attempt to get bindings when Fragment views are destroyed.")
                )
            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }

        return viewBindingFactory(thisRef.requireView()).also { this.binding = it }
    }
}

/**
 * single-liner initialization for use view binding in fragment
 */
fun <T : ViewBinding> Fragment.viewBinding(viewBindingFactory: (View) -> T) =
    FragmentViewBindingDelegate(this, viewBindingFactory)

/**
 * single-liner initialization for use view binding in adapter
 */
inline fun <T : ViewBinding> ViewGroup.viewBinding(
    viewBindingFactory: (LayoutInflater, ViewGroup, Boolean) -> T
) = viewBindingFactory.invoke(LayoutInflater.from(this.context), this, false)

/**
 * single-liner initialization for use view binding in dialog
 */
inline fun <T : ViewBinding> Context.viewBindingDialog(viewBindingFactory: (LayoutInflater) -> T) =
    viewBindingFactory.invoke(LayoutInflater.from(this))