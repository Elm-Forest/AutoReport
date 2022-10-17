package com.ctgu.autoreport.common.utils;

import cn.hutool.core.codec.BCD;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


/**
 * @author Elm Forest
 * @date 19/8/2022 下午6:31
 */

@Service
public class AesUtils {
    @Value("${aes.key}")
    private String key;

    public String encryptAes(String str) {
        return new SymmetricCrypto(SymmetricAlgorithm.AES, BCD.strToBcd(key)).encryptHex(str);
    }

    public String decryptAes(String str) {
        return new SymmetricCrypto(SymmetricAlgorithm.AES, BCD.strToBcd(key)).decryptStr(str, CharsetUtil.CHARSET_UTF_8);
    }
}