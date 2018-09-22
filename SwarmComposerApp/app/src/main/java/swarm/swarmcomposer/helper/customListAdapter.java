package swarm.swarmcomposer.helper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import swarm.swarmcomposer.activity.AppInstance;
import swarm.swarmcomposer.R;

/**
 * List Adapter for product list and combination list
 */
public class customListAdapter extends ArrayAdapter<ListElement> {

    private ArrayList<ListElement> listElements;


    public customListAdapter(@NonNull Context context, ArrayList<ListElement> listElements) {
        super(context, R.layout.listadapterrow, listElements);
        this.listElements = listElements;

    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ListElement listElement = listElements.get(position);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.listadapterrow, parent, false);

        TextView textView = customView.findViewById(R.id.textViewRow);
        textView.setText(listElement.getName());
        textView.setTextSize(AppInstance.getInstance().getData().getFontSizeNormalText());
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        customView.setTag(listElement.getId());
        return customView;
    }
}
