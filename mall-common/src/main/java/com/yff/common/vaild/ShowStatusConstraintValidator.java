package com.yff.common.vaild;

import com.yff.common.annotation.ShowStatus;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.*;

/**
 * @author yanfeifan
 * @Package com.yff.common.vaild
 * @Description
 * @date 2021/12/17 17:28
 */

public class ShowStatusConstraintValidator implements ConstraintValidator<ShowStatus,Integer> {

    private Set<Integer> valueSet = new HashSet<>();

    @Override
    public void initialize(ShowStatus constraintAnnotation) {
        int[] value = constraintAnnotation.value();
        for (int i : value) {
            valueSet.add(i);
        }
    }

    /**
     *
     * @param value 需要校验的值
     * @param context
     * @return
     */
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return valueSet.contains(value);
    }
}
