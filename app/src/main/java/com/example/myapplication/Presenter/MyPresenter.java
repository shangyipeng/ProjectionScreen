package com.example.myapplication.Presenter;

import com.example.myapplication.Contract.ContractInterface;
import com.example.myapplication.Model.MyModel;

import java.util.Map;

/**
 * 作者:今夕何夕
 * 时间:${data}
 * Description:这个是注释
 */


public class MyPresenter<T> implements ContractInterface.Presenter{
    T tt;
    MyModel myModel;

    public MyPresenter(T t) {
        myModel = new MyModel();
        this.tt = t;
    }

    @Override
    public void presenter(Map<String, Object> map, String Url,String TYPE,String token) {
        myModel.Model(map, Url, TYPE,new MyModel.MyCallBreak() {
            @Override
            public void sressco(Object o) {
                ContractInterface.View view = ( ContractInterface.View) tt;
                view.View((String) o);
            }
        },token);
    }
}



