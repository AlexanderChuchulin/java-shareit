package ru.practicum.shareit.other;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

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
        notEmptyNames.remove("itemOwner");
        String[] result = new String[notEmptyNames.size()];
        return notEmptyNames.toArray(result);
    }
}
