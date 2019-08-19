package com.ypz.rxjavademo.operators

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ypz.rxjavademo.R
import com.ypz.rxjavademo.base.ItemAdapter
import com.ypz.rxjavademo.base.ItemValue
import com.ypz.rxjavademo.base.getBaseObsever
import com.ypz.rxjavademo.base.logIMessage
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_operators.*
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern


class OperatorsCombiningActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.ypz.rxjavademo.R.layout.activity_operators)
        val itemAdapter = ItemAdapter(
                mutableListOf<ItemValue>(
                        ItemValue("merage", 0L),
                        ItemValue("merageWith", 1L),
                        ItemValue("zip", 2L),
                        ItemValue("zipWith", 3L),
                        ItemValue("combineLatest", 4L),
                        ItemValue("withLatestFrom", 5L),
                        ItemValue("join", 6L),
                        ItemValue("startWith", 7L)
                ),
                this,
                Consumer {
                    when (it) {
                        0L -> operatorsMerage(0)
                        1L -> operatorsMerage(1)
                        2L -> operatorsZip(0)
                        3L -> operatorsZip(1)
                        4L -> operatorsCombineLatest()
                        5L -> operatorsLatestFrom()
                        6L -> operatorsJoin()
                        7L -> startWith()
                    }
                }
        )

        operators_rv.apply {
            layoutManager = LinearLayoutManager(this@OperatorsCombiningActivity)
            setHasFixedSize(true)
            itemAdapter!!.setHasStableIds(true)

            adapter = itemAdapter
        }


        var str = """${"\n"}com.ypz.rxjavademo I/startWith: onSubscribe
2019-05-16 10:00:18.236 15227-15227/com.ypz.rxjavademo I/startWith: onNextValue:from Array
2019-05-16 10:00:18.236 15227-15227/com.ypz.rxjavademo I/startWith: onNextValue:from Array2
2019-05-16 10:00:18.236 15227-15227/com.ypz.rxjavademo I/startWith: onNextValue:from Iterable
2019-05-16 10:00:18.236 15227-15227/com.ypz.rxjavademo I/startWith: onNextValue:,,,,,
2019-05-16 10:00:18.236 15227-15227/com.ypz.rxjavademo I/startWith: onNextValue:Other Observable
2019-05-16 10:00:18.236 15227-15227/com.ypz.rxjavademo I/startWith: onNextValue:Start2
2019-05-16 10:00:18.237 15227-15227/com.ypz.rxjavademo I/startWith: onNextValue:Start
2019-05-16 10:00:18.237 15227-15227/com.ypz.rxjavademo I/startWith: onNextValue:old"""
        str = Regex("^((?=\\.*)[^com]+)|((\\n)(?=\\.*)[^com]+)").replace(str, "\n* ")
        /*val p = Pattern.compile("^(?=\\.*)[^com]", Pattern.CASE_INSENSITIVE)
        val m = p.matcher(str)
        var count = 0
        while (m.find()) {
            count+=1
        }
        logIMessage("replaceC",count.toString())
        while (count>=0){
            logIMessage("replaceC-",count.toString())

            count-=1
        }*/


        logIMessage("replace", str)
    }


    fun operatorsMerage(resultType: Int) {

        val observableA = Observable
                .interval(1, TimeUnit.SECONDS).skip(1)
                .take(5).map { it * 20 }
                .subscribeOn(Schedulers.newThread())

        val observabeB = Observable
                .interval(2, TimeUnit.SECONDS)
                .skip(1)
                .take(1)
                .repeat(2)
                .subscribeOn(Schedulers.newThread())
        //根据选择的Type展示不同操作符
        when (resultType) {
            0 -> {
                Observable.merge(observableA, observabeB).subscribe(getBaseObsever("operators-Merage"))
            }
            1 -> {
                observableA.mergeWith(observabeB).subscribe(getBaseObsever("operators-MerageWith"))
            }
        }

    }

    /**
     * 运行效果如下:
     * com.ypz.rxjavademo I/operators-zip: onSubscribe
     * com.ypz.rxjavademo I/operators-zip: onNextValue:1A
     * com.ypz.rxjavademo I/operators-zip: onNextValue:2B
     * com.ypz.rxjavademo I/operators-zip: onNextValue:3C
     * com.ypz.rxjavademo I/operators-zip: onNextValue:4D
     * com.ypz.rxjavademo I/operators-zipWith: onSubscribe
     * com.ypz.rxjavademo I/operators-zipWith: onNextValue:1A
     * com.ypz.rxjavademo I/operators-zipWith: onNextValue:2B
     * com.ypz.rxjavademo I/operators-zipWith: onNextValue:3C
     * com.ypz.rxjavademo I/operators-zipWith: onNextValue:4D
     *
     * */
    fun operatorsZip(resultType: Int) {
        val observableA = Observable
                .interval(1, TimeUnit.SECONDS).skip(1)
                .take(5)
                .subscribeOn(Schedulers.newThread())

        val observabeB = Observable
                .interval(2, TimeUnit.SECONDS)
                .skip(1)
                .take(4)
                .map {
                    when (it) {
                        1L -> "A"
                        2L -> "B"
                        3L -> "C"
                        4L -> "D"
                        else -> "nothing"
                    }
                }
                .subscribeOn(Schedulers.newThread())

        when (resultType) {
            0 -> Observable
                    .zip(observableA, observabeB, BiFunction<Long, String, Any> { long, str -> "$long$str" })
                    .subscribe(getBaseObsever("operators-zip"))
            1 -> observableA
                    .zipWith(observabeB, BiFunction<Long, String, Any> { long, str -> "$long$str" })
                    .subscribe(getBaseObsever("operators-zipWith"))
        }
    }

    /**
     * 运行结果如下：
     * com.ypz.rxjavademo I/operators-combineLatest: onSubscribe
     * com.ypz.rxjavademo I/operators-combineLatest: onNextValue:1A
     * com.ypz.rxjavademo I/operators-combineLatest: onNextValue:2A
     * com.ypz.rxjavademo I/operators-combineLatest: onNextValue:2B
     * com.ypz.rxjavademo I/operators-combineLatest: onNextValue:2C
     * com.ypz.rxjavademo I/operators-combineLatest: onNextValue:2D
     * com.ypz.rxjavademo I/operators-combineLatest: onNextValue:3D
     * com.ypz.rxjavademo I/operators-combineLatest: onNextValue:4D
     * com.ypz.rxjavademo I/operators-combineLatest: onNextValue:5D
     *
     * */
    fun operatorsCombineLatest() {
        val observableA = Observable
                .interval(1, TimeUnit.SECONDS).skip(1)
                .take(5)
                .subscribeOn(Schedulers.newThread())

        val observabeB = Observable
                .intervalRange(1, 4, 2800, 250, TimeUnit.MILLISECONDS)
                .take(4)
                .map {
                    when (it) {
                        1L -> "A"
                        2L -> "B"
                        3L -> "C"
                        4L -> "D"
                        else -> "nothing"
                    }
                }
                .subscribeOn(Schedulers.newThread())
        Observable
                .combineLatest(observableA, observabeB, BiFunction<Long, String, Any> { int, str -> "$int$str" })
                .subscribe(getBaseObsever("operators-combineLatest"))
    }

    /**
     * 运行结果如下
     * com.ypz.rxjavademo I/operators-withLatestFrom: onSubscribe
     * com.ypz.rxjavademo I/operators-withLatestFrom: onNextValue:2A
     * com.ypz.rxjavademo I/operators-withLatestFrom: onNextValue:3D
     * com.ypz.rxjavademo I/operators-withLatestFrom: onNextValue:4D
     * com.ypz.rxjavademo I/operators-withLatestFrom: onNextValue:5D
     *
     * */
    fun operatorsLatestFrom() {
        val observableB = Observable
                .intervalRange(1, 4, 1000, 250, TimeUnit.MILLISECONDS)
                .take(4)
                .map {
                    when (it) {
                        1L -> "A"
                        2L -> "B"
                        3L -> "C"
                        4L -> "D"
                        else -> "nothing"
                    }
                }
                .subscribeOn(Schedulers.newThread())
        val observableA = Observable
                .intervalRange(1, 5, 0, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())

        observableA
                .withLatestFrom(observableB, BiFunction<Long, String, Any>({ t1, t2 -> "$t1$t2" }))
                .subscribe(getBaseObsever("operators-withLatestFrom"))
    }


    /**
     * 运行结果如下
     * com.ypz.rxjavademo I/operators-join: onSubscribe
     * com.ypz.rxjavademo I/operators-join-leftEnd: value:1
     * com.ypz.rxjavademo I/operators-join-leftEnd: value:2
     * com.ypz.rxjavademo I/operators-join-leftEnd: value:3
     * com.ypz.rxjavademo I/operators-join-rightEnd: value:5
     * com.ypz.rxjavademo I/operators-join: onNextValue:1:5
     * com.ypz.rxjavademo I/operators-join: onNextValue:2:5
     * com.ypz.rxjavademo I/operators-join: onNextValue:3:5
     * com.ypz.rxjavademo I/operators-join-rightEnd: value:6
     * com.ypz.rxjavademo I/operators-join: onNextValue:1:6
     * com.ypz.rxjavademo I/operators-join: onNextValue:2:6
     * com.ypz.rxjavademo I/operators-join: onNextValue:3:6
     * com.ypz.rxjavademo I/operators-join-rightEnd: value:7
     * com.ypz.rxjavademo I/operators-join: onNextValue:1:7
     * com.ypz.rxjavademo I/operators-join: onNextValue:2:7
     * com.ypz.rxjavademo I/operators-join: onNextValue:3:7
     * */
    fun operatorsJoin() {
        val observableA = Observable.range(1, 3)
        val observableB = Observable.range(5, 3)
        observableA
                .join(
                        observableB,
                        Function<Int, Observable<Int>> {
                            logIMessage("operators-join-leftEnd", "value:$it")
                            Observable.just(it).delay(100, TimeUnit.MILLISECONDS)
                        },
                        Function<Int, Observable<Int>> {
                            logIMessage("operators-join-rightEnd", "value:$it")
                            Observable.just(it).delay(100, TimeUnit.MILLISECONDS)
                        },
                        BiFunction<Int, Int, String> { t1, t2 ->
                            "$t1:$t2"
                        })
                .subscribe(getBaseObsever("operators-join"))
    }

    /**
     * 运行结果如下
     * com.ypz.rxjavademo I/startWith: onSubscribe
     * com.ypz.rxjavademo I/startWith: onNextValue:from Array
     * com.ypz.rxjavademo I/startWith: onNextValue:from Array2
     * com.ypz.rxjavademo I/startWith: onNextValue:from Iterable
     * com.ypz.rxjavademo I/startWith: onNextValue:,,,,,
     * com.ypz.rxjavademo I/startWith: onNextValue:Other Observable
     * com.ypz.rxjavademo I/startWith: onNextValue:Start2
     * com.ypz.rxjavademo I/startWith: onNextValue:Start
     * com.ypz.rxjavademo I/startWith: onNextValue:old
     * */
    fun startWith() {
        Observable.just("old")
                .startWith("Start")
                .startWith("Start2")
                .startWith(Observable.just("Other Observable"))
                .startWith(Arrays.asList("from Iterable", ",,,,,"))
                .startWithArray("from Array", "from Array2")
                .subscribe(getBaseObsever("startWith"))

    }
}
