package com.ypz.rxjavademo.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ypz.rxjavademo.R;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Emitter;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by 易庞宙 on 2019 2019/3/18 11:42
 * email: 1986545332@qq.com
 */
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private ObservableEmitter<Long> emitter;
    private List<ItemValue> itemValues;
    private Context context;
    private LayoutInflater inflater;
    private Disposable disposable;


    public ItemAdapter(List<ItemValue> itemValues, @NonNull Context context, Consumer<Long> onNext) {
        this.itemValues = itemValues;
        this.context = context;
        inflater = LayoutInflater.from(context);
        ObservableOnSubscribe<Long> observableOnSubscribe = emitter -> ItemAdapter.this.emitter = emitter;
        Observable<Long> clickObservable = Observable.
                create(observableOnSubscribe).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread());
        if (disposable == null)
            disposable = clickObservable.subscribe(onNext);
    }

    public void cancleClickNext() {
        if (!disposable.isDisposed())
            disposable.dispose();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ItemViewHolder(inflater.inflate(R.layout.item_value, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
        itemViewHolder.initData(itemValues.get(i));
    }

    @Override
    public int getItemCount() {
        if (itemValues == null || itemValues.isEmpty())
            return 0;
        return itemValues.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        Emitter<MaterialButton> postEmitter = null;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void initData(ItemValue itemValue) {
            MaterialButton button = itemView.findViewById(R.id.item);
            button.setText(itemValue.getTitle());
            Observable.create((ObservableOnSubscribe<MaterialButton>) emitter -> postEmitter = emitter).
                    throttleFirst(500, TimeUnit.MILLISECONDS).
                    subscribe(materialButton -> ItemAdapter.this.emitter.onNext(itemValue.getStatus()));
            button.setOnClickListener(v -> postEmitter.onNext(button));
        }
    }
}
