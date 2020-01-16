/**
 * History:
 * <author>         <time>           <version>
 * 杨子浩      2020/1/15 12:00     1.？.?
 */
package com.yzh.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Copyright (C),2020
 * FileName: RandomUtil
 *
 * @author 杨子浩
 * @version 1.0.0
 * @Date 2020/1/15 12:00
 * 描述:
 * @since 1.0
 */
public class RandomUtil {


    protected static final Log logger = LogFactory.getLog(RandomUtil.class);
    public static Byte INITIALIZE_TYPE = 0;
    public static Byte TO_THE_LOAD_TYPE = 1;

    /**
     * 获取一个随机数
     *
     * @return
     */
    public static int getMathRandomString(int num,int xia) {
        return (int) (Math.random() * num + xia);
    }

    public static void loggerout(Byte type, int num) {

        String msg = "data Authorization ";
        String msg1 = "";
        double msg2 = 1;

        StringBuilder sb = new StringBuilder();

        switch (type) {
            case 0: {
                msg1 = "(数据权限初始化中)  ";
                if (num == 1) {
                    msg2 = getMathRandomString(50,1);
                }
                if (num == 2) {
                    msg2 = getMathRandomString(20,60);
                }
                if (num == 3) {
                    msg1 = "(数据权限已初始化)  ";
                    msg2 = 100;
                }
                break;
            }
            case 1: {
                msg1 = "(数据权限加载中)  ";
                if (num == 1) {
                    msg2 = getMathRandomString(60,1);
                }
                if (num == 2) {
                    msg1 = "(数据权限加载失败) ";
                }
                if (num == 3) {
                    msg2 = getMathRandomString(20,70);
                }
                if (num == 4) {
                    msg1 = "(数据权限已加载)  ";
                    msg2 = 100;
                }
                break;
            }
        }

        if (msg2 >= 10)  msg2/=10;

        for (int i=1 ; i<=10; i++){
            if (i <= msg2){
                sb.append("#");
            }else {
                sb.append(" ");
            }
        }
        sb.append("   ("+(int)msg2*10+"/100)");
        logger.info(msg+msg1+sb.toString());
    }

}