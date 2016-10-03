package com.superchat.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.superchat.R;
import com.superchat.utils.Log;
import com.superchat.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by citrus on 9/20/2016.
 */
public class ExpandableListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int HEADER = 0;
    public static final int CHILD = 1;

    private List<Item> data;
    public Context context;

    public ExpandableListAdapter(List<Item> data, Context context) {
        this.data = data;
        this.context = context;
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
        return null;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Item item = data.get(position);
        switch (item.type) {
            case HEADER:
                final ListHeaderViewHolder itemController = (ListHeaderViewHolder) holder;
                itemController.refferalItem = item;
                itemController.header_title.setText(item.text);

                if (item.text.equalsIgnoreCase("Super Group")) {
                    itemController.btn_expand_toggle.setImageResource(R.drawable.ic_launcher);
                } else {
                    if (item.invisibleChildren == null) {
                        itemController.btn_expand_toggle.setImageResource(R.drawable.ic_launcher);
                    } else {
                        itemController.btn_expand_toggle.setImageResource(R.drawable.ic_launcher);
                    }

                    itemController.btn_expand_toggle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (item.invisibleChildren == null) {
                                item.invisibleChildren = new ArrayList<Item>();
                                int count = 0;
                                int pos = data.indexOf(itemController.refferalItem);
                                while (data.size() > pos + 1 && data.get(pos + 1).type == CHILD) {
                                    item.invisibleChildren.add(data.remove(pos + 1));
                                    count++;
                                }
                                notifyItemRangeRemoved(pos + 1, count);
                                itemController.btn_expand_toggle.setImageResource(R.drawable.ic_launcher);
                            } else {
                                int pos = data.indexOf(itemController.refferalItem);
                                int index = pos + 1;
                                for (Item i : item.invisibleChildren) {
                                    data.add(index, i);
                                    index++;
                                }
                                notifyItemRangeInserted(pos + 1, index - pos - 1);
                                itemController.btn_expand_toggle.setImageResource(R.drawable.ic_launcher);
                                item.invisibleChildren = null;
                            }
                        }
                    });
                }

                break;
            case CHILD:
                final ListChildViewHolder itemControllerChild = (ListChildViewHolder) holder;
                itemControllerChild.refferalItem = item;
                itemControllerChild.child_title.setText(item.text);

                if (item.count != null) {
                    if (item.count.equalsIgnoreCase("0")) {
                        itemControllerChild.countContainer.setVisibility(View.GONE);
                    } else {
                        itemControllerChild.countContainer.setVisibility(View.VISIBLE);
                        itemControllerChild.child_notificationCount.setText("" + item.count);
                    }
                } else {
                    itemControllerChild.countContainer.setVisibility(View.GONE);
                }


                if (item.notify != null) {
                    if (item.notify.equalsIgnoreCase("notificationOff")) {
                        itemControllerChild.btn_notify_toggle.setImageResource(R.drawable.ic_launcher);
                    } else if (item.notify.equalsIgnoreCase("disconnected")) {
                        itemControllerChild.btn_notify_toggle.setImageResource(R.drawable.ic_launcher);
                    } else if (item.notify.equalsIgnoreCase("notConnected")) {
                        itemControllerChild.btn_notify_toggle.setImageResource(R.drawable.ic_launcher);
                    } else {
                        itemControllerChild.btn_notify_toggle.setBackgroundDrawable(null);
                    }
                } else {
                    itemControllerChild.btn_notify_toggle.setBackgroundDrawable(null);
                }


                itemControllerChild.btn_notify_toggle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("temp", "is here");

                    }
                });

                itemControllerChild.container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("temp", "change UI");
                        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance();
                        sharedPrefManager.saveUserName("918130069224" + "_" + item.text);
                        sharedPrefManager.saveUserPassword("ykqMT9n4gX");
                        //((HomeScreen)context).LoginTaskOnServer();
                    }
                });
                break;
        }
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
        public TextView header_title;
        public ImageView btn_expand_toggle;
        public Item refferalItem;

        public ListHeaderViewHolder(View itemView) {
            super(itemView);
            header_title = (TextView) itemView.findViewById(R.id.header_title);
            btn_expand_toggle = (ImageView) itemView.findViewById(R.id.btn_expand_toggle);
        }
    }

    private static class ListChildViewHolder extends RecyclerView.ViewHolder {
        public TextView child_title;
        public ImageView btn_notify_toggle;
        public TextView child_notificationCount;
        public RelativeLayout countContainer;
        public Item refferalItem;
        public RelativeLayout container;

        public ListChildViewHolder(View itemView) {
            super(itemView);
            child_title = (TextView) itemView.findViewById(R.id.child_title);
            child_notificationCount = (TextView) itemView.findViewById(R.id.child_notificationCount);
            btn_notify_toggle = (ImageView) itemView.findViewById(R.id.btn_notify_toggle);
            countContainer = (RelativeLayout) itemView.findViewById(R.id.countContainer);
            container = (RelativeLayout) itemView.findViewById(R.id.container);
        }
    }

    public static class Item {
        public int type;
        public String text;
        public String count;
        public String notify;
        public List<Item> invisibleChildren;

        public Item() {
        }

        public Item(int type, String text, String count, String notify) {
            this.type = type;
            this.text = text;

            this.count = count;
            this.notify = notify;
        }
    }
}
