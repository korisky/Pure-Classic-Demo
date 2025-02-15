package com.own.anno.demo.aspect;

import com.alibaba.fastjson2.JSONObject;
import com.roylic.struct.FuncResult;
import com.roylic.struct.RpcResult;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.Signature;
import org.springframework.util.StopWatch;

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

    private final StopWatch stopWatch;
    private final Object[] args;
    private final String[] splitName;
    private final String className;
    private final String methodName;
    private final String[] sign;

    public LogTmpParams(Signature signature, Object[] args, StopWatch stopWatch) {
        this.args = args;
        this.splitName = signature.getDeclaringTypeName().split("\\.");
        this.className = splitName[splitName.length - 1];
        this.methodName = signature.getName();
        this.sign = judgeSign(className);
        this.stopWatch = stopWatch;
    }

    /**
     * Input logs
     */
    public void beforeCallingLog() {
        if (null != stopWatch) {
            stopWatch.start();
        }
        // could not be changed, https://github.com/alibaba/fastjson2/issues/798
        Object loggingArgs = args;
        if (className.contains("controller") || className.contains("Controller")) {
            loggingArgs = this.args.length > 0 ? args[0] : "";
        }
        log.info(sign[0] + " [{}] input on [{}] with params:[{}]", className, methodName, JSONObject.toJSONString(loggingArgs));
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
        if (null != stopWatch && stopWatch.isRunning()) {
            stopWatch.stop();
        }
        // a. return error-enum already
        if (proceed instanceof Enum) {
            errorLog(proceed.toString());
            return;
        }
        // b. return call value with error-enum
        if (proceed instanceof RpcResult || proceed instanceof FuncResult) {
            Enum error = proceed instanceof RpcResult ?
                    ((RpcResult<?, ?>) proceed).getError() : ((FuncResult<?, ?>) proceed).getError();
            if (null == error) {
                Object data = proceed instanceof RpcResult ?
                        ((RpcResult<?, ?>) proceed).getData() : ((FuncResult<?, ?>) proceed).getData();
                if (null == stopWatch) {
                    log.info(sign[1] + " [{}] output on [{}] with params:[{}]", className, methodName, JSONObject.toJSONString(data));
                } else {
                    log.info(sign[1] + " [{}] output on [{}] with params:[{}], PERFORMANCE: {}ms",
                            className, methodName, JSONObject.toJSONString(data), stopWatch.getTotalTimeMillis());
                }

            } else {
                errorLog(error.toString());
            }
            return;
        }
        // c. return correct msg
        if (null == stopWatch) {
            log.info(sign[1] + " [{}] output on [{}] with params:[{}]", className, methodName, JSONObject.toJSONString(proceed));
        } else {
            log.info(sign[1] + " [{}] output on [{}] with params:[{}], PERFORMANCE: {}ms",
                    className, methodName, JSONObject.toJSONString(proceed), stopWatch.getTotalTimeMillis());
        }

    }

    /**
     * For getting correct sign
     */
    private static String[] judgeSign(String className) {
        String str = className.toLowerCase(Locale.ROOT);
        return str.contains("controller") ? CONTROLLER_SIGN : (str.contains("service") ? SERVICE_SIGN : INNER_SIGN);
    }

}
