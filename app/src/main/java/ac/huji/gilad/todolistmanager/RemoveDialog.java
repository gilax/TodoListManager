package ac.huji.gilad.todolistmanager;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class RemoveDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Remove from TODO list");
        builder.setMessage("Do you want to remove \"" + getArguments().getString(ToDoListManager.TEXT_TO_REMOVE)
                + "\" from the TODO list?");

        builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ToDoListManager manager = (ToDoListManager) getActivity();
                int position = getArguments().getInt(ToDoListManager.POSITION_TO_REMOVE);
                manager.adapter.remove(position);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        return builder.create();
    }
}
