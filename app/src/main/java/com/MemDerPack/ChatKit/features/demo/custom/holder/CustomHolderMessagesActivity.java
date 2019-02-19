package com.MemDerPack.ChatKit.features.demo.custom.holder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.MemDerPack.ChatKit.data.fixtures.MessagesFixtures;
import com.MemDerPack.ChatKit.data.model.Message;
import com.MemDerPack.ChatKit.features.demo.DemoMessagesActivity;
import com.MemDerPack.ChatKit.features.demo.custom.holder.holders.messages.CustomIncomingImageMessageViewHolder;
import com.MemDerPack.ChatKit.features.demo.custom.holder.holders.messages.CustomIncomingTextMessageViewHolder;
import com.MemDerPack.ChatKit.features.demo.custom.holder.holders.messages.CustomOutcomingImageMessageViewHolder;
import com.MemDerPack.ChatKit.features.demo.custom.holder.holders.messages.CustomOutcomingTextMessageViewHolder;
import com.MemDerPack.ChatKit.utils.AppUtils;
import com.MemDerPack.R;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.MessageContentType;
import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

public class CustomHolderMessagesActivity extends DemoMessagesActivity
        implements MessagesListAdapter.OnMessageLongClickListener<Message>,
        MessageInput.InputListener,
        MessageInput.AttachmentsListener {

    public static void open(Context context) {
        context.startActivity(new Intent(context, CustomHolderMessagesActivity.class));
    }

    private MessagesList messagesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_holder_messages);

        messagesList = (MessagesList) findViewById(R.id.messagesList);
        initAdapter();

        MessageInput input = (MessageInput) findViewById(R.id.input);
        input.setInputListener(this);
        input.setAttachmentsListener(this);
    }

    @Override
    public boolean onSubmit(CharSequence input) {
        messagesAdapter.addToStart(
                MessagesFixtures.getTextMessage(input.toString()), true);
        return true;
    }

    @Override
    public void onAddAttachments() {
        messagesAdapter.addToStart(MessagesFixtures.getImageMessage(), true);
    }

    @Override
    public void onMessageLongClick(Message message) {
        AppUtils.showToast(this, R.string.on_log_click_message, false);
    }

    private void initAdapter() {

        //We can pass any data to ViewHolder with payload
        CustomIncomingTextMessageViewHolder.Payload payload = new CustomIncomingTextMessageViewHolder.Payload();
        //For example click listener
        payload.avatarClickListener = new CustomIncomingTextMessageViewHolder.OnAvatarClickListener() {
            @Override
            public void onAvatarClick() {
                Toast.makeText(CustomHolderMessagesActivity.this,
                        "Text message avatar clicked", Toast.LENGTH_SHORT).show();
            }
        };

        MessageHolders holdersConfig = new MessageHolders()
                .setIncomingTextConfig(
                        (Class<? extends MessageHolders.BaseMessageViewHolder<? extends IMessage>>) CustomIncomingTextMessageViewHolder.class,
                        R.layout.item_custom_incoming_text_message,
                        payload)
                .setOutcomingTextConfig(
                        (Class<? extends MessageHolders.BaseMessageViewHolder<? extends IMessage>>) CustomOutcomingTextMessageViewHolder.class,
                        R.layout.item_custom_outcoming_text_message)
                .setIncomingImageConfig(
                        (Class<? extends MessageHolders.BaseMessageViewHolder<? extends MessageContentType.Image>>) CustomIncomingImageMessageViewHolder.class,
                        R.layout.item_custom_incoming_image_message)
                .setOutcomingImageConfig(
                        (Class<? extends MessageHolders.BaseMessageViewHolder<? extends MessageContentType.Image>>) CustomOutcomingImageMessageViewHolder.class,
                        R.layout.item_custom_outcoming_image_message);

        super.messagesAdapter = new MessagesListAdapter<>(super.senderId, holdersConfig, super.imageLoader);
        super.messagesAdapter.setOnMessageLongClickListener(this);
        super.messagesAdapter.setLoadMoreListener(this);
        messagesList.setAdapter(super.messagesAdapter);
    }
}
