package com.m.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.m.support.Inject.InjectUtility;

public abstract class AFragmentHelper {

    private ABaseFragment baseFragment;
    private Activity baseActivity;

    public void setBaseFragment(ABaseFragment baseFragment) {
        this.baseFragment = baseFragment;
    }

    protected ABaseFragment getBaseFragment() {
        return baseFragment;
    }

    public Activity getActivity() {
        return baseActivity;
    }

    public void onAttach(Activity activity) {
        baseActivity = activity;
    }

    public void onCreate(Bundle savedInstanceState) {

    }

    public void layoutInit(View rootView, LayoutInflater inflater, Bundle savedInstanceSate) {
        InjectUtility.initInjectedView(this, rootView);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup rootView, Bundle savedInstanceState) {
        return rootView;
    }

    public void onActivityCreated(Bundle savedInstanceState) {

    }

    public void onStart() {

    }

    public void onResume() {

    }

    public void onPause() {

    }

    public void onStop() {

    }

    public void onDestroyView() {

    }

    public void onDestroy() {

    }

    public void onDetach() {

    }

    public boolean onHomeClick() {
        return false;
    }

    public boolean onBackClick() {
        return false;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

}
