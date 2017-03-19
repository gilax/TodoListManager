package ac.huji.gilad.todolistmanager;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import static ac.huji.gilad.todolistmanager.ToDoListManager.POSITION_TO_REMOVE;
import static ac.huji.gilad.todolistmanager.ToDoListManager.TEXT_TO_REMOVE;

class ToDoListAdapter extends RecyclerView.Adapter<ToDoListAdapter.ViewHolder> {
    static List<String> toDoList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        Context context;
        RelativeLayout layout;
        TextView textView;

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
                    args.putString(TEXT_TO_REMOVE, toDoList.get(position));
                    removeDialog.setArguments(args);
                    removeDialog.show(fragmentManager, "remove_dialog");
                    return true;
                }
            });
            layout = (RelativeLayout) view.findViewById(R.id.to_do_inner_layout);
            textView = (TextView) view.findViewById(R.id.to_do_text);
            context = view.getContext();
        }
    }

    ToDoListAdapter(List<String> dataSet) {
        super();
        toDoList = dataSet;
    }

    @Override
    public ToDoListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.to_do_recycler_layout, parent, false);

        ViewHolder vh = new ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (position % 2 == 0) {
            holder.layout.setBackgroundColor(Color.parseColor("#ff33b5e5"));
        } else {
            holder.layout.setBackgroundColor(Color.parseColor("#ff33b55e"));
        }

        holder.textView.setText(toDoList.get(position));
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
        toDoList.add(str);
        notifyDataSetChanged();
    }

    void remove(int position) {
        toDoList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, toDoList.size());
    }
}
