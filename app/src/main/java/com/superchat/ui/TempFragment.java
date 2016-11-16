package com.superchat.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.superchat.R;

//import android.app.ListFragment;

public class TempFragment extends Fragment {

	public View onCreateView(LayoutInflater layoutinflater,
							 ViewGroup viewgroup, Bundle bundle) {
		View view = layoutinflater.inflate(R.layout.contact_home, null);

		return view;
	}
}
