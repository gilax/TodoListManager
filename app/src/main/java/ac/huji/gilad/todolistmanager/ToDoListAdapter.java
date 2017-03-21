package ac.huji.gilad.todolistmanager;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static ac.huji.gilad.todolistmanager.ToDoListManager.POSITION_TO_REMOVE;
import static ac.huji.gilad.todolistmanager.ToDoListManager.TEXT_TO_REMOVE;

class ToDoListAdapter extends RecyclerView.Adapter<ToDoListAdapter.ViewHolder> {
    private static List<ToDoItem> toDoList;
    private static int numberOfDone = 0;
    private boolean onBind;

    static class ViewHolder extends RecyclerView.ViewHolder {
        Context context;
        ConstraintLayout layout;
        TextView textView;
        CheckBox done;

        ViewHolder(View view){
            super(view);
            view.setLongClickable(true);
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    FragmentManager fragmentManager = ((FragmentActivity)context).getSupportFragmentManager();
                    RemoveDialog removeDialog = new RemoveDialog();
                    Bundle args = new Bundle();
                    args.putInt(POSITION_TO_REMOVE, position);
                    args.putString(TEXT_TO_REMOVE, toDoList.get(position).getTitle());
                    removeDialog.setArguments(args);
                    removeDialog.show(fragmentManager, "remove_dialog");
                    return true;
                }
            });
            layout = (ConstraintLayout) view.findViewById(R.id.to_do_inner_layout);
            textView = (TextView) view.findViewById(R.id.to_do_text);
            done = (CheckBox) view.findViewById(R.id.checkBox);
            context = view.getContext();
        }

        public void setTextColor(int position) {
            if (toDoList.get(position).isDone()) {
                textView.setTextColor(Color.GRAY);
            } else {
                textView.setTextColor(Color.parseColor("#ffeeeeee"));
            }
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

        ViewHolder vh = new ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (position % 2 == 0) {
            holder.layout.setBackgroundColor(Color.parseColor("#ff33b5e5"));
        } else {
            holder.layout.setBackgroundColor(Color.parseColor("#ff33b55e"));
        }

        holder.textView.setText(toDoList.get(position).getTitle());
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

    void add(String str) {
        toDoList.add(toDoList.size() - numberOfDone, new ToDoItem(str));
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
