package com.langmuir.easybind.select;

import java.util.function.Function;
import javafx.beans.value.ObservableValue;

import com.langmuir.easybind.EasyBinding;

interface ParentSelectedBuilder<T> extends SelectBuilder<T> {
    @Override
    default <U> SelectBuilder<U> select(Function<? super T, ObservableValue<U>> selector) {
        return new IntermediateSelectedBuilder<T, U>(this, selector);
    }

    @Override
    default <U> EasyBinding<U> selectObject(Function<? super T, ObservableValue<U>> selector) {
        NestedSelectionElementFactory<T, U> leafSelectionFactory = onInvalidation -> {
            return new LeafSelectionElement<T, U>(onInvalidation, selector);
        };
        return create(leafSelectionFactory);
    }

    <U> EasyBinding<U> create(NestedSelectionElementFactory<T, U> nestedSelectionFactory);
}
