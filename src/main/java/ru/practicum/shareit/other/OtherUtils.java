package ru.practicum.shareit.other;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.ValidationExc;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

public class OtherUtils {
    /**
     * Метод возвращает массив имён основных не нулевых полей объекта
     */
    public static String[] getNotNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Set<String> notEmptyNames = new HashSet<>();

        for (PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue != null) notEmptyNames.add(pd.getName());
        }
        notEmptyNames.add("userIdHeader");
        String[] result = new String[notEmptyNames.size()];

        return notEmptyNames.toArray(result);
    }

    /**
     * Метод возвращает PageRequest, собранный на основе двух первых дополнительных данных переданных в контролёр
     */
    public static Pageable pageableCreateFrommAdditionalParams(String[] additionalParams, String entityName, String... sortParam) {

        if (Integer.parseInt(additionalParams[0]) < 0 || additionalParams[1] != null && Integer.parseInt(additionalParams[1]) <= 0) {
            throw new ValidationExc("Неправильные параметры разбивки на страницы. " + entityName + " не возвращены.");
        }

        long startElement = Integer.parseInt(additionalParams[0]);
        long size;
        long page;

        if (additionalParams[1] == null) {
            size = Integer.MAX_VALUE;
            page = 0;
        } else {
            size = Long.parseLong(additionalParams[1]);
            page = (long) (Math.ceil(((double) startElement + 1) / size) - 1);
        }

        if (sortParam.length == 1 && !sortParam[0].isBlank()) {
            return PageRequest.of((int) page, (int) size, Sort.by(sortParam[0]).descending());
        }

        return PageRequest.of((int) page, (int) size);
    }
}
