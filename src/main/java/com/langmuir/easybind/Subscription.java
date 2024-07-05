package com.langmuir.easybind;

@FunctionalInterface
public interface Subscription {
    static final Subscription EMPTY = () -> {
    };

    /**
     * Returns a new aggregate subscription whose {@link #unsubscribe()}
     * method calls {@code unsubscribe()} on all arguments to this method.
     */
    static Subscription multi(Subscription... subs) {
        return switch (subs.length) {
            case 0 -> EMPTY;
            case 1 -> subs[0];
            case 2 -> new BiSubscription(subs[0], subs[1]);
            default -> new MultiSubscription(subs);
        };
    }

    void unsubscribe();

    /**
     * Returns a new aggregate subscription whose {@link #unsubscribe()}
     * method calls {@code unsubscribe()} on both this subscription and
     * {@code other} subscription.
     */
    default Subscription and(Subscription other) {
        return new BiSubscription(this, other);
    }
}

class BiSubscription implements Subscription {
    private final Subscription s1;
    private final Subscription s2;

    public BiSubscription(Subscription s1, Subscription s2) {
        this.s1 = s1;
        this.s2 = s2;
    }

    @Override
    public void unsubscribe() {
        s1.unsubscribe();
        s2.unsubscribe();
    }
}

class MultiSubscription implements Subscription {
    private final Subscription[] subscriptions;

    public MultiSubscription(Subscription... subscriptions) {
        this.subscriptions = subscriptions;
    }

    @Override
    public void unsubscribe() {
        for (Subscription s : subscriptions) {
            s.unsubscribe();
        }
    }
}
