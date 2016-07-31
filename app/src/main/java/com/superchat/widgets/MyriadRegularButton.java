



package com.superchat.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import com.superchat.utils.Methods;

public class MyriadRegularButton extends Button
{

    public MyriadRegularButton(Context context)
    {
        super(context);
    }

    public MyriadRegularButton(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public MyriadRegularButton(Context context, AttributeSet attributeset, int i)
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
