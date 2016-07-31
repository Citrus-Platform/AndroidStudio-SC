



package com.superchat.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.superchat.utils.Methods;

public class MyriadRegularTextView extends TextView
{

    public MyriadRegularTextView(Context context)
    {
        super(context);
    }

    public MyriadRegularTextView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public MyriadRegularTextView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
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
