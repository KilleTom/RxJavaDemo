package com.ypz.rxjavademo.base;

/**
 * Created by 易庞宙 on 2019 2019/3/18 11:52
 * email: 1986545332@qq.com
 */
public class ItemValue {

    private String title;

    private Long status;

    public ItemValue(String title, Long status) {
        this.title = title;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public Long getStatus() {
        return status;
    }
}
