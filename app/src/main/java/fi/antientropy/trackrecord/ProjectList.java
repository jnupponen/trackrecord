package fi.antientropy.trackrecord;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import fi.antientropy.trackrecord.domain.Project;
import fi.antientropy.trackrecord.persistence.Datasource;

public class ProjectList extends ArrayAdapter<Project> {

    private static final String DELETE_PROJECT_TEXT = "Delete";
    private static final String DELETE_PROJECT_OK_BUTTON_TEXT = "Ok";
    private static final String DELETE_PROJECT_CANCEL_BUTTON_TEXT = "Cancel";

    private final List<Project> list;
    private final Activity context;
    private final Datasource datasource;
    private Project activeElement;

    public ProjectList(Activity context, List<Project> list, Datasource datasource) {
        super(context, R.layout.row_button_layout, list);
        this.context = context;
        this.list = list;
        this.datasource = datasource;
        this.activeElement = getActiveElement();
    }

    static class ViewHolder {
        TextView text;
        TextView time;
        ImageView image;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.row_button_layout, null);
            final ViewHolder viewHolder = new ViewHolder();

            // Asetetaan nimi.
            viewHolder.text = (TextView) view.findViewById(R.id.label);
            viewHolder.text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Project element = (Project) viewHolder.text.getTag();
                    final Project elementToUpdate = (Project) viewHolder.text.getTag();
                    final String elementName = element.getName();

                    AlertDialog.Builder alert = new AlertDialog.Builder(context);

                    alert.setTitle(MainActivity.WRITE_TIMER_NAME_PROMPT);

                    // Set an EditText view to get user input
                    final EditText input = new EditText(context);
                    input.setText(elementName);
                    alert.setView(input);

                    alert.setPositiveButton(MainActivity.ADD_BUTTON_TEXT, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String value = input.getText().toString();

                            if (!elementName.contentEquals(value) && !value.trim().isEmpty()) {
                                Project update = new Project(elementToUpdate.getId(), value,
                                        elementToUpdate.getStart(), 0, elementToUpdate.getDuration());
                                update.setActive(elementToUpdate.isActive());

                                Project addedModel = datasource.update(update);
                                list.set(getPosition(elementToUpdate), addedModel);
                                notifyDataSetChanged();
                            }
                        }
                    });

                    alert.setNegativeButton(MainActivity.CANCEL_BUTTON_TEXT, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Canceled.
                        }
                    });

                    final AlertDialog dialog = alert.create();

                    input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (hasFocus) {
                                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                            }
                        }
                    });
                    dialog.show();

                    element.setName(elementName);
                }
            });
            view.setTag(viewHolder);
            viewHolder.text.setTag(list.get(position));

            // Set image.
            viewHolder.image = (ImageView) view.findViewById(R.id.icon);
            viewHolder.image.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Project element = (Project) viewHolder.image.getTag();

                    if(element.isActive()) {
                        element.stopTimer();
                        datasource.update(element);
                        activeElement = null;
                    }
                    else {
                        if(activeElement != null) {
                            activeElement.stopTimer();
                            datasource.update(activeElement);
                            activeElement = null;

                         }

                        element.startTimer();
                        datasource.update(element);
                        activeElement = element;

                    }

                    notifyDataSetChanged();
                    clear();
                    addAll(datasource.getProjects());

                    return false;
                }
            });

            view.setTag(viewHolder);
            viewHolder.image.setTag(list.get(position));


            // Set time.
            viewHolder.time = (TextView) view.findViewById(R.id.time);
            // Delete project.
            viewHolder.time.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Project element = (Project) viewHolder.time.getTag();
                    deleteProject(element);

                    return false;
                }
            });

            view.setTag(viewHolder);
            viewHolder.time.setTag(list.get(position));


        } else {
            view = convertView;
            ((ViewHolder) view.getTag()).text.setTag(list.get(position));
            ((ViewHolder) view.getTag()).time.setTag(list.get(position));
            ((ViewHolder) view.getTag()).image.setTag(list.get(position));
        }

        // Set values on display.
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.text.setText(list.get(position).getName());
        holder.time.setText(list.get(position).getPrintDuration());
        if(list.get(position).isActive()) {
            holder.image.setImageResource(R.drawable.timer_active);
        }
        else {
            holder.image.setImageResource(R.drawable.timer_not_active);
        }

        return view;
    }

    private void deleteProject(final Project project) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setTitle(DELETE_PROJECT_TEXT);
        alert.setMessage(project.getName());

        alert.setPositiveButton(DELETE_PROJECT_OK_BUTTON_TEXT, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                datasource.delete(project);
                remove(project);

                notifyDataSetChanged();
            }
        });

        alert.setNegativeButton(DELETE_PROJECT_CANCEL_BUTTON_TEXT, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }
    private Project getActiveElement() {
        return datasource.getActiveProject();
    }

}

