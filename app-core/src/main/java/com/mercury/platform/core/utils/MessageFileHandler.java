package com.mercury.platform.core.utils;

import com.mercury.platform.core.utils.interceptor.*;
import com.mercury.platform.shared.ConfigManager;
import com.mercury.platform.shared.HasEventHandlers;
import com.mercury.platform.shared.events.EventRouter;
import com.mercury.platform.shared.events.custom.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class MessageFileHandler implements HasEventHandlers {
    private final Logger logger = Logger.getLogger(MessageFileHandler.class);
    private final String logFilePath = ConfigManager.INSTANCE.getGamePath() + File.separator + "logs" + File.separator + "Client.txt";
    private Date lastMessageDate = new Date();

    private List<MessageInterceptor> interceptors;

    public MessageFileHandler() {
        interceptors = new ArrayList<>();
        interceptors.add(new IncTradeMessagesInterceptor());
        interceptors.add(new OutTradeMessagesInterceptor());
        interceptors.add(new PlayerJoinInterceptor());
        interceptors.add(new PlayerLeftInterceptor());
    }

    public void parse(){
        List<String> stubMessages = new ArrayList<>();
        File logFile = new File(logFilePath);
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(logFile,"r");
            int lines = 0;
            StringBuilder builder = new StringBuilder();
            long length = logFile.length();
            length--;
            randomAccessFile.seek(length);
            for(long seek = length; seek >= 0; --seek){
                randomAccessFile.seek(seek);
                char c = (char)randomAccessFile.read();
                builder.append(c);
                if(c == '\n'){
                    builder = builder.reverse();
                    String stroke = builder.toString();
                    String utf8 = new String(stroke.getBytes("ISO-8859-1"),"UTF-8");
                    stubMessages.add(utf8);
                    lines++;
                    builder = new StringBuilder();
                    if (lines == 30){
                        break;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error in MessageFileHandler: ", e);
        }
        List<String> filteredMessages = stubMessages.stream().filter(message -> {
            if(message != null && !message.equals("\n")) {
                Date msgDate = new Date(StringUtils.substring(message, 0, 20));
                return msgDate.after(lastMessageDate);
            }
            return false;

        }).collect(Collectors.toList());
        if(filteredMessages.size() != 0) {
            lastMessageDate = new Date(StringUtils.substring(filteredMessages.get(0), 0, 20));
        }

        interceptors.forEach(interceptor -> {
            filteredMessages.forEach(interceptor::match);
        });
    }
    public void addInterceptor(MessageInterceptor interceptor){
        interceptors.add(interceptor);
    }

    @Override
    public void initHandlers() {
        EventRouter.registerHandler(AddInterceptorEvent.class, event ->
                addInterceptor(((AddInterceptorEvent) event).getInterceptor()));
    }
}