package com.esteel.common.core;

/**
 * @version 1.0.0
 * @ClassName ErrorCode.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日  13:29
 */
public class ErrorCode {

    private String code ;

    private String message ;

    public ErrorCode (String code,String message){
        this.code = code ;
        this.message = message ;
    }

    public String getCode (){
        return code;
    }

    public String getMessage(){
        return message ;
    }

    @Override
    public String toString(){
        return String.format("%s：%s",code,message);
    }

    public interface SystemError {
        ErrorCode SERVER_INTERNAL_ERROR = new ErrorCode("500001", "( >﹏<。)～ 系统正忙，请稍后再试 (500001)");
        ErrorCode DUBBO_UN_AVAILABLE = new ErrorCode("500002", "( >﹏<。)～ 系统正忙，请稍后再试 (500002)");
        ErrorCode DUBBO_ERROR = new ErrorCode("500003", "( >﹏<。)～ 系统正忙，请稍后再试 (500003)");
    }

    public interface AuthenticationError {
        ErrorCode UNAUTHORIZED = new ErrorCode("400001", "[400001] 非官方资源");
        ErrorCode UNKNOWN_CREDENTIAL = new ErrorCode("400002", "[400002] 非官方资源");
        ErrorCode TOKEN_EXPIRED = new ErrorCode("400003", "[400003] 非官方资源");
        ErrorCode INVALID_TOKEN = new ErrorCode("400004", "[400004] 非官方资源");
        ErrorCode INVALID_CREDENTIAL = new ErrorCode("400005", "[400005] 非官方资源");
        ErrorCode TOKEN_NOT_FOUND = new ErrorCode("400006", "[400006] 非官方资源");
    }

    public interface ResourceError {
        ErrorCode CAN_NOT_ACCESS = new ErrorCode("410001", "资源不可访问");
        ErrorCode RESOURCE_EXPORTING_FAILED = new ErrorCode("410002", "导出失败，请重新再试");
        ErrorCode RESOURCE_EXPORTING = new ErrorCode("410003", "导出中，请重新再试");
        ErrorCode INVALID_RESOURCE = new ErrorCode("410004", "资源类型错误，请联系管理员");
        ErrorCode RESOURCE_NOT_FOUND = new ErrorCode("410005", "资源不存在");
    }

    public interface ApiError {
        ErrorCode PARAMETER_VALIDATION_ERROR = new ErrorCode("600001", "[600001] 参数验证失败");
        ErrorCode RPC_CALL_VALIDATION_ERROR = new ErrorCode("600002", "[600002] 参数验证失败");
        ErrorCode NO_RESPONSE_ERROR = new ErrorCode("600003", "调用RPC接口无响应");
        ErrorCode NO_RESPONSE_STATUS_ERROR = new ErrorCode("600004", "调用RPC接口无响应状态码");
        ErrorCode NO_RESPONSE_ERROR_ERROR = new ErrorCode("600005", "调用RPC接口无响应错误码");
        ErrorCode INVALID_SIGNATURE = new ErrorCode("600006", "签名验证失败");
        ErrorCode REQUEST_TOO_BUSY = new ErrorCode("600007", "请求太频繁");
        ErrorCode DENIED_BY_RULE = new ErrorCode("600008", "请求被拒");
        ErrorCode UNSUPPORTED_CHARACTERS = new ErrorCode("600009", "检测到非法字符: %s, 请删除");
    }

    public interface RoleError {
        ErrorCode ROLE_EXIST_ERROR = new ErrorCode("700001", "角色已存在，请确认角色名");
        ErrorCode ROLE_ADD_ERROR = new ErrorCode("700002", "添加角色失败");
        ErrorCode ROLE_NOT_EXIST_ERROR = new ErrorCode("700003", "根据角色ID为查询到对应角色，请确认参数是否正确");
    }
}
