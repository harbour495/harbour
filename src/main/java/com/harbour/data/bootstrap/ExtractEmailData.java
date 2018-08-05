package com.harbour.data.bootstrap;

import com.harbour.data.dao.DataDao;
import com.harbour.data.model.Data;
import com.harbour.data.telegram.TelegramSender;
import com.sun.mail.imap.IMAPFolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;
import java.util.Properties;

@Component
public class ExtractEmailData implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    DataDao dataDao;
    @Autowired
    TelegramSender telegramSender;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        {
            try {
                Properties properties = new Properties();
                properties.setProperty("mail.host", "imap.gmail.com");
                properties.setProperty("mail.port", "993");
                properties.setProperty("mail.store.protocol", "imaps");

                Session session = Session.getDefaultInstance(new Properties());
                Store store = session.getStore("imaps");
                store.connect("imap.googlemail.com", 993, "paramintrigue@gmail.com", "Ammaamma8@");
                Folder folder = store.getFolder("INBOX");
                //folder.open( Folder.READ_ONLY );
                folder.open(Folder.READ_WRITE);
                folder.addMessageCountListener(new MessageCountAdapter() {
                    public void messagesAdded(MessageCountEvent ev) {
                        Message[] msgs = ev.getMessages();
                        // System.out.println("Got " + msgs.length + " new messages");

                        // Just dump out the new messages
                        for (Message message : msgs) {
                            try {
                                Data data = new Data();
                                String subject = message.getSubject();
                                data.setSubject(subject);
                                dataDao.insertData(data);

                                //Sending telegram message
                                telegramSender.sendMessage("Hello!! The subject (" + subject + ") extracted and has been added to db");
                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                        }
                    }
                });

                // Check mail once in "freq" MILLIseconds
                int freq = Integer.parseInt("15");
                boolean supportsIdle = false;
                try {
                    if (folder instanceof IMAPFolder) {
                        IMAPFolder f = (IMAPFolder) folder;
                        f.idle();
                        supportsIdle = true;
                    }
                } catch (FolderClosedException fex) {
                    throw fex;
                } catch (MessagingException mex) {
                    supportsIdle = false;
                }
                for (; ; ) {
                    if (supportsIdle && folder instanceof IMAPFolder) {
                        IMAPFolder f = (IMAPFolder) folder;
                        f.idle();
                        System.out.println("IDLE done");
                    } else {
                        Thread.sleep(freq); // sleep for freq milliseconds

                        // This is to force the IMAP server to send us
                        // EXISTS notifications.
                        folder.getMessageCount();
                    }
                }

            } catch (Exception exception) {
                exception.printStackTrace();
            }


        }
    }
}
