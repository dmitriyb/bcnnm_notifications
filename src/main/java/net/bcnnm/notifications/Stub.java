package net.bcnnm.notifications;

import org.springframework.data.annotation.Id;

public class Stub {
    @Id
    public String id;

    private String name;
    private String content;

    public Stub(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public Stub(){

    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Stub{" +
                "name='" + name + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
