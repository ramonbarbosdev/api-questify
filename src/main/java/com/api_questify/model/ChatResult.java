package com.api_questify.model;

public record ChatResult(

        String content,

        Integer promptTokens,

        Integer completionTokens,

        Integer totalTokens

) {}