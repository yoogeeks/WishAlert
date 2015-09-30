package uha.ensisa.android.wishalert;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class HomeListAdapter extends BaseAdapter {

    private List<Event> listData;
    private LayoutInflater layoutInflater;

    public HomeListAdapter(Context aContext, List<Event> listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void removeAt(int position) { listData.remove(position); }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_view_item, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.type = (TextView) convertView.findViewById(R.id.type);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(listData.get(position).getName());
        holder.date.setText(listData.get(position).getDate());
        holder.type.setText(listData.get(position).getType());
        return convertView;

    }

    static class ViewHolder {
        TextView name;
        TextView date;
        TextView type;
    }
}
