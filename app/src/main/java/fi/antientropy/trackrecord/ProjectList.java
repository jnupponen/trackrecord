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
        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.row_button_layout, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) view.findViewById(R.id.label);
            viewHolder.image = (ImageView) view.findViewById(R.id.icon);
            viewHolder.time = (TextView) view.findViewById(R.id.time);

            viewHolder.text.setOnClickListener(new ChangeNameByClickingName(viewHolder, context, datasource, this, list));
            view.setTag(viewHolder);
            viewHolder.text.setTag(list.get(position));

            viewHolder.image.setOnTouchListener(new ToggleTimerByTouchingIcon(viewHolder, datasource, this));
            view.setTag(viewHolder);
            viewHolder.image.setTag(list.get(position));

            viewHolder.time.setOnTouchListener(new DeleteProjectByTouchingTime(viewHolder, datasource, this,context));
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
}

