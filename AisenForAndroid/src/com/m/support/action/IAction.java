package com.m.support.action;

import android.app.Activity;

/**
 * Created by wangdan on 15-3-11.
 */
public abstract class IAction {

    private Activity context;

    private IAction parent;

    private IAction child;

    public IAction(Activity context, IAction parent) {
        this.context = context;
        this.parent = parent;
        if (parent != null)
            parent.setChild(this);
    }

    protected boolean interrupt() {
        return false;
    }

    public void run() {
        if (parent == null || !parent.interrupt()) {
            doAction();
        }
    }

    final protected void setChild(IAction child) {
        this.child = child;
    }

    final protected IAction getChild() {
        return child;
    }

    final protected Activity getContext() {
        return context;
    }

    abstract public void doAction();

}
