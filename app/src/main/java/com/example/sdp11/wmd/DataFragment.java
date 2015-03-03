package com.example.sdp11.wmd;


import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class DataFragment extends Fragment {
    private final static String TAG = DataFragment.class.getSimpleName();

    private View view;
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
        for (int i = 0; i < 25; i++) dataSource.createThrow(1, 2, 3, 4, 5, 6, 7);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_data, container, false);

        ListView throwDataList = (ListView)view.findViewById(R.id.list);

        List<ThrowData> values = dataSource.getAllThrows();

        adapter = new ThrowDataListAdapter();
        throwDataList.setAdapter(adapter);

        for (ThrowData t : values) {
            adapter.addThrow(t);
        }

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
            mInflator = getActivity().getLayoutInflater();
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
                viewHolder.throwID = (TextView) view.findViewById(R.id.throw_id);
                viewHolder.totalDistance = (TextView) view.findViewById(R.id.total_distance);
                viewHolder.throwIntegrity = (TextView) view.findViewById(R.id.throw_integrity);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            ThrowData t = throwDataList.get(i);
//            final String tid = String.valueOf(t.getThrowId());
//            if (tid != null && tid.length() > 0){
//                if(viewHolder.throwID == null) Log.e(TAG, "viewHolder.throwID is null");
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
