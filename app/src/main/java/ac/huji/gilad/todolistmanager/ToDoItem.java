package ac.huji.gilad.todolistmanager;


public class ToDoItem {
    private String title;
    private boolean done;

    ToDoItem(String title) {
        this.title = title;
        this.done = false;
    }

    public String getTitle() {
        return title;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
