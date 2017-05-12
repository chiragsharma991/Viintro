package com.viintro.Viintro.Messages;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.viintro.Viintro.Constants.Configuration_Parameter;
import com.viintro.Viintro.Model.Messages;
import com.viintro.Viintro.R;
import com.viintro.Viintro.Reusables.CommonFunctions;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by hasai on 24/04/17.
 */

public class MessageConversationAdapter extends BaseAdapter {

    private Context context;
    private ArrayList arr_messages_conversation;
    private SharedPreferences sharedPreferences;
    private Configuration_Parameter m_config;


    public MessageConversationAdapter(Context context, ArrayList arr_messages_conversation)
    {
        this.context = context;
        this.arr_messages_conversation = arr_messages_conversation;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        m_config = Configuration_Parameter.getInstance();

    }

    @Override
    public int getCount() {
        if (arr_messages_conversation != null) {
            return arr_messages_conversation.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int i)
    {
        return arr_messages_conversation.get(i);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final Messages message = (Messages) arr_messages_conversation.get(position);
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
        {
            convertView = vi.inflate(R.layout.adapter_message_conversion, parent, false);
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }


        boolean isOutgoing = (message.getSender_id() == sharedPreferences.getInt(m_config.login_id,0)) ;
        setAlignment(holder, isOutgoing);

        holder.txtMessage.setText(message.getText());
        holder.txt_Time.setText(CommonFunctions.getLocalTime(message.getTime_ago()));




        return convertView;
    }

    public void add(Messages message) {
        arr_messages_conversation.add(message);
    }

    public void add(ArrayList<Messages> messages) {
        arr_messages_conversation.addAll(messages);
    }

    private void setAlignment(ViewHolder holder, boolean isOutgoing) {
        if (!isOutgoing)//incoming
        {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.contentWithBG.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            lp.setMargins(10,0,100,10);
            holder.content.setLayoutParams(lp);

            layoutParams = (LinearLayout.LayoutParams) holder.txt_Time.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            layoutParams.setMargins(50,0,10,0);
            holder.txt_Time.setLayoutParams(layoutParams);

            if (holder.txtMessage != null) {
                holder.content.setBackgroundResource(R.color.orange);
                layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
                layoutParams.gravity = Gravity.LEFT;
                layoutParams.setMargins(50,0,10,0);
                holder.txtMessage.setLayoutParams(layoutParams);
            } else {
                holder.content.setBackgroundResource(android.R.color.transparent);
            }
        }
        else
        {

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.contentWithBG.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            lp.setMargins(100,0,10,10);
            holder.content.setLayoutParams(lp);

            layoutParams = (LinearLayout.LayoutParams) holder.txt_Time.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            layoutParams.setMargins(10,0,50,0);
            holder.txt_Time.setLayoutParams(layoutParams);


            if (holder.txtMessage != null) {
                holder.content.setBackgroundResource(R.color.white);
                layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
                layoutParams.gravity = Gravity.RIGHT;
                layoutParams.setMargins(10,0,50,0);
                holder.txtMessage.setLayoutParams(layoutParams);
            } else {
                holder.content.setBackgroundResource(android.R.color.transparent);
            }
        }
    }

    private ViewHolder createViewHolder(View v)
    {
        ViewHolder holder = new ViewHolder();
        holder.txtMessage = (TextView) v.findViewById(R.id.txtMessage);
        holder.content = (LinearLayout) v.findViewById(R.id.content);
        holder.contentWithBG = (LinearLayout) v.findViewById(R.id.contentWithBackground);
        holder.txt_Time = (TextView) v.findViewById(R.id.txt_Time);
        return holder;
    }

    private static class ViewHolder
    {
        public TextView txtMessage;
        public TextView txt_Time;
        public LinearLayout content;
        public LinearLayout contentWithBG;
    }
}
