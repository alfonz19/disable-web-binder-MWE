package com.example.is_web_binder_used_to_bind_jackson_dto_request_body;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.is_web_binder_used_to_bind_jackson_dto_request_body.IsWebBinderUsedToBindJacksonDtoRequestBodyApplication.ROLE2;

@Slf4j
@RestController()
@Validated
@RequestMapping(SomeController.CONTROLLER_PATH)
@AllArgsConstructor
public class SomeController {

    public static final String CONTROLLER_PATH = "/someController";
    public static final String SEARCH_METHOD = "/search";

    @RolesAllowed(ROLE2)
    @PostMapping(SEARCH_METHOD)
    public String search(@Valid @RequestBody SearchRequest searchRequest) {
        return "OK";
    }


//       vvvvv solution per each controller if needed. vvvvv
//    @InitBinder
//    public void initUserBinder(WebDataBinder binder) {
//        // customization specific to binding a User
//        binder.setExcludedValidators(e->true);
//    }
//       ^^^^^ solution per each controller if needed. ^^^^^

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class SearchRequest {

        @NotNull
        @PositiveOrZero
        private Long userId;

    }
}