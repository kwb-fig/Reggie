package com.kong.Exception;

import com.kong.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(annotations = RestController.class)
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public R<String> exception(Exception e){
        log.error(e.getMessage());
        return R.error("添加错误");
    }

    @ExceptionHandler(CustomerException.class)
    public R<String> customerexception(Exception e){
        log.error(e.getMessage());
        return R.error(e.getMessage());
    }
    @ExceptionHandler(FileException.class)
    public R<String> FileException(Exception e){
        log.error(e.getMessage());
        return R.error(e.getMessage());
    }

    @ExceptionHandler(deleteException.class)
    public R<String> deleteException(Exception e){
        log.error(e.getMessage());
        return R.error(e.getMessage());
    }
    @ExceptionHandler(subException.class)
    public R<String> subException(Exception e){
        log.error(e.getMessage());
        return R.error(e.getMessage());
    }
    @ExceptionHandler(submitException.class)
    public R<String> submitException(Exception e){
        log.error(e.getMessage());
        return R.error(e.getMessage());
    }
    @ExceptionHandler(addressExcetption.class)
    public R<String> addressExcetption(Exception e){
        log.error(e.getMessage());
        return R.error(e.getMessage());
    }

}
