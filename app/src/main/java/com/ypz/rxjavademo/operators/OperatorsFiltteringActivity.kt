package com.ypz.rxjavademo.operators

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.ypz.rxjavademo.R
import com.ypz.rxjavademo.base.*
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_operators.*
import java.util.concurrent.TimeUnit

/**
 *  Created by 易庞宙 on 2019 2019/4/1 11:09
 *  email: 1986545332@qq.com
 */
class OperatorsFiltteringActivity : AppCompatActivity() {
    private var itemAdapter: ItemAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_operators)
        itemAdapter = ItemAdapter(
                listOf(
                        ItemValue("Operators-take_count", 0),
                        ItemValue("Operators-take_time", 1),
                        ItemValue("Operators-takeLast_count", 2),
                        ItemValue("Operators-takeLast_time", 3),
                        ItemValue("Operators-takeLast_count&time", 4),
                        ItemValue("Operators-skip_count", 13),
                        ItemValue("Operators-skip_time", 14),
                        ItemValue("Operators-skipLast_count", 15),
                        ItemValue("Operators-skipLast_time", 16),
                        ItemValue("Operators-element", 6),
                        ItemValue("Operators-first", 5),
                        ItemValue("Operators-last", 7),
                        ItemValue("Operators-ignoreElements", 8),
                        ItemValue("Operators-debounce", 9),
                        ItemValue("Operators-distinct_easeUsed", 10),
                        ItemValue("Operators-window-指定数据组装时间段", 10),
                        ItemValue("Operators-filter", 12)
                ),
                this,
                Consumer {
                    when (it) {
                        0L -> operatorsTakeCount()
                        1L -> operatorsTakeTime()
                        2L -> operatorsTakeLastCount()
                        3L -> operatorsTakeLastTime()
                        4L -> operatorsTakeLastCountTime()
                        5L -> operatorsFirst()
                        6L -> operatorsElementAt()
                        7L -> operatorsLast()
                        8L -> operatorsIgnoreElements()
                        9L -> operatorsDebounce()
                        10L -> operatorsDistinct()
                        12L -> operatorsFilter()
                        13L -> operatorsSkip()
                        14L -> operatorsSkipTime()
                        15L -> operatorsSkipLast()
                        16L -> operatorsSkipLastTime()
                        else -> {

                        }
                    }
                })
        operators_rv.apply {
            layoutManager = LinearLayoutManager(this@OperatorsFiltteringActivity)
            setHasFixedSize(true)
            itemAdapter!!.setHasStableIds(true)
            adapter = itemAdapter
        }
        distinctKey()
    }

    /**
     * 运行结果如下：
     * com.ypz.rxjavademo I/operatorsTake_count: onSubscribe
     * com.ypz.rxjavademo I/operatorsTake_count: onNext:value1
     * com.ypz.rxjavademo I/operatorsTake_count: onNext:value2
     * com.ypz.rxjavademo I/operatorsTake_count: onNext:value3
     * com.ypz.rxjavademo I/operatorsTake_count: onComplete
     *
     * */
    private fun operatorsTakeCount() {
        Observable.rangeLong(1L, 5L).take(3).subscribe(ShowMessageBaseObserver<Long>("operatorsTake_count"))
    }


    /**
     * 运行结果如下:
     * 第一次运行：
     * com.ypz.rxjavademo I/operatorsTake_time: onSubscribe
     * com.ypz.rxjavademo I/operatorsTake_time: onNext:value0
     * com.ypz.rxjavademo I/operatorsTake_time: onNext:value1
     * com.ypz.rxjavademo I/operatorsTake_time: onComplete
     * 第二次运行：
     * com.ypz.rxjavademo I/operatorsTake_time: onSubscribe
     * com.ypz.rxjavademo I/operatorsTake_time: onNext:value0
     * com.ypz.rxjavademo I/operatorsTake_time: onNext:value1
     * com.ypz.rxjavademo I/operatorsTake_time: onNext:value2
     * com.ypz.rxjavademo I/operatorsTake_time: onComplete
     *
     * */
    private fun operatorsTakeTime() =
            Observable.interval(1, TimeUnit.SECONDS).take(3, TimeUnit.SECONDS).subscribe(ShowMessageBaseObserver<Long>("operatorsTake_time"))


    /**
     * 运行结果如下：
     * com.ypz.rxjavademo I/operatorsTakeLast_count: onSubscribe
     * com.ypz.rxjavademo I/operatorsTakeLast_count: onNext:value4
     * com.ypz.rxjavademo I/operatorsTakeLast_count: onNext:value5
     * com.ypz.rxjavademo I/operatorsTakeLast_count: onComplete
     *
     * */
    private fun operatorsTakeLastCount() =
            Observable.rangeLong(1L, 5L).takeLast(2).subscribe(ShowMessageBaseObserver("operatorsTakeLast_count"))


    /**
     * 运行结果如下:
     * com.ypz.rxjavademo I/operatorsTakeLast_time: onSubscribe
     * com.ypz.rxjavademo I/operatorsTakeLast_time: onNext:value3
     * com.ypz.rxjavademo I/operatorsTakeLast_time: onNext:value4
     * com.ypz.rxjavademo I/operatorsTakeLast_time: onNext:value5
     * com.ypz.rxjavademo I/operatorsTakeLast_time: onComplete
     *
     * */
    private fun operatorsTakeLastTime() =
            Observable.intervalRange(0, 6, 0, 1, TimeUnit.SECONDS).takeLast(3, TimeUnit.SECONDS).subscribe(ShowMessageBaseObserver<Long>("operatorsTakeLast_time"))

    /**
     * time和cout的组合就是延迟数据最后一刻的观测有可能会丢死部分数据
     * 延迟时间小于最后一段观测数据的时间总和的时候会丢失数据
     * 大于等于的时候不会丢失数据
     * 运行结果如下
     * com.ypz.rxjavademo I/operatorsTakeLast_count: onSubscribe
     * com.ypz.rxjavademo I/operatorsTakeLast_count: onNext:value4
     * com.ypz.rxjavademo I/operatorsTakeLast_count: onNext:value5
     * com.ypz.rxjavademo I/operatorsTakeLast_count: onComplete
     * */
    private fun operatorsTakeLastCountTime() =
            Observable.intervalRange(0, 6, 0, 1, TimeUnit.SECONDS).takeLast(2, 3, TimeUnit.SECONDS).subscribe(ShowMessageBaseObserver("operatorsTakeLast_count"))

    /**
     * 运行结果如下：
     * com.ypz.rxjavademo I/operatorsElementAt-使用角标索引: onSubscribe
     * com.ypz.rxjavademo I/operatorsElementAt-使用角标索引: onNext:value1
     * com.ypz.rxjavademo I/operatorsElementAt-默认角标用法: onSubscribe
     * com.ypz.rxjavademo I/operatorsElementAt-默认角标用法: onNext:value3
     * */
    private fun operatorsElementAt() {
        var justIndexDispoable: Disposable? = null
        Observable.rangeLong(1, 5).elementAt(0).subscribe(
                getBaseLongMaybeObserver("operatorsElementAt-使用角标索引", {
                    justIndexDispoable = it
                }, {
                    justIndexDispoable?.dispose()
                })
        )

        var usedDefinedIndexDispoable: Disposable? = null

        Observable.rangeLong(1, 5).elementAt(6, 3).subscribe(
                getBaseLongSingleObserver("operatorsElementAt-默认角标用法",
                        { usedDefinedIndexDispoable = it },
                        { usedDefinedIndexDispoable?.dispose() })
        )
    }

    /**
     * 运行结果如下：
     * com.ypz.rxjavademo I/operatorsFirst: onSubscribe
     * com.ypz.rxjavademo I/operatorsFirst: onNext:value1
     * */
    private fun operatorsFirst() {
        var disposable: Disposable? = null
        Observable.rangeLong(1, 5).first(2).subscribe(
                getBaseLongSingleObserver("operatorsFirst",
                        { disposable = it },
                        { disposable?.dispose() }))
    }

    /**
     * 运行结果如下:
     * com.ypz.rxjavademo I/operatorsLast: onSubscribe
     * com.ypz.rxjavademo I/operatorsLast: onNext:value5
     *
     * */
    private fun operatorsLast() {
        var disposable: Disposable? = null
        Observable.rangeLong(1, 5).last(1).subscribe(
                getBaseLongSingleObserver("operatorsLast",
                        { disposable = it },
                        { disposable?.dispose() }))
    }

    /**
     * 运行结果如下:
     * com.ypz.rxjavademo I/operatorsIgnoreElements: onComplete
     * */
    private fun operatorsIgnoreElements() {
        Observable.rangeLong(1, 5).ignoreElements().subscribe(
                { logIMessage("operatorsIgnoreElements", "onComplete") },
                {
                    logIMessage("operatorsIgnoreElements", "errorMessage:${it.message
                            ?: "unkown error"}")
                })
    }

    /**
     * 运行结果如下:
     * com.ypz.rxjavademo I/operatorsDebounce: onSubscribe
     * com.ypz.rxjavademo I/operatorsDebounce: onNext:value6
     * com.ypz.rxjavademo I/operatorsDebounce: onNext:value7
     * com.ypz.rxjavademo I/operatorsDebounce: onComplete
     *
     * */
    private fun operatorsDebounce() {
        Observable.create(ObservableOnSubscribe<Long> { emitter ->
            if (emitter.isDisposed) return@ObservableOnSubscribe
            for (index in 1L..7L) {
                emitter.onNext(index)
                Thread.sleep(index * 100L)
            }
            emitter.onComplete()
        })
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribe(ShowMessageBaseObserver<Long>("operatorsDebounce"))
    }

    /**
     * 运行结果：
     * com.ypz.rxjavademo I/operatorsDistinct-Easy用法: onSubscribe
     * com.ypz.rxjavademo I/operatorsDistinct-Easy用法: onNextValue:1
     * com.ypz.rxjavademo I/operatorsDistinct-Easy用法: onNextValue:2
     * com.ypz.rxjavademo I/operatorsDistinct-Easy用法: onNextValue:3
     * com.ypz.rxjavademo I/operatorsDistinct-Easy用法: onComplete
     *
     * */
    private fun operatorsDistinct() {
        Observable.just(1L, 2L, 3L, 1L, 2L).distinct()
                .subscribe(ShowMessageBaseObserver<Long>("operatorsDistinct-Easy用法"))
    }

    /**
     * 运行结果如下:
     * com.ypz.rxjavademo I/operatorsFilter: onSubscribe
     * com.ypz.rxjavademo I/operatorsFilter: onNext:value2
     * com.ypz.rxjavademo I/operatorsFilter: onNext:value4
     * com.ypz.rxjavademo I/operatorsFilter: onNext:value6
     * com.ypz.rxjavademo I/operatorsFilter: onNext:value8
     * com.ypz.rxjavademo I/operatorsFilter: onNext:value10
     * com.ypz.rxjavademo I/operatorsFilter: onComplete
     *
     * */
    private fun operatorsFilter() {
        Observable.rangeLong(1, 10).filter { (it % 2 == 0L) }.subscribe(ShowMessageBaseObserver<Long>("operatorsFilter"))
    }

    /**
     * 运行结果如下：
     * com.ypz.rxjavademo I/operatorsSkip-count: onSubscribe
     * com.ypz.rxjavademo I/operatorsSkip-count: onNext:value4
     * com.ypz.rxjavademo I/operatorsSkip-count: onNext:value5
     * com.ypz.rxjavademo I/operatorsSkip-count: onComplete
     *
     * */
    private fun operatorsSkip() {
        Observable.rangeLong(1, 5).skip(3).subscribe(
                ShowMessageBaseObserver<Long>("operatorsSkip-count")
        )
    }

    /**
     * 运行结果如下：
     * com.ypz.rxjavademo I/operatorsSkip_time: onSubscribe
     * com.ypz.rxjavademo I/operatorsSkip_time: onNext:value2
     * com.ypz.rxjavademo I/operatorsSkip_time: onNext:value3
     * com.ypz.rxjavademo I/operatorsSkip_time: onNext:value4
     * com.ypz.rxjavademo I/operatorsSkip_time: onComplete
     *
     * */
    private fun operatorsSkipTime() {
        Observable.interval(1, TimeUnit.SECONDS).take(5).skip(3, TimeUnit.SECONDS)
                .subscribe(ShowMessageBaseObserver<Long>("operatorsSkip_time"))
    }

    /**
     * 运行结果如下:
     * com.ypz.rxjavademo I/operatorsSkipLast-count: onSubscribe
     * com.ypz.rxjavademo I/operatorsSkipLast-count: onNext:value1
     * com.ypz.rxjavademo I/operatorsSkipLast-count: onNext:value2
     * com.ypz.rxjavademo I/operatorsSkipLast-count: onComplete
     *
     * */
    private fun operatorsSkipLast() {
        Observable.rangeLong(1, 5).skipLast(5)
                .subscribe(ShowMessageBaseObserver<Long>("operatorsSkipLast-count"))
    }

    /**
     * 运行结果如下:
     * com.ypz.rxjavademo I/operatorsSkipLast-time: onSubscribe
     * com.ypz.rxjavademo I/operatorsSkipLast-time: onNext:value0
     * com.ypz.rxjavademo I/operatorsSkipLast-time: onNext:value1
     * com.ypz.rxjavademo I/operatorsSkipLast-time: onComplete
     *
     * */
    private fun operatorsSkipLastTime() {
        Observable.interval(1, TimeUnit.SECONDS).take(6)
                .skipLast(4, TimeUnit.SECONDS).subscribe(
                        ShowMessageBaseObserver<Long>("operatorsSkipLast-time"))
    }

    /**
     * 运行结果如下：
     * com.ypz.rxjavademo I/operatorsDistinct-Key用法: onSubscribe
     * com.ypz.rxjavademo I/operatorsDistinct-Key用法: onNextValue:1
     * com.ypz.rxjavademo I/operatorsDistinct-Key用法: onNextValue:2
     * com.ypz.rxjavademo I/operatorsDistinct-Key用法: onNextValue:3
     * com.ypz.rxjavademo I/operatorsDistinct-Key用法: onNextValue:1
     * com.ypz.rxjavademo I/operatorsDistinct-Key用法: onNextValue:2
     * com.ypz.rxjavademo I/operatorsDistinct-Key用法: onComplete*/
    private fun distinctKey() {
        var times = 1L
        Observable
                .just(1L, 2L, 3L, 1L, 2L)
                .distinct {
                    times += 1
                    times
                }
                .subscribe(ShowMessageBaseObserver<Long>("operatorsDistinct-Key用法"))
    }
}
