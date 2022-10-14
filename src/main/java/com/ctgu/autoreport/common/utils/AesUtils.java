package com.ctgu.autoreport.common.utils;

import cn.hutool.core.codec.BCD;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.ctgu.autoreport.common.constant.EmailAccountConst;


/**
 * @author Elm Forest
 * @date 19/8/2022 下午6:31
 */
public class AesUtils {
    private static final SymmetricCrypto AES;

    static {
        AES = new SymmetricCrypto(SymmetricAlgorithm.AES, BCD.strToBcd(EmailAccountConst.AES_KEY));
    }

    public static String encryptAes(String str) {
        return AES.encryptHex(str);
    }

    public static String decryptAes(String str) {
        return AES.decryptStr(str, CharsetUtil.CHARSET_UTF_8);
    }
}