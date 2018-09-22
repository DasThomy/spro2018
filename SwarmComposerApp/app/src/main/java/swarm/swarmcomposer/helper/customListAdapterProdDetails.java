package swarm.swarmcomposer.helper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import swarm.swarmcomposer.activity.AppInstance;
import swarm.swarmcomposer.R;

/**
 * custom list adapter for the product view
 */
public class customListAdapterProdDetails extends ArrayAdapter<ArrayList<String>> {

    private ArrayList<ArrayList<String>> arrayLists;
    private ArrayList<String> arrayListTitles;


    public customListAdapterProdDetails(@NonNull Context context, ArrayList<ArrayList<String>> arrayLists, ArrayList<String> arrayListTitles) {
        super(context, R.layout.listadapterrow, arrayLists);
        this.arrayLists = arrayLists;
        this.arrayListTitles = arrayListTitles;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.textview, parent, false);
        TextView textViewTitles = customView.findViewById(R.id.texViewProdLeft);
        TextView textViewInfo = customView.findViewById(R.id.texViewProdRight);
        textViewTitles.setText(arrayListTitles.get(position));
        textViewTitles.setTextSize(AppInstance.getInstance().getData().getFontSizeNormalText());
        String addThisText = "";
        if (arrayLists.get(position) != null) {
            for (String iter : arrayLists.get(position)) {
                addThisText += iter;
                addThisText += "\n";
            }
            // remove last new Line
            if (addThisText.length() > 0)
                addThisText = addThisText.substring(0, addThisText.length() - 1);
        }
        textViewInfo.setText(addThisText);
        textViewInfo.setTextSize(AppInstance.getInstance().getData().getFontSizeNormalText());
        return customView;
    }
}
