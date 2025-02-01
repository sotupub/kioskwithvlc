package wenchao.kiosk;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
public class ChannelListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Channel> mChannelList;
    private int mSelectedPosition = -1;
    private ChannelSelectionListener mListener;
    private boolean leftSidebarActive;
    // ViewHolder class with CardView variables


    public void setSelectedPosition(int position) {
        mSelectedPosition = position;  // Update the selected position state
        Log.d("From The Log", "setSelectedPosition: "+position);
        notifyDataSetChanged();  // Notify the grid view to refresh and reflect the change
    }


    public ChannelListAdapter(Context context, ArrayList<Channel> channelList, ChannelSelectionListener listener) {
        this.mContext = context;
        this.mChannelList = channelList;
        this.mListener = listener;
        this.leftSidebarActive = false;
    }
    public void setLeftSidebarActive(boolean isActive) {
        this.leftSidebarActive = isActive;
        notifyDataSetChanged();  // Update the view
    }
    @Override
    public int getCount() {
        return mChannelList.size(); // total number of channels
    }
    public ArrayList<Channel> getChannelList() {
        return mChannelList;
    }



    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        int layoutId = leftSidebarActive ? R.layout.item_right_channel : R.layout.item_channel;
        if (convertView == null) {

            convertView = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Channel channel = mChannelList.get(position);
        holder.textViewChannelName.setText(channel.getName());
        Glide.with(mContext).load(channel.getLogoUrl()).override(Target.SIZE_ORIGINAL).into(holder.imageViewLogo);

        return convertView;
    }



    private GradientDrawable createDrawableForPosition(int position) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(8); // Set the corner radius

        if (position == mSelectedPosition) {
            // For selected item
            drawable.setStroke(3, mContext.getResources().getColor(R.color.white)); // White stroke
            drawable.setColor(mContext.getResources().getColor(R.color.light_gray)); // Semi-transparent background
        } else {
            // For unselected item
            drawable.setStroke(0, mContext.getResources().getColor(R.color.transparent)); // No stroke
            drawable.setColor(mContext.getResources().getColor(R.color.transparent_gray)); // Light gray background
        }
        return drawable;
    }



    static class ViewHolder {
        LinearLayout linearLayout1, linearLayout2, linearLayout3, linearLayout4;
        ImageView imageViewLogo1, imageViewLogo2, imageViewLogo3, imageViewLogo4;
        TextView textViewChannelName1, textViewChannelName2, textViewChannelName3, textViewChannelName4;
        ImageView imageViewLogo;
        TextView textViewChannelName;
        ViewHolder(View view) {
            linearLayout1 = view.findViewById(R.id.linearLayoutChannel1);
            linearLayout2 = view.findViewById(R.id.linearLayoutChannel2);
            linearLayout3 = view.findViewById(R.id.linearLayoutChannel3);
            linearLayout4 = view.findViewById(R.id.linearLayoutChannel4);
            imageViewLogo1 = view.findViewById(R.id.imageViewLogoList1);
            imageViewLogo2 = view.findViewById(R.id.imageViewLogoList2);
            imageViewLogo3 = view.findViewById(R.id.imageViewLogoList3);
            imageViewLogo4 = view.findViewById(R.id.imageViewLogoList4);
            textViewChannelName1 = view.findViewById(R.id.textViewChannelNameList1);
            textViewChannelName2 = view.findViewById(R.id.textViewChannelNameList2);
            textViewChannelName3 = view.findViewById(R.id.textViewChannelNameList3);
            textViewChannelName4 = view.findViewById(R.id.textViewChannelNameList4);
            imageViewLogo = view.findViewById(R.id.imageViewLogo);
            textViewChannelName = view.findViewById(R.id.textViewChannelName);
        }
    }



    public void updateChannelList(ArrayList<Channel> newChannelList) {
        mChannelList.clear();
        mChannelList.addAll(newChannelList);
        notifyDataSetChanged();
    }
    private void loadLogoWithGlide(String logoUrl, ImageView imageView) {
        Glide.with(mContext)
                .load(logoUrl)
                .placeholder(R.drawable.intro) // Placeholder image while loading
                .error(R.drawable.intro) // Image to show if loading fails
                .into(imageView);
    }

    private void handleChannelSelection(int position) {
        if (mSelectedPosition == position) {
            // If already selected, do nothing
            return;
        }

        // Update selected position
        mSelectedPosition = position;

        // Notify adapter of the change
        notifyDataSetChanged();
    }

    public interface ChannelSelectionListener {
        void onChannelSelected(Channel channel, int position);
    }

}

