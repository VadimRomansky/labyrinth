package ru.romansky.labyrinthTest;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.function.Predicate;

public class ElementWiseUtil {
    public static<T> void selectElements(List<T> list, Predicate<T> predicate, List<T> newList){
        for (T element :
                list) {
            if(predicate.test(element)){
                newList.add(element);
            }
        }
    }
    public static<T> LinkedList<T> selectElements(LinkedList<T> list, Predicate<T> predicate){
        LinkedList<T> result = new LinkedList<>();
        selectElements(list, predicate, result);
        return result;
    }

    public static<T> ArrayList<T> selectElements(ArrayList<T> list, Predicate<T> predicate){
        ArrayList<T> result = new ArrayList<>();
        selectElements(list, predicate, result);
        return result;
    }

    public static<T> Vector<T> selectElements(Vector<T> list, Predicate<T> predicate){
        Vector<T> result = new Vector<>();
        selectElements(list, predicate, result);
        return result;
    }
}
