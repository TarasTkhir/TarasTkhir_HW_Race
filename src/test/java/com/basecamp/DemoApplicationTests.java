package com.basecamp;

import com.basecamp.exception.InvalidDataException;
import com.basecamp.service.ProductService;
import com.basecamp.service.impl.ConcurrentTaskService;
import com.basecamp.service.impl.ProductServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ProductServiceImpl.class)
public class DemoApplicationTests {

    @Autowired
    private ProductService productService;

    @MockBean
    private ConcurrentTaskService taskService;

    public static final String INVALID_PRODUCT_ID = "invalid_id";

    @Test //negative case
    public void givenInvalidProductId_whenGetProductInfo_thenReturnException() {
        // GIVEN

        // WHEN
        Throwable thrown = catchThrowable(() ->
                productService.getProductInfo(INVALID_PRODUCT_ID));

        // THEN

        assertThat(thrown)
                .isNotNull()
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("Validation failed. ProductId invalid_id is not a number.");

    }

    @Test //negative case
    public void givenEmptyProductId_whenGetProductInfo_thenReturnException() {
        // GIVEN

        // WHEN
        Throwable thrown = catchThrowable(() ->
                productService.getProductInfo(""));

        // THEN

        assertThat(thrown)
                .isNotNull()
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("ProductId is not set.");

    }

}
