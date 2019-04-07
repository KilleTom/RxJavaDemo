package com.ypz.rxjavademo.base

import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * Created by 易庞宙 on 2019 2019/4/4 20:04
 * email: 1986545332@qq.com
 */
class ShowMessageBaseObserver<T>(private val tag: String) : Observer<T> {

    private var disposable: Disposable? = null

    override fun onSubscribe(d: Disposable) {
        logIMessage(tag, "onSubscribe")
        disposable = d
    }

    override fun onNext(t: T) {
        logIMessage(tag, "onNextValue:${t.toString()}")
    }

    override fun onError(e: Throwable) {
        logIMessage(tag, "errorMessage:${e.message ?: "unkown error"}")
        if (disposable != null && !disposable!!.isDisposed)
            disposable!!.dispose()
    }

    override fun onComplete() {
        logIMessage(tag, "onComplete")
        if (disposable != null && !disposable!!.isDisposed)
            disposable!!.dispose()
    }


}
