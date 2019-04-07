package com.ypz.rxjavademo.operators

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.ypz.rxjavademo.R
import com.ypz.rxjavademo.base.ItemAdapter
import com.ypz.rxjavademo.base.ItemValue
import com.ypz.rxjavademo.base.logIMessage
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.ObservableSource
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_operators.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class OperatorsCreatingActivity : AppCompatActivity() {

    private var itemAdapter: ItemAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_operators)
        itemAdapter = ItemAdapter(
                listOf(
                        ItemValue("Operators-create", 0),
                        ItemValue("Operators-just", 1),
                        ItemValue("Operators-from", 2),
                        ItemValue("Operators-repeat", 3),
                        ItemValue("Operators-defer", 4),
                        ItemValue("Operators-interval", 5),
                        ItemValue("Operators-timer", 6),
                        ItemValue("Operators-ranger", 7),
                        ItemValue("Operators-empty", 8),
                        ItemValue("Operators-never", 9),
                        ItemValue("Operators-error", 10)
                ),
                this@OperatorsCreatingActivity,
                Consumer {
                    logIMessage("clickValue", "$it")
                    when (it) {
                        0L -> operatorsCreate()
                        1L -> operatorsJust()
                        2L -> operatorsFrom()
                        3L -> operatorsRepeat()
                        4L -> operatorsDefer()
                        5L -> operatorsInterval()
                        6L -> operatorsTimer()
                        7L -> operatorsRange()
                        8L->{
                            Observable.empty<String>().subscribe(object :Observer<String>{
                                override fun onComplete() {
                                    logIMessage("Operators-empty","value:onComplete")
                                }

                                override fun onSubscribe(d: Disposable) {

                                }

                                override fun onNext(t: String) {
                                    logIMessage("Operators-empty","value:$it")
                                }

                                override fun onError(e: Throwable) {
                                    logIMessage("Operators-empty","value:$it")
                                }

                            })
                        }
                        9L->{
                            Observable.never<String>().subscribe(object :Observer<String>{
                                override fun onComplete() {
                                    logIMessage("Operators-never","value:onComplete")
                                }

                                override fun onSubscribe(d: Disposable) {
                                    logIMessage("Operators-never","value:onSubscribe")
                                }

                                override fun onNext(t: String) {
                                    logIMessage("Operators-never","value:$it")
                                }

                                override fun onError(e: Throwable) {
                                    logIMessage("Operators-never","value:$it")
                                }
                            })
                        }
                        10L->{
                            Observable.error<Throwable> {
                                Throwable("this is a error")
                            }.subscribe(object :Observer<Throwable>{
                                override fun onComplete() {
                                    logIMessage("Operators-error","value:onComplete")
                                }

                                override fun onSubscribe(d: Disposable) {
                                    logIMessage("Operators-error","value:onSubscribe")
                                }

                                override fun onNext(t: Throwable) {
                                    logIMessage("Operators-error","onNext value:$it")
                                }

                                override fun onError(e: Throwable) {
                                    logIMessage("Operators-error","onError value:$it")
                                }
                            })
                        }
                        else -> {

                        }
                    }
                }
        )
        operators_rv.apply {
            layoutManager = LinearLayoutManager(this@OperatorsCreatingActivity)
            setHasFixedSize(true)
            itemAdapter!!.setHasStableIds(true)
            adapter = itemAdapter
        }



    }

    /**
     * 运行结果如下：
     * com.ypz.rxjavademo I/operatorsCreate: 创建一个最基本的观察者模式
     * com.ypz.rxjavademo I/operatorsCreate: onSubscribe
     * com.ypz.rxjavademo I/operatorsCreate: onNext-value1
     * com.ypz.rxjavademo I/operatorsCreate: onNext-value2
     * com.ypz.rxjavademo I/operatorsCreate: onNext-value3
     * com.ypz.rxjavademo I/operatorsCreate: onNext-value4
     * com.ypz.rxjavademo I/operatorsCreate: onNext-value5
     * com.ypz.rxjavademo I/operatorsCreate: onNext-value6
     * com.ypz.rxjavademo I/operatorsCreate: onNext-value7
     * com.ypz.rxjavademo I/operatorsCreate: onNext-value8
     * com.ypz.rxjavademo I/operatorsCreate: onNext-value9
     * com.ypz.rxjavademo I/operatorsCreate: onNext-value10
     * com.ypz.rxjavademo I/operatorsCreate: onComplete
     *
     * */
    private fun operatorsCreate() {
        showResultMessage("创建一个最基本的观察者模式")
        logIMessage(
                "operatorsCreate",
                """创建一个最基本的观察者模式""")
        Observable.create<Long> {
            if (!it.isDisposed) {
                for (item in 1..10) {
                    it.onNext(item.toLong())
                    if (item == 10) it.onComplete()
                }
            }
        }.observeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<Long> {
            override fun onComplete() {
                showResultMessage("onComplete")
                logIMessage("operatorsCreate", "onComplete")
            }

            override fun onSubscribe(d: Disposable) {
                showResultMessage("onSubscribe")
                logIMessage("operatorsCreate", "onSubscribe")
            }

            override fun onNext(t: Long) {
                showResultMessage("onNext-value$t")
                logIMessage("operatorsCreate", "onNext-value$t")
            }

            override fun onError(e: Throwable) {
                showResultMessage("onError${e.message ?: "unknown error"}")
                logIMessage("operatorsCreate", "onError${e.message ?: "unknown error"}")
            }

        })
    }

    private fun operatorsJust() {
        /*发射单数据*/
        Observable.just("just").subscribe {
            logIMessage("operatorsJust", it)
        }
        /*按顺序发射多数据*/
        Observable.just<Any>(1, 2, 3, 4, "item5").subscribe {
            logIMessage("operatorsJust2", it.toString())
        }
    }

    private fun operatorsFrom() {
        /*遍历数组示例*/
        Observable.fromArray<Any>("item1", 1).subscribe {
            logIMessage("fromArray", it.toString())
        }

        Observable.fromIterable(1..10).subscribe {
            logIMessage("fromIterable", it.toString())
        }

        fromFuture()
    }

    private fun fromFuture() {
        val executorService = Executors.newCachedThreadPool()
        val future = executorService.submit(Callable<String> {
            logIMessage("future", "do some work look like task")
            Thread.sleep(2500)
            "finsh work"
        })
        logIMessage("future", "do some work but it's timeout")
        Observable.fromFuture(future, 1, TimeUnit.SECONDS).subscribe(
                {
                    logIMessage("future-next", "value$it")
                },
                {
                    logIMessage("future-error", "value${it.message}")
                }
        )
        Observable.fromFuture(future).subscribe {
            logIMessage("future", "value$it")
        }
    }

    /**
     * 运行结果如下：
     * com.ypz.rxjavademo I/Operators-repeat: repeat 重复发送数据
     * com.ypz.rxjavademo I/Operators-repeat: value:hello repeat
     * com.ypz.rxjavademo I/Operators-repeat: value:hello repeat
     * com.ypz.rxjavademo I/Operators-repeat: repeatWhen 指定条件重发数据或者中断数据
     * com.ypz.rxjavademo I/Operators-repeat-repeatWhen: apply Times:0
     * com.ypz.rxjavademo I/Operators-repeat-repeatWhen: onNext:ss
     * com.ypz.rxjavademo I/Operators-repeat-repeatWhen: 触发重订阅
     * com.ypz.rxjavademo I/Operators-repeat-repeatWhen: flatMap Times:0
     * com.ypz.rxjavademo I/Operators-repeat-repeatWhen: onNext:ss
     * com.ypz.rxjavademo I/Operators-repeat-repeatWhen: 触发重订阅
     * com.ypz.rxjavademo I/Operators-repeat-repeatWhen: flatMap Times:1
     * com.ypz.rxjavademo I/Operators-repeat-repeatWhen: onNext:ss
     * com.ypz.rxjavademo I/Operators-repeat-repeatWhen: 触发重订阅
     * com.ypz.rxjavademo I/Operators-repeat-repeatWhen: flatMap Times:2
     * com.ypz.rxjavademo I/Operators-repeat-repeatWhen: onNext:ss
     * com.ypz.rxjavademo I/Operators-repeat-repeatWhen: 触发重订阅
     * com.ypz.rxjavademo I/Operators-repeat: repeatUntil 达到条件就中断上流重复发送数据
     * com.ypz.rxjavademo I/Choreographer: Skipped 121 frames!  The application may be doing too much work on its main thread.
     * com.ypz.rxjavademo I/Operators-repeat-repeatUntil: value0
     * com.ypz.rxjavademo I/Operators-repeat-repeatUntil: value1
     * com.ypz.rxjavademo I/Operators-repeat-repeatUntil: value2
     * com.ypz.rxjavademo I/Operators-repeat-repeatUntil: value3
     * com.ypz.rxjavademo I/Operators-repeat-repeatUntil: value4
     * com.ypz.rxjavademo I/Operators-repeat-repeatUntil: value0
     * com.ypz.rxjavademo I/Operators-repeat-repeatUntil: value1
     * com.ypz.rxjavademo I/Operators-repeat-repeatUntil: value2
     * com.ypz.rxjavademo I/Operators-repeat-repeatUntil: value3
     * com.ypz.rxjavademo I/Operators-repeat-repeatUntil: value4
     * */
    private fun operatorsRepeat() {
        logIMessage("Operators-repeat", "repeat 重复发送数据")
        Observable.just("hello repeat").repeat(2).subscribe { logIMessage("Operators-repeat", "value:$it") }
        logIMessage("Operators-repeat", "repeatWhen 指定条件重发数据或者中断数据")
        Observable.create(
                ObservableOnSubscribe<String> { emitter ->
                    emitter.onNext("ss")
                    emitter.onComplete()
                }).doOnComplete {
            logIMessage("Operators-repeat-repeatWhen", "触发重订阅")
        }.repeatWhen(object : Function<Observable<Any>, ObservableSource<*>> {
            private var n = 0
            @Throws(Exception::class)
            override fun apply(objectObservable: Observable<Any>): ObservableSource<*> {
                /**
                 * 这里进行在RxJava2.x以及一些博客说道apply这里在上流发送数据后才会回调这个事件的对象勘误
                 * 说法没有错但是repeatWhen在使用完整个事件流创建的时候它会一并创建出来而且只会创建一次
                 * 并不是在上游调用onComple后才会回调到这个事件，
                 * 这个事件创建了决定是否重复以及终止数据被观察着对象仅仅回调一次
                 * */
                logIMessage("Operators-repeat-repeatWhen", "apply Times:$n")
                return objectObservable.flatMap {
                    if (n != 3) {
                        logIMessage("Operators-repeat-repeatWhen", "flatMap Times:$n")
                        n++
                        Observable.timer(300, TimeUnit.MILLISECONDS)
                    } else {
                        Observable.empty<Any>()
                    }
                }
            }
        }).subscribe(object : Observer<String> {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(s: String) {
                logIMessage("Operators-repeat-repeatWhen", "onNext:$s")

            }

            override fun onError(e: Throwable) {
                logIMessage("Operators-repeat-repeatWhen", "onError:${e.message
                        ?: "unknown error"}")
            }

            override fun onComplete() {
                logIMessage("Operators-repeat-repeatWhen", "onComplete:")
            }
        })

        Thread.sleep(2000)
        logIMessage("Operators-repeat", "repeatUntil 达到条件就中断上流重复发送数据")
        var repeatUntilTime = 0
        Observable.interval(500, TimeUnit.MILLISECONDS).take(5).repeatUntil {
            repeatUntilTime += 1
            repeatUntilTime >= 2
        }.subscribe {
            logIMessage("Operators-repeat-repeatUntil", "value$it")
        }
    }

    /**
     * 运行结果如下：
     * com.ypz.rxjavademo I/Operators-Defer: value:hello defer
     *
     * */
    private fun operatorsDefer() {
        //直到有观察者订阅时才创建 Observable，并且为每个观察者创建一个全新的Observable，
        val defer = Observable.defer {
            logIMessage("Operators-Defer", "was-create")
            Observable.just("hello defer")
        }
        Thread.sleep(2000)
        logIMessage("Operators-Defer", "sleep finsh")
        defer.subscribe { logIMessage("Operators-Defer", "value:$it") }
    }

    private fun operatorsInterval() {
        /*类似定时器的作用*/
        var disposable: Disposable? = null
        var loadingArrayString = arrayOf("Loading", "Loading.", "Loading..", "Loading...", "Loading..", "Loading.")
        logIMessage("Operators-interval", "实现动态修改字符串的值")
        disposable = Observable.intervalRange(0, 6, 0, 1, TimeUnit.SECONDS)
                .observeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread()).subscribe {
                    val message = loadingArrayString[it.toInt()]
                    runOnUiThread { show_result.text = message }
                    logIMessage("Operators-interval-Loading", "value:$message")
                    if (it == 5L) disposable?.dispose()
                }
    }

    private fun operatorsTimer() {
        /*指定一段时间延迟开始发送数据*/
        logIMessage("Operators-timer", "延迟两秒发送数据")
        showResultMessage("延迟两秒发送数据")
        Observable.timer(2, TimeUnit.SECONDS).observeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread()).subscribe {
            logIMessage("Operators-timer", "value:hello timer")
            showResultMessage("hello timer")
        }
    }

    private fun operatorsRange() {
        Observable.range(1, 3).subscribe {
            logIMessage("operators-range", it.toString())
        }
    }

    /**
     * 运行结果如下：
     * com.ypz.rxjavademo I/operators-range: 1
     * com.ypz.rxjavademo I/operators-range: 2
     * com.ypz.rxjavademo I/operators-range: 3
     * */

    private fun showResultMessage(message: String) = runOnUiThread {
        show_result.text = message
    }

    override fun onDestroy() {
        super.onDestroy()
        itemAdapter?.cancleClickNext()
    }
}
