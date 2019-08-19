package com.ypz.rxjavademo.operators

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ypz.rxjavademo.R
import com.ypz.rxjavademo.base.ItemAdapter
import com.ypz.rxjavademo.base.ItemValue
import com.ypz.rxjavademo.base.getBaseObsever
import com.ypz.rxjavademo.base.getBaseSingleObserver
import io.reactivex.Observable
import io.reactivex.functions.BiPredicate
import io.reactivex.functions.Consumer
import io.reactivex.functions.Predicate
import kotlinx.android.synthetic.main.activity_operators.*
import java.util.concurrent.TimeUnit

/**
 * Created by 易庞宙 on 2019 2019/4/11 15:25
 * email: 1986545332@qq.com
 */
class OperatorsConditional_BooleanActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_operators)
        val itemAdapter = ItemAdapter(
                listOf(
                        ItemValue("all", 0),
                        ItemValue("contains", 1),
                        ItemValue("amb", 2),
                        ItemValue("defaultIfEmpty", 3),
                        ItemValue("sikpUnitl", 4),
                        ItemValue("sikpWhile", 5),
                        ItemValue("takeUntil", 6),
                        ItemValue("takeUntil", 7)
                ),
                this,
                Consumer {
                    when (it) {
                        0L -> operatorsAll()
                        1L -> operatorsContains()
                        2L -> operatorsAmb()
                        3L -> operatorsDefaultIfEmpty()
                        4L -> operatorsSikpUnitl()
                        5L -> operatorsSikpWhile()
                        6L -> operatorsTakeUntil()
                        7L -> operatorsTakeWhen()
                    }
                }
        )
        operators_rv.apply {
            layoutManager = LinearLayoutManager(this@OperatorsConditional_BooleanActivity)
            adapter = itemAdapter
        }
        //operatorsAll()
        //operatorsContains()
        //operatorsSikpWhile()
        //operatorsTakeUntil()
        // operatorsTakeWhen()
    }

    /**
     * 运行结果如下:
     * com.ypz.rxjavademo I/operatorsAll: onSubscribe
     * com.ypz.rxjavademo I/operatorsAll: onNext:value-true
     * */
    private fun operatorsAll() =
            Observable.range(1, 4).all { it < 5 }.subscribe(getBaseSingleObserver("operatorsAll"))


    /**
     * 运行结果如下:
     * com.ypz.rxjavademo I/operatorsAmb: onSubscribe
     * com.ypz.rxjavademo I/operatorsAmb: onNextValue:5
     *
     * */
    private fun operatorsAmb() {
        Observable.amb(mutableListOf(
                Observable.just(1).delay(1, TimeUnit.SECONDS),
                Observable.just(5)
        )).subscribe(getBaseObsever("operatorsAmb"))

    }

    /**
     * 运行结果如下:
     * com.ypz.rxjavademo I/operatorsContains: onSubscribe
     * com.ypz.rxjavademo I/operatorsContains: onNext:value-true
     * */
    private fun operatorsContains() =
            Observable.range(1, 6).contains(3).subscribe(getBaseSingleObserver("operatorsContains"))


    /**
     * 运行结果如下:
     * com.ypz.rxjavademo I/operatorsContains: onSubscribe
     * com.ypz.rxjavademo I/operatorsDefaultIfEmpty: onNextValue:5
     * */
    private fun operatorsDefaultIfEmpty() =
            Observable.empty<Long>().defaultIfEmpty(5).subscribe(getBaseObsever("operatorsDefaultIfEmpty"))

    /**
     * 运行结果如下:
     * com.ypz.rxjavademo I/operatorsSequenceEqual-直接比较: onSubscribe
     * com.ypz.rxjavademo I/operatorsSequenceEqual-直接比较: onNext:value-true
     * com.ypz.rxjavademo I/operatorsSequenceEqual-BiPredicate: onSubscribe
     * com.ypz.rxjavademo I/operatorsSequenceEqual-BiPredicate: onNext:value-false
     *
     * */
    private fun operatorsSequenceEqual() {

        Observable.sequenceEqual(
                Observable.range(1, 5),
                Observable.range(1, 5)
        ).subscribe(getBaseSingleObserver<Boolean>("operatorsSequenceEqual-直接比较"))

        Observable.sequenceEqual(
                Observable.range(1, 5),
                Observable.range(1, 5),
                object : BiPredicate<Int, Int> {
                    override fun test(t1: Int, t2: Int): Boolean {
                        return (t1) == (t2 - 1)
                    }
                }
        ).subscribe(getBaseSingleObserver<Boolean>("operatorsSequenceEqual-BiPredicate"))
    }

    /**
     * 运行结果如下:
     * com.ypz.rxjavademo I/operatorsSikpUnitl: onSubscribe
     * com.ypz.rxjavademo I/operatorsSikpUnitl: onNextValue:5
     * com.ypz.rxjavademo I/operatorsSikpUnitl: onNextValue:6
     * com.ypz.rxjavademo I/operatorsSikpUnitl: onNextValue:7
     * com.ypz.rxjavademo I/operatorsSikpUnitl: onNextValue:8
     * com.ypz.rxjavademo I/operatorsSikpUnitl: onNextValue:9
     *
     * */
    private fun operatorsSikpUnitl() {
        Observable.intervalRange(1, 9, 0, 1, TimeUnit.SECONDS).skipUntil(Observable.timer(4, TimeUnit.SECONDS)).subscribe(getBaseObsever("operatorsSikpUnitl"))
    }

    /**
     * 运行结果如下:
     * com.ypz.rxjavademo I/operatorsSikpWhile: onSubscribe
     * com.ypz.rxjavademo I/operatorsSikpWhile: onNextValue:4
     * com.ypz.rxjavademo I/operatorsSikpWhile: onNextValue:5
     * com.ypz.rxjavademo I/operatorsSikpWhile: onNextValue:6
     * com.ypz.rxjavademo I/operatorsSikpWhile: onNextValue:7
     * com.ypz.rxjavademo I/operatorsSikpWhile: onNextValue:8
     * com.ypz.rxjavademo I/operatorsSikpWhile: onNextValue:9
     *
     * */
    private fun operatorsSikpWhile() {
        Observable.range(1, 9).skipWhile(object : Predicate<Int> {
            override fun test(t: Int): Boolean = (t <= 3)
        }).subscribe(getBaseObsever("operatorsSikpWhile"))
    }

    /**
     * 运行结果如下:
     * com.ypz.rxjavademo I/operatorsTakeUntil: onSubscribe
     * com.ypz.rxjavademo I/operatorsTakeUntil: onNextValue:1
     * com.ypz.rxjavademo I/operatorsTakeUntil: onNextValue:2
     * com.ypz.rxjavademo I/operatorsTakeUntil: onNextValue:3
     * */
    private fun operatorsTakeUntil() {
        Observable.range(1, 9).takeUntil(object : Predicate<Int> {
            override fun test(t: Int): Boolean = t == 3
        }).subscribe(getBaseObsever("operatorsTakeUntil"))
    }


    /**
     * 运行结果如下:
     * com.ypz.rxjavademo I/operatorsTakeUntil: onSubscribe
     * com.ypz.rxjavademo I/operatorsTakeUntil: onNextValue:1
     * com.ypz.rxjavademo I/operatorsTakeUntil: onNextValue:2
     * com.ypz.rxjavademo I/operatorsTakeUntil: onNextValue:3
     * */
    private fun operatorsTakeWhen() {
        Observable.range(1, 9).takeWhile(object : Predicate<Int> {
            override fun test(t: Int): Boolean = t <= 3
        }).subscribe(getBaseObsever("operatorsTakeUntil"))
    }
}
