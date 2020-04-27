package com.coke.wolf.mq.namesrv;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/22 7:19 下午
 */
public class NameSrvBootStrap {

    public static void main(String[] args) {
        NameSrvController nameSrvController = new NameSrvController();
        nameSrvController.start();
    }
}
