package com.example.is_web_binder_used_to_bind_jackson_dto_request_body;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

@ControllerAdvice
@Slf4j
@AllArgsConstructor
public final class BinderShouldNotValidateAdvice {

    //global disabling data binder premature validation
    @InitBinder
    public void initUserBinder(WebDataBinder binder) {
        // customization specific to binding a User
        binder.setExcludedValidators(e->true);
    }

}
