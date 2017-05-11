package net.kyouko.toastcapturer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.kyouko.toastcapturer.model.Message;

import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.ViewHolder> {

    private List<Message> messages;

    public MessageListAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new ViewHolder(view);
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.textType.setText(message.type);
        holder.textPackage.setText(message.appName);
        holder.textTime.setText(message.time);
        holder.textContent.setText(message.content);
    }

    @Override public int getItemCount() {
        return messages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textType;
        public TextView textPackage;
        public TextView textTime;
        public TextView textContent;

        public ViewHolder(View itemView) {
            super(itemView);
            textType = (TextView) itemView.findViewById(R.id.text_type);
            textPackage = (TextView) itemView.findViewById(R.id.text_package);
            textTime = (TextView) itemView.findViewById(R.id.text_time);
            textContent = (TextView) itemView.findViewById(R.id.text_content);
        }

    }

}
