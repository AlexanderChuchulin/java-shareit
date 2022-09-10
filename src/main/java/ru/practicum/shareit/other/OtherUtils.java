package ru.practicum.shareit.other;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.util.HashSet;
import java.util.Set;

public class OtherUtils {

    // Метод возвращает массив имён не нулевых полей объекта
    public static String[] getNotNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Set<String> notEmptyNames = new HashSet<>();

        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue != null) notEmptyNames.add(pd.getName());
        }
        notEmptyNames.add("userIdHeader");
        notEmptyNames.remove("itemOwner");
        String[] result = new String[notEmptyNames.size()];
        return notEmptyNames.toArray(result);
    }
}