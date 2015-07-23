package apitest;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.ozzca_000.myapplication.R;

import java.util.List;

import YelpData.Business;
import database.DbAbstractionLayer;


/**
 * Created by Binesh on 7/22/2015.
 */
public class DownVotedListAdaptor extends BaseAdapter {

    private Context mContext;
    private Integer[] mResturantNames;
    public DownVotedListAdaptor(Context context, DownVotedList data){

        mContext= context;
        DbAbstractionLayer.getDbAbstractionLayer();
        Business[] downVotedBussiness = DbAbstractionLayer.getDownVotedList(context);

    }


    @Override
    public int getCount() {
        return 0;
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

        ImageView imageView;
        if(convertView == null){
            imageView = new ImageView(mContext);
            ListView mResturantNames = (ListView) convertView.findViewById(R.id.mResturantNames);
        }
        else{
            imageView = (ImageView) convertView;
            ListView mResturantNames = (ListView) convertView.findViewById(R.id.mResturantNames);
        }
        return convertView;

    }

}

