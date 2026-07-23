package com.example.bms.web;

import com.example.bms.common.exception.BusinessException;
import com.example.bms.common.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import jakarta.validation.ConstraintViolationException;

/** 将业务、参数与系统异常转换为统一日语错误画面，同时避免把堆栈或 Secret 暴露给浏览器。 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String notFound(ResourceNotFoundException ex, Model model) {
        model.addAttribute("status", 404);
        model.addAttribute("title", "ページまたはデータが見つかりません");
        model.addAttribute("message", ex.getMessage());
        return "error/error";
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String business(BusinessException ex, Model model) {
        model.addAttribute("status", 400);
        model.addAttribute("title", "操作を完了できません");
        model.addAttribute("message", ex.getMessage());
        return "error/error";
    }

    /** 入力不備はシステム障害ではないため、監視アラートを発生させず HTTP 400 として返す。 */
    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String validation(Exception ex, Model model) {
        model.addAttribute("status", 400);
        model.addAttribute("title", "入力内容を確認してください");
        model.addAttribute("message", "必須項目または値の範囲が正しくありません。");
        return "error/error";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String unexpected(Exception ex, HttpServletRequest request, Model model) {
        log.error("未処理例外 path={} type={}", request.getRequestURI(), ex.getClass().getName(), ex);
        model.addAttribute("status", 500);
        model.addAttribute("title", "システムエラーが発生しました");
        model.addAttribute("message", "Request ID を添えて運用担当者に連絡してください。");
        return "error/error";
    }
}
