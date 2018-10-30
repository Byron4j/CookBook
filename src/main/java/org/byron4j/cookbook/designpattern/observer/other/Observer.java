package org.byron4j.cookbook.designpattern.observer.other;

abstract class Observer {
    protected Subject subject;
    public abstract void update();
}
