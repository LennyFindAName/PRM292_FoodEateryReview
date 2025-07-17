package prm392.project.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import prm392.project.R;
import prm392.project.model.MessageModel;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.graphics.Typeface;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_USER = 0;
    private static final int VIEW_TYPE_BOT = 1;

    private List<MessageModel> messageList;

    public MessageAdapter(List<MessageModel> messageList) {
        this.messageList = messageList;
    }

    @Override
    public int getItemViewType(int position) {
        MessageModel message = messageList.get(position);
        return message.isBot() ? VIEW_TYPE_BOT : VIEW_TYPE_USER;
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_USER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_user, parent, false);
            return new UserViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_bot, parent, false);
            return new BotViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel message = messageList.get(position);
        if (holder instanceof UserViewHolder) {
            ((UserViewHolder) holder).bind(message);
        } else if (holder instanceof BotViewHolder) {
            ((BotViewHolder) holder).bind(message);
        }
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView senderName, content;
        ImageView avatar;
        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            senderName = itemView.findViewById(R.id.text_sender_name);
            content = itemView.findViewById(R.id.text_message_content);
            avatar = itemView.findViewById(R.id.image_avatar);
        }
        void bind(MessageModel message) {
            senderName.setText(message.getSenderName());
            content.setText(message.getContent());
        }
    }

    static class BotViewHolder extends RecyclerView.ViewHolder {
        TextView senderName, content;
        ImageView avatar;
        BotViewHolder(@NonNull View itemView) {
            super(itemView);
            senderName = itemView.findViewById(R.id.text_sender_name);
            content = itemView.findViewById(R.id.text_message_content);
            avatar = itemView.findViewById(R.id.image_avatar);
        }
        void bind(MessageModel message) {
            senderName.setText(message.getSenderName());
            content.setText(parseMarkdownBold(message.getContent()));
        }
        private CharSequence parseMarkdownBold(String text) {
            SpannableStringBuilder builder = new SpannableStringBuilder();
            Pattern pattern = Pattern.compile("\\*\\*(.+?)\\*\\*");
            Matcher matcher = pattern.matcher(text);
            int lastEnd = 0;
            while (matcher.find()) {
                builder.append(text.substring(lastEnd, matcher.start()));
                String boldText = matcher.group(1);
                int start = builder.length();
                builder.append(boldText);
                builder.setSpan(new StyleSpan(Typeface.BOLD), start, start + boldText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                lastEnd = matcher.end();
            }
            builder.append(text.substring(lastEnd));
            return builder;
        }
    }
} 