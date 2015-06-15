package com.meishi.support;

/**
 * Created by Aaron on 2015/6/15.
 */
public interface Constants {

    String CITY = "上海";

    String BASE_URL = "http://192.168.0.119:8080";
    //    String BASE_URL = "http://114.90.9.40:8080";
    String CUSTOMERS_URL = BASE_URL + "/entity/customers";
    String FIND_CUSTOMER_URL = CUSTOMERS_URL + "/search/findByIdentity?clientId=";
    String ORDERS_URL = BASE_URL + "/entity/orders";
    String FIND_ORDER_URL = ORDERS_URL + "/search/findByClientId?clientId=";
    String ADMIN_TEST_USER = "123456";
    String ADMIN_TEST_PASSWORD = "111";

}
