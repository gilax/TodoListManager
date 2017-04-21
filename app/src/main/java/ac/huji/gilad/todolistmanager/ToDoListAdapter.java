package ac.huji.gilad.todolistmanager;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ToDoListAdapter extends RecyclerView.Adapter<ToDoListAdapter.ViewHolder> {
    static final String CALL = "Call";
    static final String SEND = "Send";
    static final String REMOVE = "Remove";

    private final static String TAG = "FirebaseDataBase";
    private final static String TODO_LIST = "toDoList";
    // map of <item creation time, DB key>
    private Map<String, String> keys = new HashMap<>();

    private DatabaseReference dbRef;
    private ProgressDialog progress;

    static List<ToDoItem> toDoList;
    private static int numberOfDone = 0;
    private boolean onBind;

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        Context context;
        ConstraintLayout layout;
        TextView title;
        TextView reminder;
        CheckBox done;

        ViewHolder(View view) {
            super(view);
            layout = (ConstraintLayout) view.findViewById(R.id.to_do_inner_layout);
            title = (TextView) view.findViewById(R.id.to_do_text);
            reminder = (TextView) view.findViewById(R.id.reminder_recycle);
            done = (CheckBox) view.findViewById(R.id.item_done);
            context = view.getContext();

            view.setOnCreateContextMenuListener(this);
        }

        void setTextColor(int position) {
            int color;
            if (toDoList.get(position).isDone()) {
                color = Color.GRAY;
            } else {
                color = Color.parseColor("#ffeeeeee");
            }
            title.setTextColor(color);
            reminder.setTextColor(color);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("What would you like to do?");
            if (toDoList.get(getAdapterPosition()).getTitle().toLowerCase().startsWith("call")) {
                menu.add(0, v.getId(), getAdapterPosition(), CALL);
            }
            if (toDoList.get(getAdapterPosition()).getTitle().toLowerCase().startsWith("send")) {
                menu.add(0, v.getId(), getAdapterPosition(), SEND);
            }
            menu.add(0, v.getId(), getAdapterPosition(), REMOVE);
        }
    }

    ToDoListAdapter(List<String> dataSet) {
        super();
        toDoList = new ArrayList<>();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        dbRef = firebaseDatabase.getReference(TODO_LIST);

        for (String title : dataSet) {
            toDoList.add(new ToDoItem(title));
        }
    }

    @Override
    public ToDoListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.to_do_recycler_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (position % 2 == 0) {
            holder.layout.setBackgroundColor(Color.parseColor("#ff33b5e5"));
        } else {
            holder.layout.setBackgroundColor(Color.parseColor("#ff33b55e"));
        }

        holder.title.setText(toDoList.get(position).getTitle());
        holder.reminder.setText(ToDoItem.dateFormat.format(toDoList.get(position).getRemindDate()));
        holder.setTextColor(position);

        onBind = true;
        holder.done.setChecked(toDoList.get(position).isDone());
        onBind = false;

        holder.done.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!onBind) {
                    int listPosition = holder.getAdapterPosition();
                    toDoList.get(listPosition).setDone(isChecked);

                    // update done at DB
                    String key = toDoList.get(listPosition).getCreationTime().toString();
                    dbRef.child(keys.get(key)).child("done").setValue(isChecked);

                    // move the item to its position
                    if (isChecked) {
                        int newPosition = toDoList.size() - 1 - numberOfDone;
                        if (newPosition != listPosition) {
                            ToDoItem temp = toDoList.remove(listPosition);
                            toDoList.add(newPosition, temp);
                            notifyDataSetChanged();
                        } else {
                            holder.setTextColor(listPosition);
                        }
                        numberOfDone++;
                    } else {
                        numberOfDone--;
                        int newPosition = toDoList.size() - 1 - numberOfDone;
                        if (newPosition != listPosition) {
                            ToDoItem temp = toDoList.remove(listPosition);
                            toDoList.add(newPosition, temp);
                            notifyDataSetChanged();
                        } else {
                            holder.setTextColor(listPosition);
                        }
                    }
                }
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return toDoList.size();
    }

    void setDataBaseChildListener(final Context context) {
        dbRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                ToDoItem item = dataSnapshot.getValue(ToDoItem.class);
                toDoList.add(toDoList.size() - numberOfDone, item);
                keys.put(item.getCreationTime().toString(), item.getKey());
                notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                ToDoItem newItem = dataSnapshot.getValue(ToDoItem.class);
                String itemKey = dataSnapshot.getKey();

                if (!newItem.getKey().equals(itemKey)) {
                    newItem.setKey(itemKey);
                }

                if (!toDoList.contains(newItem)) {
                    for (int i = 0; i < toDoList.size(); i++) {
                        if (toDoList.get(i).getKey().equals(itemKey)) {
                            keys.remove(toDoList.get(i).getCreationTime().toString());
                            keys.put(newItem.getCreationTime().toString(), itemKey);
                            toDoList.set(i, newItem);
                            notifyItemChanged(i);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());


            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                Toast.makeText(context, "Failed to load ToDo list from DB.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    ToDoItem get(int position) {
        return toDoList.get(position);
    }

    void add(String str, Date date) {
        ToDoItem item = new ToDoItem(str);
        item.setRemindDate(date);

        String key = dbRef.push().getKey();
        item.setKey(key);
        dbRef.child(key).setValue(item);
    }

    void remove(int position) {
        if (toDoList.get(position).isDone()) {
            numberOfDone--;
        }
        ToDoItem item = toDoList.get(position);
        String key = keys.get(item.getCreationTime().toString());
        dbRef.child(key).removeValue();
        keys.remove(item.getCreationTime().toString());

        toDoList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, toDoList.size());
    }

    void clear() {
        numberOfDone = 0;
        toDoList.clear();
        dbRef.removeValue();

        notifyDataSetChanged();
    }
}
