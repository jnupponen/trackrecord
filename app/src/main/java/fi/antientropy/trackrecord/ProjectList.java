package fi.antientropy.trackrecord;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import fi.antientropy.trackrecord.domain.Project;
import fi.antientropy.trackrecord.persistence.Datasource;

public class ProjectList extends ArrayAdapter<Project> {

    private final List<Project> list;
    private final Activity context;
    private final Datasource datasource;

    public ProjectList(Activity context, List<Project> list, Datasource datasource) {
        super(context, R.layout.row_button_layout, list);
        this.context = context;
        this.list = list;
        this.datasource = datasource;
    }

    static class ViewHolder {
        TextView text;
        TextView time;
        ImageView image;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        final Project project = list.get(position);

        if (convertView == null || convertView.getTag() == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.row_button_layout, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) view.findViewById(R.id.label);
            viewHolder.text.setTag(project);

            viewHolder.image = (ImageView) view.findViewById(R.id.icon);
            viewHolder.image.setTag(project);

            viewHolder.time = (TextView) view.findViewById(R.id.time);
            viewHolder.time.setTag(project);

            viewHolder.text.setOnClickListener(new ChangeNameByClickingName(context, datasource, this, list));
            viewHolder.image.setOnTouchListener(new ToggleTimerByTouchingIcon(datasource, this));
            viewHolder.time.setOnTouchListener(new DeleteProjectByTouchingTime(context, datasource, this));

            view.setTag(viewHolder);

        } else {
            view = convertView;
        }

        // Set values on display.
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.text.setText(project.getName());
        viewHolder.time.setText(project.getPrintDuration());
        viewHolder.image.setImageResource(project.getTimerImage());

        return view;
    }
}

