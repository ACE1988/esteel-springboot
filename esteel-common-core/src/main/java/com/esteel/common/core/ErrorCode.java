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
        ErrorCode SERVER_INTERNAL_ERROR = new ErrorCode("500001", "( >﹏<。)～ Sistem sedang sibuk sekarang, mohon coba kembali (500001)");
        ErrorCode DUBBO_UN_AVAILABLE = new ErrorCode("500002", "( >﹏<。)～ Sistem sedang sibuk sekarang, mohon coba kembali (500002)");
        ErrorCode DUBBO_ERROR = new ErrorCode("500003", "( >﹏<。)～ Sistem sedang sibuk sekarang, mohon coba kembali (500003)");
    }

    public interface AuthenticationError {
        ErrorCode UNAUTHORIZED = new ErrorCode("400001", "[400001] Sumber daya tidak resmi");
        ErrorCode UNKNOWN_CREDENTIAL = new ErrorCode("400002", "[400002] Sumber daya tidak resmi");
        ErrorCode TOKEN_EXPIRED = new ErrorCode("400003", "[400003] Sumber daya tidak resmi");
        ErrorCode INVALID_TOKEN = new ErrorCode("400004", "[400004] Sumber daya tidak resmi");
        ErrorCode INVALID_CREDENTIAL = new ErrorCode("400005", "[400005] Sumber daya tidak resmi");
        ErrorCode TOKEN_NOT_FOUND = new ErrorCode("400006", "[400006] Sumber daya tidak resmi");
    }

    public interface ResourceError {
        ErrorCode CAN_NOT_ACCESS = new ErrorCode("410001", "资源不可访问");
        ErrorCode RESOURCE_EXPORTING_FAILED = new ErrorCode("410002", "导出失败，请重新再试");
        ErrorCode RESOURCE_EXPORTING = new ErrorCode("410003", "导出中，请重新再试");
        ErrorCode INVALID_RESOURCE = new ErrorCode("410004", "资源类型错误，请联系管理员");
        ErrorCode RESOURCE_NOT_FOUND = new ErrorCode("410005", "资源不存在");
    }

    public interface ApiError {
        ErrorCode PARAMETER_VALIDATION_ERROR = new ErrorCode("600001", "[600001] Validasi parameter gagal");
        ErrorCode RPC_CALL_VALIDATION_ERROR = new ErrorCode("600002", "[600002] Validasi parameter gagal");
        ErrorCode NO_RESPONSE_ERROR = new ErrorCode("600003", "调用RPC接口无响应");
        ErrorCode NO_RESPONSE_STATUS_ERROR = new ErrorCode("600004", "调用RPC接口无响应状态码");
        ErrorCode NO_RESPONSE_ERROR_ERROR = new ErrorCode("600005", "调用RPC接口无响应错误码");
        ErrorCode INVALID_SIGNATURE = new ErrorCode("600006", "签名验证失败");
        ErrorCode REQUEST_TOO_BUSY = new ErrorCode("600007", "请求太频繁");
        ErrorCode DENIED_BY_RULE = new ErrorCode("600008", "请求被拒");
        ErrorCode UNSUPPORTED_CHARACTERS = new ErrorCode("600009", "检测到非法字符: %s, 请删除");
    }

    public interface RpcError {
        ErrorCode ORD_USER_NOT_EXIST = new ErrorCode("ORD_001", "用户不存在");
        ErrorCode ORD_USER_HAD_VA = new ErrorCode("ORD_002", "当前待还款，暂无法支付");
        ErrorCode ORD_PROCESSING_ORDER = new ErrorCode("ORD_003", "有处理中的订单");
        ErrorCode ORD_NONEED_REPAY = new ErrorCode("ORD_004", "无需还款");
        ErrorCode ORD_CREDIT_HAVE_EXIST = new ErrorCode("ORD_1000", "额度已申请");
        ErrorCode ORD_CREDIT_REJECTTED = new ErrorCode("ORD_1001", "额度申请未通过");
        ErrorCode ORD_CREDIT_NOT_VALID = new ErrorCode("ORD_1002", "额度无效或未激活");
        ErrorCode ORD_CREDIT_NOT_ENOUGH = new ErrorCode("ORD_1003", "额度不足");
        ErrorCode ORD_CREDIT_REMAINED_NOT_ENOUGH = new ErrorCode("ORD_1004", "剩余额度不足50K");
        ErrorCode ORD_MERCHNAT_ISNOT_SUPPORTED = new ErrorCode("ORD_2000", "不支持此商户");
        ErrorCode ORD_ORDER_HAVE_OVERDULED_BILL = new ErrorCode("ORD_3000", "有逾期账单");
        ErrorCode ORD_ORDER_LASTORDER_NOT_UPLOADED_ATTACH = new ErrorCode("ORD_3001", "最近一笔订单尚未上传消费凭证");
        ErrorCode ORD_ORDER_AMOUNT_LIMITED = new ErrorCode("ORD_3002", "商品金额与对应的首付比例不合法");
        ErrorCode ORD_ORDER_CAPITAL_LIMITED = new ErrorCode("ORD_3003", "额度金额限制50k-500k");
        ErrorCode ORD_ORDER_ATTACHMENT_CANT_UPLOAD = new ErrorCode("ORD_3004", "交易凭证已上传无需重复上传");
        ErrorCode ORD_ORDER_NUM_LIMIT = new ErrorCode("ORD_3005", "每天不能超过三笔订单");
        ErrorCode ORD_ORDER_ONLY1MONTH_LIMIT = new ErrorCode("ORD_3006", "不能选择1个月，请重新选择提交");
    }
}
