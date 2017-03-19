package ac.huji.gilad.todolistmanager;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

class ToDoListAdapter extends RecyclerView.Adapter<ToDoListAdapter.ViewHolder> {
    List<String> toDoList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout layout;
        TextView textView;

        ViewHolder(View view){
            super(view);
            layout = (RelativeLayout) view.findViewById(R.id.to_do_inner_layout);
            textView = (TextView) view.findViewById(R.id.to_do_text);
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
            holder.layout.setBackgroundColor(Color.RED);
        } else {
            holder.layout.setBackgroundColor(Color.BLUE);
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
