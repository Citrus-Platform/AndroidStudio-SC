package com.superchat.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.superchat.R;
import com.superchat.utils.FileDownloadResponseHandler;
import com.superchat.utils.ThumbnailImage;
import com.superchat.utils.ThumbnailReponseHandler;

import java.util.ArrayList;
import java.util.List;


public class QuickAction extends PopupWindows implements OnDismissListener, ThumbnailReponseHandler, FileDownloadResponseHandler {
	private View mRootView;
	private ImageView mArrowUp;
	private ImageView mArrowDown;
	private LayoutInflater mInflater;
	private ViewGroup mTrack;
	private ScrollView mScroller;
	private OnActionItemClickListener mItemClickListener;
	private OnDismissListener mDismissListener;
	public boolean outsidescreen = true ;
	private List<ActionItem> actionItems = new ArrayList<ActionItem>();
	
	private boolean mDidAction;
	
	private int mChildPos;
    private int mInsertPos;
    private int mAnimStyle;
    private int mOrientation;
    private int rootWidth=0;
    private int maxCount=0;
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    
    public static final int ANIM_GROW_FROM_LEFT = 1;
	public static final int ANIM_GROW_FROM_RIGHT = 2;
	public static final int ANIM_GROW_FROM_CENTER = 3;
	public static final int ANIM_REFLECT = 4;
	public static final int ANIM_AUTO = 5;
	Context context;
	
    public QuickAction(Context context,boolean isArrow,boolean outsidescreen, int maxCount) {
        this(context, VERTICAL,isArrow,outsidescreen,maxCount);
    }

    public QuickAction(Context context, int orientation,boolean isArrow,boolean outsidescreen, int maxCount) {
        super(context);
        this.context = context;
        this.isArrow = isArrow;
        mOrientation = orientation;
        this.outsidescreen = outsidescreen;
        this.maxCount=maxCount;
        
        mInflater 	 = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (mOrientation == HORIZONTAL) {
            setRootViewId(R.layout.popup_horizontal);
        } else {
        	if(isArrow)
            setRootViewId(R.layout.popup_vertical2);
        	else
            setRootViewId(R.layout.popup_vertical);
        }

        mAnimStyle 	= ANIM_AUTO;
        mChildPos 	= 0;
    }

    public ActionItem getActionItem(int index) {
        return actionItems.get(index);
    }
    	public void setRootViewId(int id) {
		mRootView	= (ViewGroup) mInflater.inflate(id, null);
		mTrack 		= (ViewGroup) mRootView.findViewById(R.id.tracks);

		mArrowDown 	= (ImageView) mRootView.findViewById(R.id.arrow_down);
		mArrowUp 	= (ImageView) mRootView.findViewById(R.id.arrow_up);

		mScroller	= (ScrollView) mRootView.findViewById(R.id.scroller);
		
		mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		setContentView(mRootView);
	}
	public void setAnimStyle(int mAnimStyle) {
		this.mAnimStyle = mAnimStyle;
	}
	public void setOnActionItemClickListener(OnActionItemClickListener listener) {
		mItemClickListener = listener;
	}
	int valueCounter=0;
	public void addActionItem(ActionItem action) {
		actionItems.add(action);
		valueCounter++;
		String title 	= action.getTitle();
		Drawable icon 	= action.getIcon();
		int value=action.getValue();
		View container;
		
		if (mOrientation == HORIZONTAL) {
            container = mInflater.inflate(R.layout.action_item_horizontal, null);
        } else {
            container = mInflater.inflate(R.layout.action_item_vertical, null);
        }
		ImageView img= (ImageView) container.findViewById(R.id.iv_icon);
		//LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(
       		// LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		if(icon!=null){
			
			((RelativeLayout)container.findViewById(R.id.iv_icon_layout)).setVisibility(View.VISIBLE);
			//if(icon instanceof  )
			if(value!=-1){
				if(value==0){
					((RelativeLayout)container.findViewById(R.id.iv_icon_layout)).setVisibility(View.GONE);
				}else{
					if(value>50)
						((TextView)container.findViewById(R.id.iv_icon_text)).setText(""+50+"+");
					else
			((TextView)container.findViewById(R.id.iv_icon_text)).setText(""+value);
				}
			}else{
				((TextView)container.findViewById(R.id.iv_icon_text)).setVisibility(View.GONE);
			}
			//LinearLayout layout=new LinearLayout(mContext);
			//TextView divider =new TextView(mContext);
			//if(actionItems!=null && (actionItems.size()))
			//if(valueCounter!=actionItems.size())
			//((LinearLayout)container.findViewById(R.id.dividerlanding)).setVisibility(View.GONE);
			//divider.setBackgroundResource(R.drawable.hor_line);
			///View view =new View(mContext);
			//TextView divider =(TextView)view;
			//view.setBackgroundResource(R.drawable.hor_line);
			 
		// img.setVisibility(View.VISIBLE);
		}else{
			//if(actionItems!=null && (value==actionItems.size()-2) &&((LinearLayout)container.findViewById(R.id.dividerlanding))!=null)
				//((LinearLayout)container.findViewById(R.id.dividerlanding)).setVisibility(View.GONE);
			((RelativeLayout)container.findViewById(R.id.iv_icon_layout)).setVisibility(View.GONE);
		}
		TextView text 	= (TextView) container.findViewById(R.id.tv_title);
		//System.out.println("actionItmeSize"+actionItems.size()+"mChildPos=="+mChildPos);
		//if(actionItems.)
		LinearLayout layout=((LinearLayout)container.findViewById(R.id.tv_title_layout));
		if(actionItems.size()==maxCount){
			
			
				//parms.setMargins(0, 0, 0, 0);
			
				//b.setLayoutParams(parms);
				//layout.setLayoutParams(parms);
			((LinearLayout)container.findViewById(R.id.dividerlanding)).setVisibility(View.GONE);
			
		}else{
			layout=((LinearLayout)container.findViewById(R.id.tv_title_layout));
			//parms.setMargins(0, 0, 0,0);
			//b.setLayoutParams(parms);
			//layout.setLayoutParams(parms);
		}
		
		if (icon != null) {
			img.setImageDrawable(icon);
		} else {
			img.setVisibility(View.GONE);
		}
		
		if (title != null) {
			text.setText(title);
		} else {
			text.setVisibility(View.GONE);
		}
		
		final int pos 		=  mChildPos;
		final int actionId 	= action.getActionId();
		//container.setBackgroundResource(R.drawable.custom_button);
		container.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(QuickAction.this, pos, actionId);
                }
				
                if (!getActionItem(pos).isSticky()) {  
                	mDidAction = true;                	
                    dismiss();
                }
			}
		});
		
		container.setFocusable(true);
		container.setClickable(true);
			 
		if (mOrientation == HORIZONTAL && mChildPos != 0) {
            View separator = mInflater.inflate(R.layout.horiz_separator, null);
            
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
            
            separator.setLayoutParams(params);
            separator.setPadding(5, 5, 5, 0);
            
            mTrack.addView(separator, mInsertPos);
            
            mInsertPos++;
        }
		
		mTrack.addView(container, mInsertPos);
		
		mChildPos++;
		mInsertPos++;
	}
	
	
	public void hideThumb(){
		 mRootView.findViewById(R.id.thumb).setVisibility(View.GONE);
	}
	public void showThumb(){
		 mRootView.findViewById(R.id.thumb).setVisibility(View.VISIBLE);
	}
	public void addThumb(Drawable icon){
		ImageView img 	= (ImageView) mRootView.findViewById(R.id.thumb);
		 img.setImageDrawable(icon);
	}
	public void addThumb(Bitmap icon){
		ImageView img 	= (ImageView) mRootView.findViewById(R.id.thumb);
//		 img.setb;
		if(icon!=null)
		img.setImageBitmap(icon);
		else
			img.setImageBitmap(icon);
	}
	public void show2(View anchor) {
		try{
		preShow(this.outsidescreen);
		
		int xPos, yPos, arrowPos;
		
		mDidAction 			= false;
		
		int[] location 		= new int[2];
	if(anchor!=null){
		anchor.getLocationOnScreen(location);

		Rect anchorRect 	= new Rect(location[0], location[1], location[0] + anchor.getWidth(), location[1] 
		                	+ anchor.getHeight());
		
		//mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
//		mRootView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	
		int rootHeight 		= mRootView.getMeasuredHeight();
		
		if (rootWidth == 0) {
			rootWidth		= mRootView.getMeasuredWidth();
		}
		
		int screenWidth 	= mWindowManager.getDefaultDisplay().getWidth();
		int screenHeight	= mWindowManager.getDefaultDisplay().getHeight();
		
		//automatically get X coord of popup (top left)
		if ((anchorRect.left + rootWidth) > screenWidth) {
			xPos 		= anchorRect.left - (rootWidth-anchor.getWidth());			
			xPos 		= (xPos < 0) ? 0 : xPos;
			
			arrowPos 	= anchorRect.centerX()-xPos;
			
		} else {
			if (anchor.getWidth() > rootWidth) {
				xPos = anchorRect.centerX() - (rootWidth/2);
			} else {
				xPos = anchorRect.left;
			}
			
			arrowPos = anchorRect.centerX()-xPos;
		}
		
		int dyTop			= anchorRect.top;
		int dyBottom		= screenHeight - anchorRect.bottom;

		boolean onTop		= (dyTop > dyBottom) ? true : false;

		if (onTop) {
			if (rootHeight > dyTop) {
				yPos 			= 15;
				LayoutParams l 	= mScroller.getLayoutParams();
				l.height		= dyTop - anchor.getHeight();
			} else {
				yPos = anchorRect.top - rootHeight;
			}
		} else {
			yPos = anchorRect.bottom;
			
			if (rootHeight > dyBottom) { 
				LayoutParams l 	= mScroller.getLayoutParams();
				l.height		= dyBottom;
			}
		}
		
//		showArrow(((onTop) ? R.id.arrow_down : R.id.arrow_up), arrowPos);
		
//		setAnimationStyle(screenWidth, anchorRect.centerX(), onTop);
		
//		mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);
		mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, getStatusBarHeight() > 0 ? getStatusBarHeight() : 50);
	
	}
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
	/**
	 * Show quickaction popup. Popup is automatically positioned, on top or bottom of anchor view.
	 * 
	 */
	public void show (View anchor) {
		preShow(outsidescreen);
		
		int xPos, yPos, arrowPos;
		
		mDidAction 			= false;
		
		int[] location 		= new int[2];
	
		anchor.getLocationOnScreen(location);

		Rect anchorRect 	= new Rect(location[0], location[1], location[0] + anchor.getWidth(), location[1] 
		                	+ anchor.getHeight());

		//mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		mRootView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	
		int rootHeight 		= mRootView.getMeasuredHeight();
		
		if (rootWidth == 0) {
			rootWidth		= mRootView.getMeasuredWidth();
		}
		
		int screenWidth 	= mWindowManager.getDefaultDisplay().getWidth();
		int screenHeight	= mWindowManager.getDefaultDisplay().getHeight();
		
		//automatically get X coord of popup (top left)
		if ((anchorRect.left + rootWidth) > screenWidth) {
			xPos 		= anchorRect.left - (rootWidth-anchor.getWidth());			
			xPos 		= (xPos < 0) ? 0 : xPos;
			
			arrowPos 	= anchorRect.centerX()-xPos;
			
		} else {
			if (anchor.getWidth() > rootWidth) {
				xPos = anchorRect.centerX() - (rootWidth/2);
			} else {
				xPos = anchorRect.left;
			}
			
			arrowPos = anchorRect.centerX()-xPos;
		}
		
		int dyTop			= anchorRect.top;
		int dyBottom		= screenHeight - anchorRect.bottom;

		boolean onTop		= (dyTop > dyBottom) ? true : false;

		if (onTop) {
			if (rootHeight > dyTop) {
				yPos 			= 15;
				LayoutParams l 	= mScroller.getLayoutParams();
				l.height		= dyTop - anchor.getHeight();
			} else {
				yPos = anchorRect.top - rootHeight;
			}
		} else {
			yPos = anchorRect.bottom;
			
			if (rootHeight > dyBottom) { 
				LayoutParams l 	= mScroller.getLayoutParams();
				l.height		= dyBottom;
			}
		}
		
		showArrow(((onTop) ? R.id.arrow_down : R.id.arrow_up), arrowPos);
		
		setAnimationStyle(screenWidth, anchorRect.centerX(), onTop);
		
		mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);
	}
	private void setAnimationStyle(int screenWidth, int requestedX, boolean onTop) {
		int arrowPos = requestedX - mArrowUp.getMeasuredWidth()/2;

		//Mahesh - plz check
//		switch (mAnimStyle) {
//		case ANIM_GROW_FROM_LEFT:
//			mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Left : R.style.Animations_PopDownMenu_Left);
//			break;
//
//		case ANIM_GROW_FROM_RIGHT:
//			mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Right : R.style.Animations_PopDownMenu_Right);
//			break;
//
//		case ANIM_GROW_FROM_CENTER:
//			mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center : R.style.Animations_PopDownMenu_Center);
//		break;
//
//		case ANIM_REFLECT:
//			//mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Reflect : R.style.Animations_PopDownMenu_Reflect);
//			mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center : R.style.Animations_PopDownMenu_Center);
//		break;
//
//		case ANIM_AUTO:
//			if (arrowPos <= screenWidth/4) {
//				mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Left : R.style.Animations_PopDownMenu_Left);
//			} else if (arrowPos > screenWidth/4 && arrowPos < 3 * (screenWidth/4)) {
//				mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center : R.style.Animations_PopDownMenu_Center);
//			} else {
//				mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Right : R.style.Animations_PopDownMenu_Right);
//			}
//
//			break;
//		}
	}
	private void showArrow(int whichArrow, int requestedX) {
		if(isArrow)return;
        final View showArrow = (whichArrow == R.id.arrow_up) ? mArrowUp : mArrowDown;
        final View hideArrow = (whichArrow == R.id.arrow_up) ? mArrowDown : mArrowUp;

        final int arrowWidth = mArrowUp.getMeasuredWidth();

        showArrow.setVisibility(View.VISIBLE);
        
        ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams)showArrow.getLayoutParams();
       
        param.leftMargin = requestedX - arrowWidth / 2;
        
        hideArrow.setVisibility(View.INVISIBLE);
    }
	public void setOnDismissListener(OnDismissListener listener) {
		setOnDismissListener(this);
		
		mDismissListener = listener;
	}
	
	@Override
	public void onDismiss() {
		if (!mDidAction && mDismissListener != null) {
			mDismissListener.onDismiss();
		}
	}
	public void hide() {
		try{
			
		if (mWindow != null && mWindow.isShowing()) {
			mWindow.dismiss() ;
		}
		}catch (Exception e) {
			System.out.println("########################EXCEPTION HANDLED#############################");
			e.printStackTrace();
		}
	}
	public boolean isShowing(){
		return mWindow.isShowing();
	}
	public interface OnActionItemClickListener {
		public abstract void onItemClick(QuickAction source, int pos, int actionId);
	}
	
	public interface OnDismissListener {
		public abstract void onDismiss();
	}

	@Override
	public void onThumbnailResponse(ThumbnailImage value, byte[] data) {
		// TODO Auto-generated method stub
		
	}
	
	public int getStatusBarHeight() {
		  int result = 0;
		  int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		  if (resourceId > 0) {
		      result = context.getResources().getDimensionPixelSize(resourceId);
		  }
		  return result;
		}

	@Override
	public void onFileDownloadResposne(View view, int type, byte[] data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFileDownloadResposne(View view, int[] type,
			String[] file_urls, String[] file_paths) {
		// TODO Auto-generated method stub
		
	}

}
