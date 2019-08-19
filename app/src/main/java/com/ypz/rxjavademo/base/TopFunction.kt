package com.ypz.rxjavademo.base

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.MaybeObserver
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 *  Created by 易庞宙 on 2019 2019/3/12 14:56
 *  email: 1986545332@qq.com
 */
@SuppressLint("LongLogTag")
fun logIMessage(tag: String, message: String) = Log.i(tag, message)

@SuppressLint("LongLogTag")
fun <T> getBaseObsever(tag: String, getDisposable: (Disposable) -> Unit, done: () -> Unit): Observer<T> =
        object : Observer<T> {
            override fun onComplete() {
                done()
            }

            override fun onSubscribe(d: Disposable) {
                logIMessage(tag, "onSubscribe")
                getDisposable(d)
            }

            override fun onNext(t: T) {
                logIMessage(tag, "onNextValue:${t.toString()}")
            }

            override fun onError(e: Throwable) {
                logIMessage(tag, "onErrorMessage:${e.message ?: "未知错误"}")
            }
        }

fun <T> getBaseObsever(tag: String): Observer<T> {
    var dis: Disposable? = null
    return getBaseObsever(tag, { disposable -> dis = disposable }, {  dis?.dispose() })
}

//获取Observer<Long>
fun getBaseLongObserver(tag: String, getDisposable: (Disposable) -> Unit, done: () -> Unit): Observer<Long> =
        getBaseObsever(tag, getDisposable, done)

//获取MaybeObserver<Long>
fun getBaseLongMaybeObserver(tag: String, getDisposable: (Disposable) -> Unit, done: () -> Unit): MaybeObserver<Long> = object : MaybeObserver<Long> {
    override fun onComplete() {
        logIMessage(tag, "onComplete")
        done()
    }

    override fun onSubscribe(p0: Disposable) {
        logIMessage(tag, "onSubscribe")
        getDisposable(p0)
    }

    override fun onSuccess(t: Long) {
        logIMessage(tag, "onNext:value$t")
    }

    override fun onError(p0: Throwable) {
        logIMessage(tag, "onError:value$p0")
        done()
    }
}

fun getBaseLongSingleObserver(tag: String, getDisposable: (Disposable) -> Unit, done: () -> Unit): SingleObserver<Long> =
        getBaseSingleObserver(tag, getDisposable, done)

fun <T> getBaseSingleObserver(tag: String): SingleObserver<T> {
    var disposable: Disposable? = null
    return getBaseSingleObserver(tag, { disposable = it }, { disposable?.dispose() })
}

fun <T> getBaseSingleObserver(tag: String, getDisposable: (Disposable) -> Unit, done: () -> Unit): SingleObserver<T> = object : SingleObserver<T> {
    override fun onSuccess(t: T) {
        logIMessage(tag, "onNext:value-${t.toString()}")
        done()
    }

    override fun onSubscribe(d: Disposable) {
        logIMessage(tag, "onSubscribe")
        getDisposable(d)
    }

    override fun onError(e: Throwable) {
        logIMessage(tag, "onError:value${e.message ?: "unkown error"}")
        done()
    }

}

fun doubleClick(view: View, time: Long, unClickTimeUnit: TimeUnit, comitToDo: () -> Unit) {
    if (!view.isClickable) return
    else {
        view.isClickable = false
        comitToDo()
        Observable.timer(time, unClickTimeUnit).observeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread()).subscribe {
            view.isClickable = true
        }
    }
}

inline fun <reified T : Activity> Activity.startActivity() {
    startActivity(Intent(this, T::class.java))
}

inline fun <reified T : AppCompatActivity> Activity.startAppCompatActivity() {
    startActivity(Intent(this, T::class.java))
}

inline fun <reified T : Activity> AppCompatActivity.startActivity() {
    startActivity(Intent(this, T::class.java))
}

inline fun <reified T : AppCompatActivity> AppCompatActivity.startAppCompatActivity() {
    startActivity(Intent(this, T::class.java))
}