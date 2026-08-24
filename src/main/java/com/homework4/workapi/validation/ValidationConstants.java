package com.homework4.workapi.validation;

public final class ValidationConstants {

    public static final int USERNAME_MAX_LENGTH = 10;
    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final int PASSWORD_MAX_LENGTH = 20;
    public static final String PASSWORD_PATTERN =
            "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$";
    public static final int MIN_PAGE = 1;
    public static final String PAGE_MIN_MESSAGE = "페이지는 1 이상이어야 합니다.";
    public static final int SEARCH_KEYWORD_MAX_LENGTH = 99;
    public static final String KEYWORD_MAX_MESSAGE = "검색어는 100자 미만 이어야 합니다.";
    public static final int MAX_SEARCH_PAGE = 1000;
    public static final String PAGE_MAX_MESSAGE =
            "페이지는 " + MAX_SEARCH_PAGE + " 이하이어야 합니다.";

    private ValidationConstants() {
    }
}
