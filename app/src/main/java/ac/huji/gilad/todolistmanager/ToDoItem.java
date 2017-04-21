package ac.huji.gilad.todolistmanager;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class ToDoItem implements Comparable<ToDoItem>{
    static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());

    private String title;
    private Date remindDate;
    private Date creationTime;
    private boolean done;
    private String key;

    ToDoItem() {}

    ToDoItem(String title) {
        this.title = title;
        this.done = false;
        this.creationTime = new Date();
    }

    String getTitle() {
        return title;
    }

    boolean isDone() {
        return done;
    }

    void setDone(boolean done) {
        this.done = done;
    }

    void setRemindDate(Date remindDate){
        this.remindDate = remindDate;
    }

    Date getRemindDate() {
        return remindDate;
    }

    @Override
    public int compareTo(ToDoItem o) {
        if (o == null) {
            throw new NullPointerException();
        }
        return Long.signum(this.creationTime.getTime() - o.creationTime.getTime());
    }

    Date getCreationTime() {
        return creationTime;
    }

    void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
