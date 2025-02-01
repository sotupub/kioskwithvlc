package wenchao.kiosk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ChannelAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Channel> channelList;
    private int selectedPosition = -1;
    private boolean leftSidebarActive = false; // Boolean to determine which layout to use

    public ChannelAdapter(Context context, ArrayList<Channel> channelList) {
        this.context = context;
        this.channelList = channelList;
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
        notifyDataSetChanged(); // Notify the adapter that the data has changed
    }

    public void setLeftSidebarActive(boolean isActive) {
        leftSidebarActive = isActive;
        notifyDataSetChanged();  // Update the view to reflect the new state
    }

    @Override
    public int getCount() {
        return channelList.size(); // Return the size of the channel list
    }

    @Override
    public Object getItem(int position) {
        return channelList.get(position); // Return the channel at the specified position
    }

    @Override
    public long getItemId(int position) {
        return position; // Return the position as the ID
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        // Choose the layout based on the sidebar state
        int layoutId = leftSidebarActive ? R.layout.item_left_channel : R.layout.item_channel; // Assume item_left_channel is similar to your described layout
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
            holder = new ViewHolder();
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Channel channel = channelList.get(position);
        holder.textViewChannelName.setText(channel.getName());
        // Use Glide to load the image. Ensure you manage placeholders and errors.
        Glide.with(context)
                .load(channel.getLogoUrl()) // Assuming getLogoUrl() gets you the correct URL
                .placeholder(R.drawable.intro) // Provide a default image during loading
                .error(R.drawable.intro) // Provide an error image if the URL fails to load
                .into(holder.imageViewLogo);

        return convertView;
    }

    static class ViewHolder {
        ImageView imageViewLogo;
        TextView textViewChannelName;
    }
}
