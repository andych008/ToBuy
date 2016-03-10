package com.dwgg.tobuy.util;

import rx.Observable;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

public class RxBus {

    private static RxBus defaultInstance;
    private final Subject bus;
    private final Subject stickyBus;

    public static RxBus getDefault() {
        if (defaultInstance == null) {
            synchronized (RxBus.class) {
                if (defaultInstance == null) {
                    defaultInstance = new RxBus();
                }
            }
        }
        return defaultInstance;
    }

    private RxBus() {
        bus = new SerializedSubject<>(PublishSubject.create());
        stickyBus = new SerializedSubject<>(BehaviorSubject.create());
    }

    public <T extends Object> Observable<T> register(final Class<T> eventType) {
        return bus.asObservable().onBackpressureBuffer().filter(new Func1<Object, Boolean>() {
            @Override
            public Boolean call(Object o) {
                return eventType.isInstance(o);
            }
        })
                .cast(eventType);
    }

    public <T extends Object> Observable<T> registerSticky(final Class<T> eventType) {
        return stickyBus.asObservable().share().onBackpressureBuffer().filter(new Func1<Object, Boolean>() {
            @Override
            public Boolean call(Object o) {
                return eventType.isInstance(o);
            }
        }).cast(eventType)
                ;
    }

    public void post(Object o) {
        bus.onNext(o);
    }

    public void postSticky(Object o) {
        stickyBus.onNext(o);
    }
}
