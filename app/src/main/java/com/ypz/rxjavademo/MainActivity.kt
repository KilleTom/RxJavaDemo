package com.ypz.rxjavademo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ypz.rxjavademo.base.logIMessage
import io.reactivex.*
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        create_1.setOnClickListener { createExample() }
        create_2.setOnClickListener { createExample2() }
        lifecyle.setOnClickListener { lifeCycleExample() }
        cold.setOnClickListener { coldObservable() }
        cth.setOnClickListener { coldTobeHot() }
        maybeObserver()
        resources
    }

    /**
     * error or next
     * onComplete
     * */
    private fun createExample() {
        Observable
                .just("this is create Demo")
                .subscribe(
                        { logIMessage("createExample", "msg:$it") },
                        { logIMessage("createExample", "error:${it.message ?: "unkown Error"}") },
                        { logIMessage("createExample", "onComplete") }
                )
    }

    /**
     * subscribe
     * error or next
     * onComplete
     * */
    private fun createExample2() {
        Observable
                .just("this is create Demo")
                .subscribe(
                        { logIMessage("createExample2", "msg:$it") },
                        { logIMessage("createExample2", "error:${it.message ?: "unkown Error"}") },
                        { logIMessage("createExample2", "onComplete") },
                        { logIMessage("createExample2", "subscribe") }
                )
    }

    private fun lifecyleExampleLog(message: String) = logIMessage("lifeCycleExample", message)

    /***/
    private fun lifeCycleExample() {
        Observable.just("this is lifeCycleExample")
                .doOnSubscribe {
                    //发生订阅后回调的的方法
                    lifecyleExampleLog("doOnSubscribe")
                }.doOnLifecycle(
                        //订阅后是否可以取消
                        { lifecyleExampleLog("doOnLifecycle is disposed${it.isDisposed}") },
                        { lifecyleExampleLog("doOnLifecycle is run") }
                )
                .doOnEach {
                    //Observable每次发送数据都会执行这个方法
                    lifecyleExampleLog("doOnEach:${when {
                        it.isOnNext -> "onNext"
                        it.isOnError -> "onError"
                        it.isOnComplete -> "onComplete"
                        else -> "nothing"
                    }}")
                }.doOnNext {
                    //在onNext前调用
                    lifecyleExampleLog("doOnNext:$it")
                }.doAfterNext {
                    //在onNext后调用
                    lifecyleExampleLog("doAfterNext:$it")
                }.doOnError {
                    //在onError前调用
                    lifecyleExampleLog("doOnError:${it.message ?: "unkown error message"}")
                }.doOnComplete {
                    //正常调用onComplete时被调用
                    lifecyleExampleLog("doOnComplete")
                }.doFinally {
                    //终止后调用不管是正常执行或者异常终止,优先于doAfterTerminate执行
                    lifecyleExampleLog("doFinally")
                }.doAfterTerminate {
                    //当onComplete或者onError执行后被触发
                    lifecyleExampleLog("doAfterTerminate")
                }.subscribe { lifecyleExampleLog("onNext value:$it") }
    }

    private fun coldObservable() {
        /**
         * subscribe1与subscribe2结果不一致.两者独立,收发顺序不一致导致并发会存在问题
         * */
        val observable = Observable.create(ObservableOnSubscribe<Long> { emitter ->
            Observable.interval(10L, TimeUnit.MILLISECONDS, Schedulers.computation())
                    .take(Long.MAX_VALUE)
                    .subscribe {
                        if (it <= 10) emitter.onNext(it)
                        else emitter.onComplete()
                    }
        }).observeOn(Schedulers.newThread())
        observable.subscribe({ Log.i("coldObservable", "subscribe-1value:$it") })
        observable.subscribe { Log.i("coldObservable", "subscribe-2value:$it") }
    }

    private fun coldTobeHot() {
        /**
         * 使用publish操作转为hot（ConnectableObservable）保证事件发送按订阅先后统一发送解决并发问题
         * 只有使用了connect操作符才后真正的发送数据
         * 用这种方式保证线程安全发送数据
         * */
        val observable = Observable.create(ObservableOnSubscribe<Long> { emitter ->
            Observable.interval(10L, TimeUnit.MILLISECONDS, Schedulers.computation())
                    .take(Long.MAX_VALUE)
                    .subscribe {
                        if (it <= 10) emitter.onNext(it)
                        else emitter.onComplete()
                    }
        }).observeOn(Schedulers.newThread()).publish()
        observable.connect()
        observable.subscribe({ Log.i("coldObservable", "subscribe-1value:$it") })
        observable.subscribe { Log.i("coldObservable", "subscribe-2value:$it") }
    }

    /**
     *
     * Subject和Processor的作用相同。
     * Processor是RxJava2.x新增的类，继承自Flowable，支持背压控制(BackPresure)，而Subject则不支持背压控制。
     *
     * Subject既是Observable，又是Observer（Subscriber）。
     * 这一点可以从Subject的源码上看到，继承自Observable，实现Observer。
     *
     * Subject作为观察者,可以订阅目标Cold Observable,使对方开始发送事件。
     * 同时它又作为Observable转发或者发送新的事件,让ColdObservable借助Subject转换为Hot Observable。
     *
     * Subject并不是线程安全的, 如果想要其线程安全, 则需要调用toSerialized()方法
     * (在 RxJava 1.x中还可以用SerializedSubject代替Subject,但是在RxJava 2.x之后,SerializedSubject不再是一个public class)。
     * */
    private fun subjectTobeHot() {
        val observable = Observable.create(ObservableOnSubscribe<Long> { emitter ->
            Observable.interval(10L, TimeUnit.MILLISECONDS, Schedulers.computation())
                    .take(Long.MAX_VALUE)
                    .subscribe {
                        if (it <= 10) emitter.onNext(it)
                        else emitter.onComplete()
                    }
        }).observeOn(Schedulers.newThread())
        val subject = PublishSubject.create<Long>()
        observable.subscribe(subject)
        //使用toSerialized保证线程安全
        subject.toSerialized()
        subject.subscribe({ Log.i("coldObservable", "subscribe-1value:$it") })
        subject.subscribe { Log.i("coldObservable", "subscribe-2value:$it") }
    }

    /**
     * ReactiveX官网的解释是： make a Connectable Observable behave like an ordinary Observable，
     * RefCount 操作 符 把 从 一个 可连接 的 Observable 连接 和 断开 的 过程 自动化 了。
     * 它 操作 一个 可连接 的 Observable， 返回 一个 普通 的 Observable。
     * 当 第一个 订阅 者/ 观察者 订阅 这个 Observable 时， RefCount 连接 到 下层 的 可连接 Observable。
     * RefCount 跟踪 有 多少 个 观察者 订阅 它， 直到 最后 一个 观察者 完成， 才 断开 与 下层 可连接 Observable 的 连接。
     * 如果 所有 的 订阅 者/ 观察者 都 取消 订阅 了， 则 数据 流 停止； 如果 重新 订阅， 则 重新 开始 数据 流。
     * */
    private fun hotTobeCold() {
        Thread.sleep(500L)
        val consumer1 = Consumer<Long> { logIMessage("hotTobeCold-consumer1", "value$it") }
        val consumer2 = Consumer<Long> { logIMessage("hotTobeCold-consumer2", "value$it") }
        val connectableObservable = Observable.create { emitter: ObservableEmitter<Long> ->
            Observable.interval(10, TimeUnit.MILLISECONDS, Schedulers.computation()).take(Integer.MAX_VALUE.toLong())
                    .subscribe {
                        emitter.onNext(it)
                    }
        }.observeOn(Schedulers.newThread()).publish()
        connectableObservable.connect()
        val observable = connectableObservable.refCount()
        var dispose1 = observable.subscribe(consumer1)
        var dispose2 = observable.subscribe(consumer2)
        Thread.sleep(50L)
        dispose1.dispose()
        dispose2.dispose()
        logIMessage("hotTobeCold", "resetDataSource")
        dispose1 = observable.subscribe(consumer1)
        dispose2 = observable.subscribe(consumer2)
        Thread.sleep(50L)
        dispose1.dispose()
        dispose2.dispose()
    }

    private fun hotTobeCold2() {
        val consumer1 = Consumer<Long> { logIMessage("hotTobeCold2-consumer1", "value$it") }
        val consumer2 = Consumer<Long> { logIMessage("hotTobeCold2-consumer2", "value$it") }
        val consumer3 = Consumer<Long> { logIMessage("hotTobeCold2-consumer3", "value$it") }
        val connectableObservable = Observable.create { emitter: ObservableEmitter<Long> ->
            Observable.interval(10, TimeUnit.MILLISECONDS, Schedulers.computation()).take(Integer.MAX_VALUE.toLong())
                    .subscribe {
                        emitter.onNext(it)
                    }
        }.observeOn(Schedulers.newThread()).publish()
        connectableObservable.connect()
        val observable = connectableObservable.refCount()
        var dispose1 = observable.subscribe(consumer1)
        var dispose2 = observable.subscribe(consumer2)
        var dispose3 = observable.subscribe(consumer3)
        Thread.sleep(30L)
        dispose1.dispose()
        dispose2.dispose()
        logIMessage("hotTobeCold2", "consumer2 reset subscribe")
        dispose2 = observable.subscribe(consumer2)
        Thread.sleep(20L)
        dispose3.dispose()
        dispose2.dispose()
    }

    private fun singleObserver() {
        /**
         *
         * 由于使用lambda表达式代码简化所以会在这里描述一些简化代码对应的代码块
         * create<String>{} ==> SingleOnSubscribe<String>{}
         *
         * */
        Single.create<String> {
            //这里操作是否成功或者失败的发送
            it.onSuccess("success")
            it.onError(Throwable("this is a error"))
        }.subscribe({
            //成功的操作
            logIMessage("singleObserver", "success-value:$it")
        }, {
            //失败操作
            logIMessage("singleObserver", "error-value:${it.message?:""}")
        })
        //更加简洁实用方式 使用BiConsumer
        Single.create<String> {
            it.onSuccess("success")
            it.onError(Throwable("this is a error"))
        }.subscribe({ t1, t2 ->
            logIMessage("singleObserver", "success-value:$t1")
            logIMessage("singleObserver", "error-value:${t2.message?:""}")
        })
    }

    private fun completableObserver(){
        Completable.create{
            try {
                TimeUnit.SECONDS.sleep(2)
                it.onComplete()
            } catch (e: Exception) {
                it.onError(e)
            }
        }.andThen(Observable.range(0,5))
                .subscribe {
                    logIMessage("andThenAction","value:$it")
                }
    }

    private fun maybeObserver(){
        //仅仅会打印sucess1
        Maybe.create<String> {
            emitter: MaybeEmitter<String> ->
            emitter.onSuccess("sucess1")
            emitter.onSuccess("sucess2")
            emitter.onSuccess("sucess2")
        }.subscribe {
            logIMessage("MaybeExample1","value:$it")
        }
        //success事件不会发送的情况
        Maybe.create<String> {
            it.onComplete()
            it.onSuccess("affter complete sucess")
        }.subscribe(
                {
                    logIMessage("MaybeExample2","value:$it")
                },
                {
                    logIMessage("MaybeExample2","value:${it.message?:"unknown error"}")
                },
                {
                    logIMessage("MaybeExample2","value:onComplete")
                }
        )
    }

}
