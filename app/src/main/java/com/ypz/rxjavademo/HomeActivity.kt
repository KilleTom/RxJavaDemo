package com.ypz.rxjavademo


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ypz.rxjavademo.base.ItemAdapter
import com.ypz.rxjavademo.base.ItemValue
import com.ypz.rxjavademo.base.startAppCompatActivity
import com.ypz.rxjavademo.operators.*
import kotlinx.android.synthetic.main.activity_operators.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_operators)
        val itemAdapter = ItemAdapter(
                listOf(
                        ItemValue("RxJava入门理念基础知识", 0),
                        ItemValue("Operators-Creating详解", 1),
                        ItemValue("Operators-Transforming详解", 2),
                        ItemValue("Operators-Filtering详解", 3),
                        ItemValue("Operators-Condition_Boolean详解", 4),
                        ItemValue("Operators-Combining详解", 5),
                        ItemValue("Operators-Connect详解", 6)

                ),
                this
        ) {
            when (it) {
                0L -> startAppCompatActivity<MainActivity>()
                1L -> startAppCompatActivity<OperatorsCreatingActivity>()
                2L -> startAppCompatActivity<OperatorsTransformingActivity>()
                3L -> startAppCompatActivity<OperatorsFiltteringActivity>()
                4L -> startAppCompatActivity<OperatorsConditional_BooleanActivity>()
                5L -> startAppCompatActivity<OperatorsCombiningActivity>()
                6L -> startAppCompatActivity<OperatorsConnectActivity>()
                else -> {

                }
            }
        }

        operators_rv.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            setHasFixedSize(true)
            itemAdapter!!.setHasStableIds(true)

            adapter = itemAdapter
        }

    }


    fun easyStartActivty(activity: java.lang.Class<*>) {
        val intent = Intent(this, activity)
        startActivity(intent)
    }

}
