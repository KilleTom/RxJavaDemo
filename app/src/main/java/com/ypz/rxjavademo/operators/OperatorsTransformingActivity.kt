package com.ypz.rxjavademo.operators

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.ypz.rxjavademo.R
import com.ypz.rxjavademo.base.ItemAdapter
import com.ypz.rxjavademo.base.ItemValue
import com.ypz.rxjavademo.base.logIMessage
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_operators.*

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
                        ItemValue("Operators-create", 0),
                        ItemValue("Operators-just", 1),
                        ItemValue("Operators-from", 2),
                        ItemValue("Operators-repeat", 3),
                        ItemValue("Operators-defer", 4),
                        ItemValue("Operators-interval", 5),
                        ItemValue("Operators-timer", 6)
                ),
                this@OperatorsTransformingActivity,
                Consumer {
                    logIMessage("clickValue", "$it")
                    when (it) {
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
}
