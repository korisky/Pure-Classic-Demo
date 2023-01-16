package com.own.spring.demo.aspect;

import com.alibaba.fastjson2.JSONObject;
import com.roylic.struct.CallValue;
import com.roylic.struct.ReturnValue;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.Signature;

import java.util.Locale;

/**
 * Aspect logging needed
 *
 * @author Roylic
 * 2022/6/29
 */
@Slf4j
public class LogTmpParams {

    private static final String[] CONTROLLER_SIGN = new String[]{">>>", "<<<"};
    private static final String[] SERVICE_SIGN = new String[]{">>>>>", "<<<<<"};
    private static final String[] INNER_SIGN = new String[]{">>>>>>>", "<<<<<<<"};

    Object[] args;
    String[] splitName;
    String className;
    String methodName;
    String[] sign;

    public LogTmpParams(Signature signature, Object[] args) {
        this.args = args;
        this.splitName = signature.getDeclaringTypeName().split("\\.");
        this.className = splitName[splitName.length - 1];
        this.methodName = signature.getName();
        this.sign = judgeSign(className);
    }

    /**
     * Input logs
     */
    public void beforeCallingLog() {
        // could not be changed, https://github.com/alibaba/fastjson2/issues/798
        log.info(sign[0] + " [{}] input on [{}] with params:[{}]", className, methodName, args);
    }

    /**
     * Error logs
     */
    public void errorLog(String errMsg) {
        log.error(sign[1] + " [{}] output on [{}] by Error:[{}]", className, methodName, errMsg);
    }

    /**
     * Output logs
     */
    public void afterCallingLog(Object proceed) {
        // a. return error-enum already
        if (proceed instanceof Enum) {
            errorLog(proceed.toString());
            return;
        }
        // b. return call value with error-enum
        if (proceed instanceof CallValue || proceed instanceof ReturnValue) {
            Enum error = proceed instanceof CallValue ?
                    ((CallValue<?, ?>) proceed).getError() : ((ReturnValue<?, ?>) proceed).getError();
            if (null == error) {
                Object data = proceed instanceof CallValue ?
                        ((CallValue<?, ?>) proceed).getData() : ((ReturnValue<?, ?>) proceed).getData();
                log.info(sign[1] + " [{}] output on [{}] with params:[{}]", className, methodName, JSONObject.toJSONString(data));
            } else {
                errorLog(error.toString());
            }
            return;
        }
        // c. return correct msg
        log.info(sign[1] + " [{}] output on [{}] with params:[{}]", className, methodName, JSONObject.toJSONString(proceed));
    }

    /**
     * For getting correct sign
     */
    private static String[] judgeSign(String className) {
        String str = className.toLowerCase(Locale.ROOT);
        return str.contains("controller") ? CONTROLLER_SIGN : (str.contains("service") ? SERVICE_SIGN : INNER_SIGN);
    }

}
