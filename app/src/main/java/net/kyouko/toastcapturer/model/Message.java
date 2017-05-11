package net.kyouko.toastcapturer.model;

public class Message {

    public String type;
    public String appName;
    public String time;
    public String content;

    public Message(String type, String appName, String time, String content) {
        this.type = type;
        this.appName = appName;
        this.time = time;
        this.content = content;
    }

}
