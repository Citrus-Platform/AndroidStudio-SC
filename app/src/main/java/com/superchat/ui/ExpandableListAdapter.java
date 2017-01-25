package com.superchat.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.superchat.R;
import com.superchat.SuperChatApplication;
import com.superchat.data.db.DBWrapper;
import com.superchat.utils.BitmapDownloader;
import com.superchat.utils.Constants;
import com.superchat.utils.Log;
import com.superchat.utils.SharedPrefManager;
import com.superchat.utils.UtilSetFont;
import com.superchat.widgets.RoundedImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by citrus on 9/20/2016.
 */
public class ExpandableListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int HEADER = 0;
    public static final int CHILD = 1;

    private List<Item> data;
    public Context context;
    private ConnectorDrawer connectorDrawer;

    public ExpandableListAdapter(List<Item> data, Context context, ConnectorDrawer connectorDrawer) {
        this.data = data;
        this.context = context;
        this.connectorDrawer = connectorDrawer;

        if(hmHeaderInfo != null){
            hmHeaderInfo.clear();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View view = null;
        Context context = parent.getContext();
        float dp = context.getResources().getDisplayMetrics().density;
        int subItemPaddingLeft = (int) (18 * dp);
        int subItemPaddingTopAndBottom = (int) (5 * dp);
        switch (type) {
            case HEADER:
                LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.list_header, parent, false);
                ListHeaderViewHolder header = new ListHeaderViewHolder(view);
                return header;
            case CHILD:
                LayoutInflater inflaterTemp = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflaterTemp.inflate(R.layout.list_child, parent, false);
                ListChildViewHolder child = new ListChildViewHolder(view);
                return child;
        }

        UtilSetFont.setFontMainScreen(view);

        return null;
    }

    public void expandHubs(final ListHeaderViewHolder itemController, final Item item) {
        int pos = data.indexOf(itemController.refferalItem);
        int index = pos + 1;
        for (Item i : item.invisibleChildren) {
            data.add(index, i);
            index++;
        }
        notifyItemRangeInserted(pos + 1, index - pos - 1);
        itemController.btn_expand_toggle.setImageResource(R.drawable.arrow_up);
        item.invisibleChildren = null;
    }

    public void hideHubs(final ListHeaderViewHolder itemController, final Item item) {
        item.invisibleChildren = new ArrayList<Item>();
        int count = 0;
        int pos = data.indexOf(itemController.refferalItem);
        while (data.size() > pos + 1 && data.get(pos + 1).type == CHILD) {
            item.invisibleChildren.add(data.remove(pos + 1));
            count++;
        }
        notifyItemRangeRemoved(pos + 1, count);
        itemController.btn_expand_toggle.setImageResource(R.drawable.arrow_down);
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    private HashMap<Integer, ListHeaderViewHolder> hmHeaderInfo = new HashMap<>();
    private void addHeaderToHashMap(ListHeaderViewHolder holder, final int position){
        hmHeaderInfo.put(position, holder);
    }

    public void openAllHeaders(){
        try {
            Set set = hmHeaderInfo.keySet();
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                int key = (int) itr.next();
                ListHeaderViewHolder holder = hmHeaderInfo.get(key);

                Item item = data.get(key);

                expandHubs(holder, item);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final Item item = data.get(position);
        switch (item.type) {
            case HEADER:
                final ListHeaderViewHolder itemController = (ListHeaderViewHolder) holder;

                itemController.refferalItem = item;
                itemController.header_title.setText(item.text);
                itemController.btn_expand_toggle.setImageResource(R.drawable.right_arrow);

                if (item.text.equalsIgnoreCase(FragmentDrawer.HEADER_OPEN_HUB)) {

                } else if (item.text.equalsIgnoreCase(FragmentDrawer.HEADER_NEW_INVITATION)) {

                } else if (item.text.equalsIgnoreCase(FragmentDrawer.HEADER_CREATE_NEW_HUB)) {

                } else {
                    addHeaderToHashMap(itemController, position);

                    if (item.invisibleChildren == null) {
                        itemController.btn_expand_toggle.setImageResource(R.drawable.arrow_up);
                    } else {
                        itemController.btn_expand_toggle.setImageResource(R.drawable.arrow_down);
                    }
                    //expandHubs(itemController, item);
                }

                itemController.llHeaderExpandable.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (item.text.equalsIgnoreCase(FragmentDrawer.HEADER_OPEN_HUB)) {
                            if (connectorDrawer != null) {
                                connectorDrawer.eventClickedOpenHub();
                            }
                        } else if (item.text.equalsIgnoreCase(FragmentDrawer.HEADER_NEW_INVITATION)) {
                            if (connectorDrawer != null) {
                                connectorDrawer.eventClickedInvitaion();
                            }
                        } else if (item.text.equalsIgnoreCase(FragmentDrawer.HEADER_CREATE_NEW_HUB)) {
                            if (connectorDrawer != null) {
                                connectorDrawer.eventClickedCreateNewHub();
                            }
                        } else {
                            if (item.invisibleChildren == null) {
                                hideHubs(itemController, item);
                            } else {
                                expandHubs(itemController, item);
                            }
                        }
                    }
                });

                break;
            case CHILD:
                final ListChildViewHolder itemControllerChild = (ListChildViewHolder) holder;
                itemControllerChild.refferalItem = item;

                itemControllerChild.child_title.setText(item.text);

                if (item.domainType != null) {

                    String domainTypeShowing = item.domainType;

                    if (item.domainType.equalsIgnoreCase("rwa")) {
                        domainTypeShowing = domainTypeShowing.toUpperCase();
                    } else if (item.domainType.equalsIgnoreCase("company")) {
                        domainTypeShowing = "Corporate";
                    } else {
                        domainTypeShowing = "Others";
                    }

                    if (DBWrapper.getInstance().isSGOwner(item.actualName))
                        itemControllerChild.child_sg_type.setText("OWNER [ " + domainTypeShowing + " ]");
                    else
                        itemControllerChild.child_sg_type.setText("[ " + domainTypeShowing + " ]");
                }
//                else
//                    itemControllerChild.child_sg_type.setText("[Owner]");
                ///////////////////////////////////////////////////////
                String fileId = DBWrapper.getInstance().getSGLogoFileID(item.actualName);
                if (fileId == null) {
                    SharedPrefManager pref = SharedPrefManager.getInstance();
                    fileId = pref.getSGFileId(item.actualName);
                }
                if (fileId != null && fileId.length() > 0) {
                    setProfilePic(itemControllerChild.displayPicture, fileId);
                } else {
//                    setProfilePic(itemControllerChild.displayPicture, null);//logo_small
                    itemControllerChild.displayPicture.setImageResource(R.drawable.small_hub_icon);
                }
                //count of child//////////////
                boolean muteId = SharedPrefManager.getInstance().isSnoozeExpired(item.actualName);

//                Log.e("here" , "mute : "+muteId);
                if (!muteId) {
                    itemControllerChild.btn_notify_toggle.setVisibility(View.VISIBLE);
                } else {
                    itemControllerChild.btn_notify_toggle.setVisibility(View.GONE);
                }
                /*int muteId = DBWrapper.getInstance().getSGMuteInfo(item.actualName);
                if (muteId == 0) {
                    itemControllerChild.btn_notify_toggle.setVisibility(View.GONE);
                } else {
                    itemControllerChild.btn_notify_toggle.setVisibility(View.VISIBLE);
                }*/

                //notify or not////////////
                int countId = DBWrapper.getInstance().getNewMessageCountForSG(item.actualName);
                if (countId == 0 || item.actualName.equalsIgnoreCase(SharedPrefManager.getInstance().getUserDomain())) {
//                    itemControllerChild.countContainer.setVisibility(View.GONE);
                    itemControllerChild.child_notificationCount.setText("");
                } else {
//                    itemControllerChild.countContainer.setVisibility(View.VISIBLE);
                    itemControllerChild.child_notificationCount.setText("(" + countId + ")");
                }

                //To make the current SG selected

                if (item.text.equalsIgnoreCase(SharedPrefManager.getInstance().getCurrentSGDisplayName()) ||
                        item.actualName.equalsIgnoreCase(SharedPrefManager.getInstance().getUserDomain())) {
                    itemControllerChild.container.setBackgroundColor(context.getResources().getColor(R.color.colorDrawerHubSelected));
                } else {
                    itemControllerChild.container.setBackgroundColor(context.getResources().getColor(R.color.colorDrawerHubUnSelected));
                }

                //activate or not////////////
                boolean active = DBWrapper.getInstance().isSGActive(item.actualName);
                if (active) {
                    itemControllerChild.btn_active_toggle.setVisibility(View.GONE);
                } else {
                    itemControllerChild.btn_active_toggle.setVisibility(View.VISIBLE);
                    itemControllerChild.btn_active_toggle.setBackgroundResource(R.drawable.deactivated);
                }

                //DBWrapper.getInstance().getSGDisplayName(sg);
                ///////////////////////////////////////////////////////


                itemControllerChild.btn_active_toggle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("temp", "is here");

                    }
                });

                itemControllerChild.container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance();
                        String user = sharedPrefManager.getUserPhone();
                        sharedPrefManager.saveSelectedIndexNav(position);
                        if (user != null && user.contains("-"))
                            user = user.replace("-", "");
                        if (context != null) {
                            HomeScreen.userDeactivated = false;
                            ((HomeScreen) context).switchSG(user + "_" + item.actualName, false, null, false);
                            notifyItemChanged(position);
                        }
                    }
                });
                break;
        }
    }

    private String getImagePath(String groupPicId) {
        if (groupPicId == null)
            groupPicId = SharedPrefManager.getInstance().getUserFileId(SharedPrefManager.getInstance().getUserName());
        if (groupPicId != null) {
            String profilePicUrl = groupPicId + ".jpg";
            File file = Environment.getExternalStorageDirectory();
            return new StringBuffer(file.getPath()).append(File.separator).append("SuperChat/").append(profilePicUrl).toString();
//			return Environment.getExternalStorageDirectory().getPath()+ File.separator +Constants.contentProfilePhoto+groupPicId+".jpg";
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).type;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private static class ListHeaderViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout llHeaderExpandable;
        public TextView header_title;
        public ImageView btn_expand_toggle;
        public Item refferalItem;

        public ListHeaderViewHolder(View itemView) {
            super(itemView);
            llHeaderExpandable = (LinearLayout) itemView.findViewById(R.id.llHeaderExpandable);
            header_title = (TextView) itemView.findViewById(R.id.header_title);
            btn_expand_toggle = (ImageView) itemView.findViewById(R.id.btn_expand_toggle);

            UtilSetFont.setFontMainScreen(itemView);
        }
    }

    private static class ListChildViewHolder extends RecyclerView.ViewHolder {
        public ImageView displayPicture;
        public TextView child_title;
        public TextView child_sg_type;
        public ImageView btn_notify_toggle;
        public ImageView btn_active_toggle;
        public TextView child_notificationCount;
        public RelativeLayout countContainer;
        public Item refferalItem;
        public RelativeLayout container;

        public ListChildViewHolder(View itemView) {
            super(itemView);

            displayPicture = (ImageView) itemView.findViewById(R.id.displayPicture);
            child_title = (TextView) itemView.findViewById(R.id.child_title);
            child_sg_type = (TextView) itemView.findViewById(R.id.child_sg_type);
            child_notificationCount = (TextView) itemView.findViewById(R.id.child_notificationCount);
            btn_notify_toggle = (ImageView) itemView.findViewById(R.id.btn_notify_toggle);
            btn_active_toggle = (ImageView) itemView.findViewById(R.id.btn_active_toggle);
//            countContainer = (RelativeLayout) itemView.findViewById(R.id.countContainer);
            container = (RelativeLayout) itemView.findViewById(R.id.container);

            UtilSetFont.setFontMainScreen(itemView);
        }
    }

    public static class Item {
        public int type;
        public String actualName = "";
        public String text = "";
        public String count = "";
        public String notify = "";
        public String domainType = "";
        public List<Item> invisibleChildren = new ArrayList<>();

        public Item() {
        }

        public Item(int type, String text, String count, String notify, String actualName, String domainType) {
            this.type = type;
            this.text = text;
            this.actualName = actualName;
            this.domainType = domainType;

            this.count = count;
            this.notify = notify;
        }

        public Item(int type, String text, List<Item> invisibleChildren) {
            this.type = type;
            this.text = text;
            this.invisibleChildren = invisibleChildren;
            if (invisibleChildren != null) {
                this.count = "" + invisibleChildren.size();
            }
        }
    }

    private boolean setProfilePic(ImageView picView, String groupPicId) {
//		System.out.println("groupPicId : "+groupPicId);
        String img_path = getThumbPath(groupPicId);
        picView.setImageResource(R.drawable.small_hub_icon);
        if (groupPicId == null || (groupPicId != null && groupPicId.equals("")) || groupPicId.equals("clear") || groupPicId.contains("logofileid"))
            return false;
        if (img_path != null) {
            File file1 = new File(img_path);
//			Log.d(TAG, "PicAvailibilty: "+ Uri.parse(filename)+" , "+filename+" , "+file1.exists());
            if (file1.exists()) {
                picView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//				picView.setImageURI(Uri.parse(img_path));
                setThumb((ImageView) picView, img_path, groupPicId);
                return true;
            } else {
                if (Build.VERSION.SDK_INT >= 11)
                    new BitmapDownloader((RoundedImageView) picView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, groupPicId, BitmapDownloader.THUMB_REQUEST);
                else
                    new BitmapDownloader((RoundedImageView) picView).execute(groupPicId, BitmapDownloader.THUMB_REQUEST);
            }
        } else {
            if (Build.VERSION.SDK_INT >= 11)
                new BitmapDownloader((RoundedImageView) picView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, groupPicId, BitmapDownloader.THUMB_REQUEST);
            else
                new BitmapDownloader((RoundedImageView) picView).execute(groupPicId, BitmapDownloader.THUMB_REQUEST);

        }
        return false;
    }

    private void setThumb(ImageView imageViewl, String path, String groupPicId) {
        BitmapFactory.Options bfo = new BitmapFactory.Options();
        bfo.inSampleSize = 2;
        Bitmap bm = null;
        try {
            bm = BitmapFactory.decodeFile(path, bfo);
//		    bm = ThumbnailUtils.extractThumbnail(bm, 200, 200);
            bm = rotateImage(path, bm);
//		    bm = Bitmap.createScaledBitmap(bm, 200, 200, true);
        } catch (Exception ex) {

        }
        if (bm != null) {
            imageViewl.setImageBitmap(bm);
//	    	SuperChatApplication.addBitmapToMemoryCache(groupPicId,bm);
        } else {
            try {
                imageViewl.setImageURI(Uri.parse(path));
            } catch (Exception e) {

            }
        }
    }

    public static Bitmap rotateImage(String path, Bitmap bm) {
        int orientation = 1;
        try {
            ExifInterface exifJpeg = new ExifInterface(path);
            orientation = exifJpeg.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

////			orientation = Integer.parseInt(exifJpeg.getAttribute(ExifInterface.TAG_ORIENTATION));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (orientation != ExifInterface.ORIENTATION_NORMAL) {
            int width = bm.getWidth();
            int height = bm.getHeight();
            Matrix matrix = new Matrix();
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                matrix.postRotate(90);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                matrix.postRotate(180);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                matrix.postRotate(270);
            }
            return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        }

        return bm;
    }

    private String getThumbPath(String groupPicId) {
        if (groupPicId == null)
            groupPicId = SharedPrefManager.getInstance().getUserFileId(SharedPrefManager.getInstance().getUserName()); // 1_1_7_G_I_I3_e1zihzwn02
        if (groupPicId != null) {
            String profilePicUrl = groupPicId + ".jpg";//AppConstants.media_get_url+
            File file = Environment.getExternalStorageDirectory();
            String filename = file.getPath() + File.separator + Constants.contentProfilePhoto + profilePicUrl;
            File contentFile = new File(filename);
            if (contentFile != null && contentFile.exists()) {
                return filename;
            }

        }
        return null;
    }
}
