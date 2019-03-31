package com.ypz.rxjavademo.operators

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.ypz.rxjavademo.R
import com.ypz.rxjavademo.base.ItemAdapter
import com.ypz.rxjavademo.base.ItemValue
import com.ypz.rxjavademo.base.logIMessage
import io.reactivex.Observable
import io.reactivex.Observable.just
import io.reactivex.Observable.range
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_operators.*
import java.util.concurrent.TimeUnit

/**
 * Created by 易庞宙 on 2019 2019/3/22 10:11
 * email: 1986545332@qq.com
 */
class OperatorsTransformingActivity : AppCompatActivity() {

    private var itemAdapter: ItemAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_operators)
        itemAdapter = ItemAdapter(
                listOf(
                        ItemValue("Operators-map", 0),
                        ItemValue("Operators-flatMap", 1),
                        ItemValue("Operators-concatMap", 2),
                        ItemValue("Operators-cast", 3),
                        ItemValue("Operators-groupBy", 4),
                        ItemValue("Operators-buffer", 5),
                        ItemValue("Operators-buffer-skip等于count", 6),
                        ItemValue("Operators-buffer-skip小于count", 7),
                        ItemValue("Operators-buffer-skip大于count", 8),
                        ItemValue("Operators-window-指定数据组装大小size", 9),
                        ItemValue("Operators-window-指定数据组装时间段", 10),
                        ItemValue("Operators-window-指定数据组装时间段和size", 11),
                        ItemValue("Operators-scan", 12)
                ),
                this@OperatorsTransformingActivity,
                Consumer {
                    logIMessage("clickValue", "$it")
                    when (it) {
                        0L -> operatorsMap()
                        1L -> operatorsFlatMap()
                        2L -> operatorsConcatMap()
                        3L -> operatorsCast()
                        4L -> operatorsGroupBy()
                        5L -> operatorsBuffer()
                        6L -> operatorsBuffer_skipComparativeCount(2, 2)
                        7L -> operatorsBuffer_skipComparativeCount(3, 2)
                        8L -> operatorsBuffer_skipComparativeCount(1, 2)
                        9L -> operatorsWindowCount()
                        10L -> operatorsWindowTimespan()
                        11L -> operatorsWindowTimesCompeteCount()
                        12L -> operatorsScan()
                        else -> {

                        }
                    /*  0L -> operatorsCreate()
                      1L -> operatorsJust()
                      2L -> operatorsFrom()
                      3L -> operatorsRepeat()
                      4L -> operatorsDefer()
                      5L -> operatorsInterval()
                      6L -> operatorsTimer()*/
                    }
                }
        )
        operators_rv.apply {
            layoutManager = LinearLayoutManager(this@OperatorsTransformingActivity)
            setHasFixedSize(true)
            itemAdapter!!.setHasStableIds(true)
            adapter = itemAdapter
        }
    }

    /**
     * 运行结果：
     * com.ypz.rxjavademo I/operatorsMap: 20
     * com.ypz.rxjavademo I/operatorsMap: 40
     * com.ypz.rxjavademo I/operatorsMap: 60
     * */
    private fun operatorsMap() =
            /**
             * map作用将每一个被发射的数据进行加工操作
             * 可以转换为其他对象，也可以将元数据进行替换
             * 注意使用map对发射的数据进行转换操作的时候
             * 应该针对场景使用适用的数学思维避免某些元数据
             * 转换出不可期望的结果
             *
             * */
            Observable.just(10, 20, 30).map {
                (it * 2).toString()
            }.subscribe {
                logIMessage("operatorsMap", it)
            }


    /**
     * transform the items emitted by an Observable into Observables, then flatten the emissions from those into a single Observable
     * 將每一个Observable变换为一组Observables，然后发射每一个Observable
     * The FlatMap operator transforms an Observable by applying a function that you specify to each item emitted by the source Observable,
     * where that function returns an Observable that itself emits items. FlatMap then merges the emissions of these resulting Observables,
     * emitting these merged results
     * flatMap操作符通过实现Function中apply函数，将Observables中每一个Observable通过实现的apply函数转换为实际所需的Observable，
     * 然后组合成一个序列发射出去（注意其组合序列可能与原Observables序列顺序不一致）
     *
     * 运行结果：
     * com.ypz.rxjavademo I/operators-flatMap: 直接转换value:5
     * com.ypz.rxjavademo I/operators-flatMap: 直接转换加函数变换
     * com.ypz.rxjavademo I/operators-flatMap: 需要转换的Value:1
     * com.ypz.rxjavademo I/operators-flatMap: 转换前数值：1
     * com.ypz.rxjavademo I/operators-flatMap: 转换后数值：10
     * com.ypz.rxjavademo I/operators-flatMap: 直接转换加函数变换value:11
     * com.ypz.rxjavademo I/operators-flatMap: 转换前数值：1
     * com.ypz.rxjavademo I/operators-flatMap: 转换后数值：11
     * com.ypz.rxjavademo I/operators-flatMap: 直接转换加函数变换value:12
     * com.ypz.rxjavademo I/operators-flatMap: 需要转换的Value:2
     * com.ypz.rxjavademo I/operators-flatMap: 转换前数值：2
     * com.ypz.rxjavademo I/operators-flatMap: 转换后数值：20
     * com.ypz.rxjavademo I/operators-flatMap: 直接转换加函数变换value:22
     * com.ypz.rxjavademo I/operators-flatMap: 转换前数值：2
     * com.ypz.rxjavademo I/operators-flatMap: 转换后数值：21
     * com.ypz.rxjavademo I/operators-flatMap: 直接转换加函数变换value:23
     * com.ypz.rxjavademo I/operators-flatMap: 需要转换的Value:3
     * com.ypz.rxjavademo I/operators-flatMap: 转换前数值：3
     * com.ypz.rxjavademo I/operators-flatMap: 转换后数值：30
     * com.ypz.rxjavademo I/operators-flatMap: 直接转换加函数变换value:33
     * com.ypz.rxjavademo I/operators-flatMap: 转换前数值：3
     * com.ypz.rxjavademo I/operators-flatMap: 转换后数值：31
     * com.ypz.rxjavademo I/operators-flatMap: 直接转换加函数变换value:34
     * */
    private fun operatorsFlatMap() {
        logIMessage("operators-flatMap", "直接转换")
        Observable.just(1, 2, 3).flatMap {
            Observable.just(it * 2 - 1)
        }.subscribe {
            logIMessage("operators-flatMap", "直接转换value:$it")
        }
        logIMessage("operators-flatMap", "直接转换加函数变换")
        val function = Function<Int, Observable<Int>> { value ->
            logIMessage("operators-flatMap", "需要转换的Value:$value")
            Observable.range(value * 10, 2)
        }
        val biFunction = BiFunction<Int, Int, Int> { initValue, changeValue ->
            logIMessage("operators-flatMap", "转换前数值：$initValue")
            logIMessage("operators-flatMap", "转换后数值：$changeValue")
            initValue + changeValue
        }
        just(1, 2, 3).observeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread()).flatMap(function, biFunction).subscribe {
            logIMessage("operators-flatMap", "直接转换加函数变换value:$it")
        }
    }

    /**
     * 运行结果如下:
     * com.ypz.rxjavademo I/operators-cast: value1
     * com.ypz.rxjavademo I/operators-cast: value2
     * com.ypz.rxjavademo I/operators-cast: errorMessage:Cannot cast java.lang.String to java.lang.Integer
     * */
    private fun operatorsConcatMap() =
            just(1, 2, 3).concatMap {
                Observable.range(it * 10, 2)
            }.subscribe {
                logIMessage("operators-concatMap", "value$it")
            }

    /**
     * 运行结果如下:
     * com.ypz.rxjavademo I/operators-concatMap: value10
     * com.ypz.rxjavademo I/operators-concatMap: value11
     * com.ypz.rxjavademo I/operators-concatMap: value20
     * com.ypz.rxjavademo I/operators-concatMap: value21
     * com.ypz.rxjavademo I/operators-concatMap: value30
     * com.ypz.rxjavademo I/operators-concatMap: value31
     * */
    private fun operatorsCast() = Observable.just(1, 2, "string")
            .cast(Integer::class.java)//订阅之后才能发横强转
            .subscribe(
                    {
                        logIMessage("operators-cast", "value$it")
                    },
                    {
                        logIMessage("operators-cast", "errorMessage:${it.message
                                ?: "unknown Error Message"}")
                    })

    /**
     * 运行结果如下：
     * com.ypz.rxjavademo I/operators-groupBy: 分组key：奇数
     * com.ypz.rxjavademo I/operators-groupBy: 奇数1
     * com.ypz.rxjavademo I/operators-groupBy: 分组key：偶数
     * com.ypz.rxjavademo I/operators-groupBy: 奇数3
     * om.ypz.rxjavademo I/operators-groupBy: 奇数5
     * com.ypz.rxjavademo I/operators-groupBy: 奇数7
     * com.ypz.rxjavademo I/operators-groupBy: 奇数9
     * */
    private fun operatorsGroupBy() {
        val orbseverabe = range(1, 10).groupBy(object : Function<Int, String> {
            override fun apply(t: Int): String {
                return if (t % 2 == 0) "偶数" else "奇数"
            }
        })
        orbseverabe.subscribe {
            logIMessage("operators-groupBy", "分组key：${it.key ?: "key is null"}")
            if (it.key.equals("奇数", true)) {
                it.subscribe {
                    logIMessage("operators-groupBy", "奇数$it")
                }
            }
        }
    }


    /**
     * 运行结果如下:
     * com.ypz.rxjavademo I/BaseOperatorsBufferObserver: forEachValue[1, 2]
     * com.ypz.rxjavademo I/BaseOperatorsBufferObserver: forEachValue[3, 4]
     * com.ypz.rxjavademo I/BaseOperatorsBufferObserver: forEachValue[5, 6]
     * */
    private fun operatorsBuffer() =
            Observable
                    .range(1, 6).buffer(2)
                    .subscribe(getBaseOperatorsBufferObserver())


    private fun getBaseOperatorsBufferObserver(): Observer<List<Int>> =
            object : Observer<List<Int>> {
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: List<Int>) {
                    logIMessage("BaseOperatorsBufferObserver", "forEachValue$t")

                }

                override fun onError(e: Throwable) {

                }
            }

    private fun operatorsBuffer_skipComparativeCount(count: Int, skip: Int) =
            Observable
                    .range(1, 6)
                    .buffer(count, skip)
                    .subscribe(getBaseOperatorsBufferObserver())


    /**
     * 运行结果如下
     * com.ypz.rxjavademo I/operators-WindowCount: onNext
     * com.ypz.rxjavademo I/operators-WindowCount: onNextValue1
     * com.ypz.rxjavademo I/operators-WindowCount: onNextValue2
     * com.ypz.rxjavademo I/operators-WindowCount: onNextValue3
     * com.ypz.rxjavademo I/operators-WindowCount: onNext
     *  com.ypz.rxjavademo I/operators-WindowCount: onNextValue4
     * com.ypz.rxjavademo I/operators-WindowCount: onNextValue5
     * com.ypz.rxjavademo I/operators-WindowCount: onNextValue6
     *  com.ypz.rxjavademo I/operators-WindowCount: onNext
     *  com.ypz.rxjavademo I/operators-WindowCount: onNextValue7
     *  com.ypz.rxjavademo I/operators-WindowCount: onNextValue8
     *  com.ypz.rxjavademo I/operators-WindowCount: onNextValue9
     *  com.ypz.rxjavademo I/operators-WindowCount: onComplete
     * */
    private fun operatorsWindowCount() {
        var time = 0
        val tag = "operators-WindowCount"
        Observable.range(1, 9).window(3).subscribe(
                object : Observer<Observable<Int>> {

                    override fun onComplete() {
                        logIMessage(tag, "onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {}

                    override fun onNext(t: Observable<Int>) {
                        logIMessage(tag, "onNext")
                        time += 1
                        if (time % 2 == 0) Thread.sleep(3000)
                        t.subscribe {
                            logIMessage("operators-WindowCount", "onNextValue$it")
                        }
                    }

                    override fun onError(e: Throwable) {}
                })
    }

    /**
     * 运行效果如下：
     * com.ypz.rxjavademo I/operators-WindowTimespan: onNext
     * com.ypz.rxjavademo I/operators-WindowTimespan: onNextValue0
     * com.ypz.rxjavademo I/operators-WindowTimespan: onNextValue1
     * com.ypz.rxjavademo I/operators-WindowTimespan: onNextValue2
     * com.ypz.rxjavademo I/operators-WindowTimespan: onNext
     * om.ypz.rxjavademo I/operators-WindowTimespan: onNextValue3
     * com.ypz.rxjavademo I/operators-WindowTimespan: onNextValue4
     * com.ypz.rxjavademo I/operators-WindowTimespan: onNextValue5
     * com.ypz.rxjavademo I/operators-WindowTimespan: onNext
     * com.ypz.rxjavademo I/operators-WindowTimespan: onNextValue6
     * com.ypz.rxjavademo I/operators-WindowTimespan: onNextValue7
     * com.ypz.rxjavademo I/operators-WindowTimespan: onNextValue8
     * com.ypz.rxjavademo I/operators-WindowTimespan: onNext
     * com.ypz.rxjavademo I/operators-WindowTimespan: onNextValue9
     * com.ypz.rxjavademo I/operators-WindowTimespan: onComplete
     * */
    private fun operatorsWindowTimespan() {
        val logTag = "operators-WindowTimespan"
        Observable
                .interval(1, TimeUnit.SECONDS)
                .take(10)
                .window(3, TimeUnit.SECONDS)
                .subscribe(getBaseWindowTimeObserver(logTag))
    }

    private fun operatorsWindowTimesComparativeCount() {
        val tag = "operators-WindowTimesCompeteCount"
        Observable
                .interval(1000, TimeUnit.MILLISECONDS)
                .take(20)
                .window(5, 3, TimeUnit.SECONDS)
                .subscribe(getBaseWindowTimeObserver(tag))
    }

    /**
     *  com.ypz.rxjavademo I/operators-WindowTimesCompeteCount: onNext
     *  com.ypz.rxjavademo I/operators-WindowTimesCompeteCount: onNextValue1
     *  com.ypz.rxjavademo I/operators-WindowTimesCompeteCount: onNextValue2
     *  com.ypz.rxjavademo I/operators-WindowTimesCompeteCount: onNextValue3
     *  com.ypz.rxjavademo I/operators-WindowTimesCompeteCount: onNext
     *  com.ypz.rxjavademo I/operators-WindowTimesCompeteCount: onNextValue4
     * com.ypz.rxjavademo I/operators-WindowTimesCompeteCount: onNextValue5
     *  com.ypz.rxjavademo I/operators-WindowTimesCompeteCount: onNextValue6
     *  com.ypz.rxjavademo I/operators-WindowTimesCompeteCount: onNext
     *  com.ypz.rxjavademo I/operators-WindowTimesCompeteCount: onComplete
     * */
    private fun operatorsWindowTimesCompeteCount() {
        val tag = "operators-WindowTimesCompeteCount"
        Observable
                .range(1, 6)
                .window(4, TimeUnit.SECONDS, 3)
                .subscribe(object : Observer<Observable<Int>> {
                    override fun onComplete() {
                        logIMessage(tag, "onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {}

                    override fun onNext(t: Observable<Int>) {
                        logIMessage(tag, "onNext")
                        t.subscribe { logIMessage(tag, "onNextValue$it") }
                    }

                    override fun onError(e: Throwable) {}
                })
    }

    private fun getBaseWindowTimeObserver(tag: String): Observer<Observable<Long>> =
            object : Observer<Observable<Long>> {
                override fun onComplete() {
                    logIMessage(tag, "onComplete")
                }

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: Observable<Long>) {
                    logIMessage(tag, "onNext")
                    t.subscribe { logIMessage(tag, "onNextValue$it") }
                }

                override fun onError(e: Throwable) {}
            }


    /**
     * 运行结果如下
     * com.ypz.rxjavademo I/operatorsScan: 从0加到1是:1
     * com.ypz.rxjavademo I/operatorsScan: 从0加到2是:3
     * com.ypz.rxjavademo I/operatorsScan: 从0加到3是:6
     * com.ypz.rxjavademo I/operatorsScan: 从0加到4是:10
     * com.ypz.rxjavademo I/operatorsScan: 从0加到5是:15
     * com.ypz.rxjavademo I/operatorsScan: 从0加到6是:21
     * com.ypz.rxjavademo I/operatorsScan: 从0加到7是:28
     * com.ypz.rxjavademo I/operatorsScan: 从0加到8是:36
     * com.ypz.rxjavademo I/operatorsScan: 从0加到9是:45
     * com.ypz.rxjavademo I/operatorsScan: 从0加到10是:55
     * 源码如下：
     * */
    private fun operatorsScan() {
        var message = "从0加到1是:"
        Observable.range(1, 10).scan { t1: Int, t2: Int ->
            message = "从0加到${t2}是:"
            t1 + t2
        }.subscribe {
            logIMessage("operatorsScan", "$message$it")
        }
    }
}
