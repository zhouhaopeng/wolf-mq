package com.coke.wolf.common;

import com.coke.wolf.common.utils.NumberUtils;
import org.junit.Test;

/**
 * @author Haopeng Zhou
 * @version 1.0
 * @date 2020/4/26 11:11 下午
 */
public class NumberUtilTest {

    @Test
    public void test() {
        for (int i = 0; i < 10; i++) {
           System.out.println(NumberUtils.random(10));
        }
    }
}
