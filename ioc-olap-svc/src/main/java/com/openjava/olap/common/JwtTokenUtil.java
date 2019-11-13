package com.openjava.olap.common;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import sun.misc.BASE64Encoder;

import java.util.Date;

public class JwtTokenUtil {
    private static final Long EXP = 30 * 60 * 1000l;//半小时过期
    private static final SignatureAlgorithm JWT_ALG = SignatureAlgorithm.HS256;//加解密算法

    public static String generateToken(String key, String secret) {
        BASE64Encoder encoder = new BASE64Encoder();
        String base64Str = encoder.encode(secret.getBytes());
        //生成token
        String token = Jwts.builder()
                .setIssuer(key)
                .setExpiration(new Date(System.currentTimeMillis() + EXP))
                .signWith(JWT_ALG, base64Str)
                .compact();
        return token;
    }

}
