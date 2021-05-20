package kr.ac.jbnu.sw.ServerTeamTestApplication.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import kr.ac.jbnu.sw.ServerTeamTestApplication.R;
import kr.ac.jbnu.sw.ServerTeamTestApplication.model.GlobalStorage;

public class ServerDataAdapter extends BaseAdapter {
    private Context mContext;
    private GlobalStorage globalStorage;

    public ServerDataAdapter(Context context) {
        this.mContext = context;
        globalStorage = GlobalStorage.getInstance();
    }

    @Override
    public int getCount() {
        return globalStorage.getReceiveServerDataMap().size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(R.layout.adapter_default_item, null);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.userIdTextView = (TextView)view.findViewById(R.id.user_id);
            viewHolder.userDescTextView = (TextView)view.findViewById(R.id.user_desc);

            view.setTag(viewHolder);
        }

        if (globalStorage.getReceiveServerDataMapKeySetArrayList().get(position) != null) {
            ViewHolder viewHolder = (ViewHolder)view.getTag();

            String key = globalStorage.getReceiveServerDataMapKeySetArrayList().get(position);

            viewHolder.userIdTextView.setText("id : " + key);
            viewHolder.userDescTextView.setText(formatString(globalStorage.getReceiveServerDataMap().get(key).toString()));
        }

        return view;
    }

    private static class ViewHolder {
        TextView userIdTextView;
        TextView userDescTextView;
    }

    private String formatString(String text){
        StringBuilder json = new StringBuilder();
        String indentString = "";

        for (int i = 0; i < text.length(); i++) {
            char letter = text.charAt(i);
            switch (letter) {
                case '{':
                case '[':
                    json.append("\n" + indentString + letter + "\n");
                    indentString = indentString + "\t";
                    json.append(indentString);
                    break;
                case '}':
                case ']':
                    indentString = indentString.replaceFirst("\t", "");
                    json.append("\n" + indentString + letter);
                    break;
                case ',':
                    json.append(letter + "\n" + indentString);
                    break;

                default:
                    json.append(letter);
                    break;
            }
        }

        return json.toString();
    }

}
