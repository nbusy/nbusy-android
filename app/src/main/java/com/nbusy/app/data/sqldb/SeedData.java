package com.nbusy.app.data.sqldb;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.nbusy.app.data.Chat;
import com.nbusy.app.data.Message;
import com.nbusy.app.data.UserProfile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class SeedData {
    public static final UserProfile profile = new UserProfile(
            "id-s1234",
            "token-12jg4ec",
            "mail-chuck@nbusy.com",
            "name-chuck norris",
            new byte[]{0, 2, 3},
            new ArrayList<Chat>());

    public static final Chat chat1 = new Chat(
            "id-chat-s1234",
            "Phil Norris",
            "my last message to Phil",
            new Date(),
            ImmutableSet.<Message>of());

    public static final List<Chat> chats = ImmutableList.of(chat1);

    public static final Chat[] chatsArray = chats.toArray(new Chat[chats.size()]);
}
