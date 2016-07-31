



package com.superchat.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

import com.superchat.interfaces.EditFieldPreImeEvent;
import com.superchat.utils.Methods;

public class MyriadRegularEditText extends EditText
{

	Context context;
	EditFieldPreImeEvent listner;

	public MyriadRegularEditText(Context context1)
	{
		super(context1);
		listner = null;
		context = context1;
	}

	public MyriadRegularEditText(Context context1, AttributeSet attributeset)
	{
		super(context1, attributeset);
		listner = null;
		context = context1;
	}

	public MyriadRegularEditText(Context context1, AttributeSet attributeset, int i)
	{
		super(context1, attributeset, i);
		listner = null;
		context = context1;
	}

	public Typeface getTypeface()
	{
		if (isInEditMode())
		{
			return super.getTypeface();
		} else
		{
			return Methods.Get_tf_MyriadProRegular(getContext());
		}
	}

	public void setListner(EditFieldPreImeEvent editfieldpreimeevent)
	{
		listner = editfieldpreimeevent;
	}

	@Override
	public boolean onKeyPreIme(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(listner != null)
			listner.onEditTextBackPressed();
		}
		return super.onKeyPreIme(keyCode, event);
	}
	public void setTypeface(Typeface typeface)
	{
		if (isInEditMode())
		{
			super.setTypeface(typeface);
			return;
		} else
		{
			super.setTypeface(Methods.Get_tf_MyriadProRegular(getContext()));
			return;
		}
	}
}
