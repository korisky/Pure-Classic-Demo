package com.own.spring.demo.hash;


import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Here the hash function idea is from:
 * https://probablydance.com/2018/06/16/fibonacci-hashing-the-optimization-that-the-world-forgot-or-a-better-alternative-to-integer-modulo/
 */
public class FibonacciHashFunction {


    public class HashObj {

        private Integer id;

        @Override
        public int hashCode() {

            // 64bit/8bytes obj -> a = 2^bitSize / eta
            BigDecimal a = new BigDecimal("2")
                    .multiply(BigDecimal.TEN.pow(64))
                    .multiply(new BigDecimal("0.618"));

            // in hash table -> a*k mod 2^
            return a.multiply(new BigDecimal(id)).intValue();
        }
    }


    public static void main(String[] args) {
        BigDecimal bigDecimal = new BigDecimal("2").multiply(BigDecimal.TEN.pow(64));
        BigDecimal multiply = bigDecimal.multiply(new BigDecimal("0.618")); // much better
        BigDecimal divide = bigDecimal.divide(new BigDecimal("1.618"), 18, RoundingMode.FLOOR);
        System.out.println(multiply.divide(BigDecimal.TEN.pow(64)).stripTrailingZeros());
        System.out.println(divide.divide(BigDecimal.TEN.pow(64)).stripTrailingZeros());
    }


}
