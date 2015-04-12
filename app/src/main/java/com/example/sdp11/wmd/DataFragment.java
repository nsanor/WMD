package com.example.sdp11.wmd;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class DataFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private final static String TAG = DataFragment.class.getSimpleName();

    private ThrowsDataSource dataSource;
    private ThrowDataListAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private long gameId;

    public DataFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //Test
        if(dataSource == null) {
            dataSource = MainActivity.dataSource;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_data, container, false);

        ListView throwDataListView = (ListView) view.findViewById(R.id.list);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright);
        gameId = TotalsData.getGameId();

        List<ThrowData> values = dataSource.getAllThrows(gameId);

        adapter = new ThrowDataListAdapter(values);
        throwDataListView.setAdapter(adapter);

        throwDataListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> aView, View v, int pos, long id) {
                try {
                    //Log.e(TAG, "Original: " + adapter.getThrow(pos).getTotalTime() + ", Converted: " + convertToGPSTime(adapter.getThrow(pos).getTotalTime()));
                    Intent intent = new Intent(getActivity(), ThrowDataActivity.class);
                    intent.putExtra("Throw", adapter.getThrow(pos));
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    public String convertToGPSTime(double time) {
        //Subtract 4 hours to convert to Eastern time
        time -= 40000;
        String temp = String.valueOf(time);
        return temp.substring(0,2) + ":" + temp.substring(2,4) + ":" + temp.substring(4);
    }

    @Override
    public void onResume() {
        dataSource.open();
        super.onResume();

        if(dataSource.isEmpty(gameId)) {
            addDemoThrows();
            Log.e(TAG, "datasource is empty");
        }
        else Log.e(TAG, "datasource is not empty");

        refreshData();
    }

    @Override
    public void onPause() {
        dataSource.close();
        super.onPause();
    }



    private void addDemoThrows() {
        dataSource.createThrow(75, 0);
        dataSource.createThrow(78, 2.2);
        dataSource.createThrow(65, 10.1);
        dataSource.createThrow(100, 0);
        dataSource.createThrow(55, 4.3);
        dataSource.createThrow(35, 20);
        dataSource.createThrow(75, 1.48);
    }

    public void refreshData() {
        gameId = TotalsData.getGameId();
        adapter.clear();
        List<ThrowData> values = dataSource.getAllThrows(gameId);
        for(ThrowData t : values) {
            adapter.addThrow(t);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {}

    @Override
    public void onClick(View view) {

    }

    static class ViewHolder{
        TextView throwID;
        TextView totalDistance;
        TextView totalAngle;
    }

    // Adapter for holding devices found through scanning.
    private class ThrowDataListAdapter extends BaseAdapter {
        private ArrayList<ThrowData> throwDataList;
        private LayoutInflater mInflator;

        public ThrowDataListAdapter() {
            super();
            throwDataList = new ArrayList<ThrowData>();
            mInflator = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        public ThrowDataListAdapter(List<ThrowData> values) {
            super();
            throwDataList = new ArrayList<ThrowData>();
            for(ThrowData v : values) {
                throwDataList.add(v);
            }
            mInflator = getActivity().getLayoutInflater();
        }

        public void addThrow(ThrowData t) {
            throwDataList.add(t);
        }

        public ThrowData getThrow(int position) {
            return throwDataList.get(position);
        }

        public void clear() {
            throwDataList.clear();
        }

        @Override
        public int getCount() {
            return throwDataList.size();
        }

        @Override
        public Object getItem(int i) {
            return throwDataList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.

            if (view == null) {
                view = mInflator.inflate(R.layout.data_row, null);
                viewHolder = new ViewHolder();
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.throwID = (TextView) view.findViewById(R.id.row_throw_id);
            viewHolder.totalDistance = (TextView) view.findViewById(R.id.row_total_distance);
            viewHolder.totalAngle = (TextView) view.findViewById(R.id.row_total_angle);

            ThrowData t = throwDataList.get(i);

            viewHolder.throwID.setText(String.valueOf(t.getThrowId()));
            viewHolder.totalDistance.setText(String.valueOf(t.getTotalDistance()));
            viewHolder.totalAngle.setText(String.valueOf(t.getTotalAngle()));

            return view;
        }
    }

}
