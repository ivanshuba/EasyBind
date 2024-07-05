package com.langmuir.easybind;

import java.util.function.Function;
import java.util.stream.Stream;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;

class ListCombinationBinding<T, U> extends ObjectBinding<U> implements EasyBinding<U> {

    private final InvalidationListener elemListener = obs -> elementInvalidated();
    private final WeakInvalidationListener weakElemListener = new WeakInvalidationListener(elemListener);
    private final ListChangeListener<ObservableValue<? extends T>> listListener = this::sourceChanged;
    private final WeakListChangeListener<ObservableValue<? extends T>> weakListListener = new WeakListChangeListener<>(listListener);
    private final ObservableList<? extends ObservableValue<? extends T>> source;
    private final Function<? super Stream<T>, ? extends U> combiner;

    public ListCombinationBinding(ObservableList<? extends ObservableValue<? extends T>> list, Function<? super Stream<T>, ? extends U> f) {
        source = list;
        combiner = f;

        source.addListener(weakListListener);
        source.forEach(elem -> elem.addListener(weakElemListener));
    }

    @Override
    protected U computeValue() {
        return combiner.apply(source.stream().map(ObservableValue::getValue));
    }

    @Override
    public void dispose() {
        source.forEach(elem -> elem.removeListener(weakElemListener));
        source.removeListener(weakListListener);
    }

    private void sourceChanged(Change<? extends ObservableValue<? extends T>> ch) {
        while (ch.next()) {
            ch.getRemoved().forEach(elem -> elem.removeListener(weakElemListener));
            ch.getAddedSubList().forEach(elem -> elem.addListener(weakElemListener));
            invalidate();
        }
    }

    private void elementInvalidated() {
        invalidate();
    }
}
