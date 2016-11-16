package com.superchat.ui;

import android.support.v4.app.ListFragment;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * Created by Motobeans on 10/17/2016.
 */

public class CustomFragmentHomeTabs extends ListFragment {

    public Toolbar toolbar_child_fragment_tab;

    public void selectFrag() {
        try {

            showToolbar();
            /*
            if (searchBoxView != null) {
                searchBoxView.setText("");
            }
*/
            ((HomeScreen) getActivity()).hideKeyboard(getActivity());
        } catch (Exception e) {

        }
    }

    public void deselectFrag() {
        try {
            hideToolbar();
        } catch (Exception e) {

        }
    }

    public void showToolbar(){
        if (toolbar_child_fragment_tab != null) {
            toolbar_child_fragment_tab.setVisibility(View.VISIBLE);
        }
    }
    public void hideToolbar(){
        if (toolbar_child_fragment_tab != null) {
            toolbar_child_fragment_tab.setVisibility(View.GONE);
        }
    }
}
