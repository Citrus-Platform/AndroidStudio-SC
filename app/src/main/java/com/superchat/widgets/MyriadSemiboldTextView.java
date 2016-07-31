



package com.superchat.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.superchat.utils.Methods;

public class MyriadSemiboldTextView extends TextView
{

    public MyriadSemiboldTextView(Context context)
    {
        super(context);
    }

    public MyriadSemiboldTextView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public MyriadSemiboldTextView(Context context, AttributeSet attributeset, int i)
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
            return Methods.Get_tf_Myriad_semi_bold(getContext());
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
            super.setTypeface(Methods.Get_tf_Myriad_semi_bold(getContext()));
            return;
        }
    }
}
