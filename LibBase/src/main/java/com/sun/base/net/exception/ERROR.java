package com.sun.base.net.exception;

/**
 * @author: Harper
 * @date: 2021/11/12
 * @note: 与服务器约定好的异常
 */
public interface ERROR {
    /**
     * 未知错误
     */
    int UNKNOWN = 1000;
    /**
     * 解析错误
     */
    int PARSE_ERROR = 1001;
    /**
     * 网络错误
     */
    int NETWORD_ERROR = 1002;
    /**
     * 协议出错
     */
    int HTTP_ERROR = 1003;
    /**
     * token不正确，需跳到登录页
     */
    int WRONG_TOKEN = 401;

    /**
     * 作业打回
     */
    int WRONG_WORK_BACK = -2038;

    /**
     * 作业删除
     */
    int WRONG_WORK_DELETE = -2001;

    /**
     * 作业撤回已截止（学生端）
     */
    int REDO_COLORFUL_WORK_YI_JIE_ZHI = -2018;

    /**
     * 作业已被学生撤回
     */
    int REDO_COLORFUL_WORK_CODE = -2045;

    /**
     * 学生作业不存在
     */
    int COLORFUL_WORK_DEL_BY_STU_CODE = -2002;

    /**
     * 老师已评价，不能撤回了
     */
    int COLORFUL_TEACHER_HAVE_COMMENT = -2019;

    /**
     * 作业未提交
     */
    int COLORFUL_TEACHER_NO_COMMIT = -2034;

    /**
     * 作业未提交
     */
    int SPEECH_TEACHER_NO_COMMIT = -2035;

    /**
     * 失败
     */
    int ERROR_FAILURE = 500;
}
