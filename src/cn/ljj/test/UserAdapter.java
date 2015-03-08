
package cn.ljj.test;

import java.util.List;

import cn.ljj.message.User;
import cn.ljj.messager.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class UserAdapter extends BaseAdapter {
    List<User> mData = null;
    private Context mContext = null;
    private LayoutInflater mInflater = null;

    public UserAdapter(List<User> data, Context context) {
        mContext = context;
        mData = data;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (mData == null) {
            return 0;
        }
        return mData.size();
    }

    public void setDataAndNotify(List<User> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layout_user_item, null);
            ViewHolder holder = new ViewHolder();
            holder.mName = (TextView) convertView.findViewById(R.id.text_name);
            holder.mId = (TextView) convertView.findViewById(R.id.text_id);
            holder.mStatus = (TextView) convertView.findViewById(R.id.text_status);
            convertView.setTag(holder);
        }
        bindView((User) getItem(position), convertView);
        return convertView;
    }

    private void bindView(User user, View view) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.mName.setText(user.getName());
//        holder.mId.setText(user.getIdentity() + "");
        holder.mStatus.setText(user.getStatus() + "");
    }

    class ViewHolder {
        TextView mName = null;
        TextView mId = null;
        TextView mStatus = null;
    }
}
