package com.skv.telegram.api;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BotRequestMapping {

    String[] value() default {};

    BotRequestMethod[] messageType() default {BotRequestMethod.COMMAND};
}
