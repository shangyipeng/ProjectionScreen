package com.example.myapplication.Contract;


import java.util.Map;

/**
 * 作者:今夕何夕
 * 时间:${data}
 * Description:过渡--连接V层和M层
 */
public class ContractInterface {
    //登录
    public interface View{
        void View(String o);
    }
    public interface Presenter{
        void presenter(Map<String,Object> map,String Url,String Type,String token);
    }
}
