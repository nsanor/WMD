package com.example.sdp11.wmd;


import android.app.Fragment;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class DataFragment extends Fragment {
    private final static String TAG = DataFragment.class.getSimpleName();

    private ThrowsDataSource dataSource;
    private ThrowDataListAdapter adapter;

    public DataFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataSource = MainActivity.dataSource;

        //Test
        dataSource.deleteAllThrows();
        addDemoThrows();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_data, container, false);

        ListView throwDataListView = (ListView) view.findViewById(R.id.list);

        List<ThrowData> values = dataSource.getAllThrows();

        adapter = new ThrowDataListAdapter(values);
        throwDataListView.setAdapter(adapter);

        throwDataListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> aView, View v, int pos, long id) {
                try {
                    Intent intent = new Intent(getActivity(), ThrowDataActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        dataSource.open();
        super.onResume();

    }

    @Override
    public void onPause() {
        dataSource.close();
        super.onPause();
    }

    private void addDemoThrows() {
        dataSource.createThrow(1, 1, 1, 1, 75, 1, 1);
        dataSource.createThrow(1, 1, 1, 1, 78, 1, 1);
        dataSource.createThrow(1, 1, 1, 1, 62, 0.85, 1);
        dataSource.createThrow(1, 1, 1, 1, 80, 1, 1);
        dataSource.createThrow(1, 1, 1, 1, 20, 0.2, 1);
        dataSource.createThrow(1, 1, 1, 1, 82, 0.94, 1);
        dataSource.createThrow(1, 1, 1, 1, 75, 1, 1);
        dataSource.createThrow(1, 1, 1, 1, 66, 1, 1);
        dataSource.createThrow(1, 1, 1, 1, 59, 0.57, 1);
        dataSource.createThrow(1, 1, 1, 1, 70, 0.8, 1);
    }

    static class ViewHolder{
        TextView throwID;
        TextView totalDistance;
        TextView throwIntegrity;
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
            viewHolder.throwIntegrity = (TextView) view.findViewById(R.id.row_throw_integrity);

            ThrowData t = throwDataList.get(i);
//            final String tid = String.valueOf(t.getThrowId());
//            if (tid != null && tid.length() > 0){
//                if(viewHolder.throwID == null) Log.i(TAG, "viewHolder.throwID is null");
//
//            }
//            else
//                viewHolder.throwID.setText(R.string.unknown_device);

            viewHolder.throwID.setText(String.valueOf(t.getThrowId()));
            viewHolder.totalDistance.setText(String.valueOf(t.getTotalDistance()));
            viewHolder.throwIntegrity.setText(String.valueOf(t.getThrowIntegrity()));

            return view;
        }
    }

}
