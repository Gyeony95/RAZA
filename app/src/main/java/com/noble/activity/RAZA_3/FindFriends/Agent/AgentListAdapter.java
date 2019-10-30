package com.noble.activity.RAZA_3.FindFriends.Agent;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.noble.activity.RAZA_3.FindFriends.Socket.SocketWraper;
import com.noble.activity.RAZA_3.R;

import java.util.ArrayList;

/**
 * Created by cx on 2018/12/14.
 */

public class AgentListAdapter extends BaseAdapter {

    private final String TAG = AgentListAdapter.class.getName();

    private ArrayList<Agent> mAgents;

    private Context mContext;

    private LayoutInflater mLayoutInflater;

    private int mChooseItemIdx;

    private View mChooseView;

    public AgentListAdapter(Context context) {
        super();
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mAgents = new ArrayList<>();
        mChooseItemIdx = 0;
    }

    public void addAgent(Agent agent) {
        mAgents.add(agent);
    }

    public void removeAgent(Agent agent) {
        mAgents.remove(agent);
    }

    public void update() {
        this.notifyDataSetChanged();
    }

    public void reset() {
        mAgents.clear();
    }

    public Agent getChooseAgent() {
        if (mAgents.isEmpty())
            return null;

        return mAgents.get(mChooseItemIdx);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null)
            view = mLayoutInflater.inflate(R.layout.agent_list_cell, null);

        Log.e(TAG, "name : "+ mAgents.get(i).name());
        Log.e(TAG, "id : "+ mAgents.get(i).id());
        Log.e(TAG, "connect : "+ mAgents.get(i).connect());
        Log.e(TAG, "type : "+ mAgents.get(i).type());

        TextView textView = (TextView)view.findViewById(R.id.agent_list_cell_tv);
        String showText = new String("name:" + mAgents.get(i).name() + "connect:" + mAgents.get(i).connect()+" type:" + mAgents.get(i).type());


        textView.setText(showText);

        //나일때
        if (mAgents.get(i).id().equals(SocketWraper.shareContext().getUid())) {
            textView.setTextColor(Color.YELLOW);
        } else {//내가 아닐때
            textView.setTextColor(Color.RED);

            //여기부터
            Agent agent = getChooseAgent();
            if (agent != null) {
                SocketWraper.shareContext().setTarget(agent.id());
                SocketWraper.shareContext().invite();
            }
        }
        textView.setTag(new Integer(i));
        textView.setBackgroundColor(Color.WHITE);


        /*
        for(int k =0; k < mAgents.size()-1; k++){



            if (mAgents.get(i).id().equals(SocketWraper.shareContext().getUid())){

            }else{
                Log.e("sliver", "이거 실행되야하는데?!");
                Agent agent = getChooseAgent();
                if (agent != null) {
                    SocketWraper.shareContext().setTarget(agent.id());
                    SocketWraper.shareContext().invite();
                }

            }
        }
*/

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mChooseView != null) {
                    mChooseView.setBackgroundColor(Color.WHITE);
                }
                view.setBackgroundColor(Color.GRAY);
                Integer idx = (Integer)view.getTag();
                mChooseItemIdx = idx.intValue();
                mChooseView = view;
                Log.e("sliver", "AgentListAdaper choose item idx:" + mChooseItemIdx);
            }
        });

        return view;
    }

    @Override
    public int getCount() {
        return mAgents.size();
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Nullable
    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }

    @Override
    public Object getItem(int i) {
        return null;
    }
}
