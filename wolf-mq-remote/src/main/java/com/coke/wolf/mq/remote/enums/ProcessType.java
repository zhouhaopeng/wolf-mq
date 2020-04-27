package com.coke.wolf.mq.remote.enums;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/23 3:31 下午
 */
public enum ProcessType {

    Client_Request(0, 9),
    Broker_Request(10, 19),
    NameSrv_Request(20, 39),

    NONE(-1, -1),
    ;

    private int begin;

    private int end;

    ProcessType(int begin, int end) {
        this.begin = begin;
        this.end = end;
    }

    public static ProcessType convert(int type) {

        ProcessType result = NONE;
        for (ProcessType processType : ProcessType.values()) {
            if (type >= processType.begin && type <= processType.end) {
                result = processType;
            }
        }
        return result;
    }
}
