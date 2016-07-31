



package com.superchat.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

import com.superchat.utils.Methods;

public class MyriadSemiboldEditText extends EditText
{

    public MyriadSemiboldEditText(Context context)
    {
        super(context);
    }

    public MyriadSemiboldEditText(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public MyriadSemiboldEditText(Context context, AttributeSet attributeset, int i)
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
