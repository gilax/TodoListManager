package ac.huji.gilad.todolistmanager;

import android.content.Context;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class ToDoListAdapter extends RecyclerView.Adapter<ToDoListAdapter.ViewHolder> {
    static final String CALL = "Call";
    static final String SEND = "Send";
    static final String REMOVE = "Remove";

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

    ToDoItem get(int position) {
        return toDoList.get(position);
    }

    void add(String str, Date date) {
        ToDoItem item = new ToDoItem(str);
        item.setRemindDate(date);

        toDoList.add(toDoList.size() - numberOfDone, item);
        notifyDataSetChanged();
    }

    void remove(int position) {
        if (toDoList.get(position).isDone()) {
            numberOfDone--;
        }
        toDoList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, toDoList.size());
    }

    void clear() {
        numberOfDone = 0;
        toDoList.clear();
        notifyDataSetChanged();
    }
}
