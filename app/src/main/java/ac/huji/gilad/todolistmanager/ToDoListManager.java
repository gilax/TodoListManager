package ac.huji.gilad.todolistmanager;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class ToDoListManager extends AppCompatActivity {
    static final String POSITION_TO_REMOVE = "position";
    static final String TEXT_TO_REMOVE = "todo";


    List<String> toDoListItems = new ArrayList<>();
    ToDoListAdapter adapter;
    RecyclerView toDoList;
    LinearLayoutManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list_manager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
            }
        });

        adapter = new ToDoListAdapter(toDoListItems);
        toDoList = (RecyclerView) findViewById(R.id.recycle_to_do_list);
        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        toDoList.setLayoutManager(manager);
        toDoList.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_to_do_list_manager, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            addItem();
            return true;
        } else if (id == R.id.action_remove_all) {
            removeAllItems();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void removeAllItems() {
        adapter.clear();
    }

    private void addItem() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        AddDialog addDialog = new AddDialog();
        addDialog.show(fragmentManager, "add_dialog");
    }
}
