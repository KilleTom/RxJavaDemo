package com.ypz.rxjavademo.base

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
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
fun logIMessage(tag:String,message:String) = Log.i(tag,message)

//获取Observer<Long>
fun getBaseLongObserver(tag: String, getDisposable: (Disposable) -> Unit, done: () -> Unit): Observer<Long> = object : Observer<Long> {
    override fun onComplete() {
        logIMessage(tag, "onComplete")
        done()
    }

    override fun onSubscribe(p0: Disposable) {
        logIMessage(tag, "onSubscribe")
        getDisposable(p0)
        if (!p0.isDisposed){

        }
    }

    override fun onNext(p0: Long) {
        logIMessage(tag, "onNext:value$p0")
    }

    override fun onError(p0: Throwable) {
        logIMessage(tag, "onError:value$p0")
        done()
    }

}

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

fun getBaseLongSingleObserver(tag: String, getDisposable: (Disposable) -> Unit, done: () -> Unit): SingleObserver<Long> = object : SingleObserver<Long> {
    override fun onSuccess(t: Long) {
        logIMessage(tag, "onNext:value$t")
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