package com.nbusy.app.worker;

import com.google.common.base.Optional;
import com.nbusy.app.data.Chat;
import com.nbusy.app.data.DB;
import com.nbusy.app.data.DataMap;
import com.nbusy.app.data.Message;
import com.nbusy.app.data.UserProfile;
import com.nbusy.app.data.callbacks.GetChatMessagesCallback;
import com.nbusy.app.data.callbacks.UpsertMessagesCallback;
import com.nbusy.app.data.composite.ChatAndMessages;
import com.nbusy.app.data.composite.ChatsAndMessages;
import com.nbusy.app.worker.eventbus.ChatsUpdatedEvent;
import com.nbusy.app.worker.eventbus.EventBus;
import com.nbusy.sdk.Client;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import titan.client.callbacks.SendMsgsCallback;
import titan.client.messages.MsgMessage;

/**
 * Manages persistent connection to NBusy servers and the persistent queue for relevant operations.
 * All notifications from this class is sent out using an event bus.
 */
public class Worker {

    private final Client client;
    private final EventBus eventBus;
    private final DB db;
    private final UserProfile userProfile;

    public Worker(final Client client, final EventBus eventBus, DB db, UserProfile userProfile) {
        if (client == null) {
            throw new IllegalArgumentException("client cannot be null");
        }
        if (eventBus == null) {
            throw new IllegalArgumentException("eventBus cannot be null");
        }
        if (db == null) {
            throw new IllegalArgumentException("db cannot be null ");
        }
        if (userProfile == null) {
            throw new IllegalArgumentException("userProfile cannot be null ");
        }

        this.client = client;
        this.eventBus = eventBus;
        this.db = db;
        this.userProfile = userProfile;
    }

    /************************
     * Server Communication *
     ************************/

    private void receiveMessages(MsgMessage... msgs) {
        if (msgs == null || msgs.length == 0) {
            throw new IllegalArgumentException("messages cannot be null or empty");
        }

        final Message[] nbusyMsgs = DataMap.titanToNBusyMessages(msgs);
        final Set<Chat> chats = userProfile.upsertMessages(nbusyMsgs);
        db.upsertMessages(new UpsertMessagesCallback() {
            @Override
            public void success() {
                for (Chat chat : chats) {
                    eventBus.post(new ChatsUpdatedEvent(chat));
                }
            }

            @Override
            public void error() {
            }
        }, nbusyMsgs);
    }

    public void sendMessages(String chatId, String... msgs) {
        Optional<ChatAndMessages> cmOpt = userProfile.addNewOutgoingMessages(chatId, msgs);
        if (!cmOpt.isPresent()) {
            return;
        }

        sendMessages(cmOpt.get().messages);
    }

    public void sendMessages(Set<Message> msgs) {
        sendMessages(msgs.toArray(new Message[msgs.size()]));
    }

    public void sendMessages(List<Message> msgs) {
        sendMessages(msgs.toArray(new Message[msgs.size()]));
    }

    public void sendMessages(final Message... msgs) {
        if (msgs == null || msgs.length == 0) {
            throw new IllegalArgumentException("messages cannot be null or empty");
        }

        // update in memory user profile with messages in case any of them are new, and notify all listener about this state change
        Set<Chat> chats = userProfile.upsertMessages(msgs);
        eventBus.post(new ChatsUpdatedEvent(chats));

        // persist messages in the database with Status = NEW
        db.upsertMessages(new UpsertMessagesCallback() {
            @Override
            public void success() {
                client.sendMessages(new SendMsgsCallback() {
                    @Override
                    public void sentToServer() {
                        // update in memory representation of messages
                        final ChatsAndMessages chatsAndMsgs = userProfile.setMessageStatuses(Message.Status.SENT_TO_SERVER, msgs);

                        // now the sent messages are ACKed by the server, update them with Status = SENT_TO_SERVER
                        db.upsertMessages(new UpsertMessagesCallback() {
                            @Override
                            public void success() {
                                // finally, notify all listening views about the changes
                                eventBus.post(new ChatsUpdatedEvent(chatsAndMsgs.chats));
                            }

                            @Override
                            public void error() {
                            }
                        }, new ArrayList<>(chatsAndMsgs.messages));
                    }
                }, DataMap.nbusyToTitanMessages(msgs));
            }

            @Override
            public void error() {
            }
        }, msgs);
    }

    /***********************
     * Database Operations *
     ***********************/

    /**
     * Retrieve messages from database, update in memory representation, notify views about the new data.
     */
    public void getChatMessages(final String chatId) {
        if (chatId == null || chatId.isEmpty()) {
            throw new IllegalArgumentException("chatId cannot be null or empty");
        }

        db.getChatMessages(chatId, new GetChatMessagesCallback() {
            @Override
            public void chatMessagesRetrieved(List<Message> msgs) {
                if (msgs.size() != 0) {
                    msgs = DataMap.dbToNBusyMessages(userProfile, msgs);
                    eventBus.post(new ChatsUpdatedEvent(userProfile.upsertMessages(msgs)));
                }
            }
        });
    }
}
