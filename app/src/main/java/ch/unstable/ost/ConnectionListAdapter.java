package ch.unstable.ost;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import ch.unstable.ost.api.transport.model.Connection;
import ch.unstable.ost.api.transport.model.Journey;
import ch.unstable.ost.api.transport.model.Section;
import ch.unstable.ost.utils.TimeDateUtils;
import ch.unstable.ost.views.ConnectionLineView;


public class ConnectionListAdapter extends RecyclerView.Adapter<ConnectionListAdapter.ViewHolder> {
    private final List<Connection> mConnections = new ArrayList<>();
    public static final String TAG = "ConnectionListAdapter";

    private final View.OnClickListener mOnViewHolderClickListener = new OnViewHolderClickListener();
    private OnConnectionClickListener mOnConnectionClickListener;

    public void setConnections(@NonNull Collection<Connection> Connections) {
        mConnections.clear();
        mConnections.addAll(Connections);
        notifyDataSetChanged();
    }

    public void setConnections(@NonNull Connection... Connections) {
        mConnections.clear();
        Collections.addAll(mConnections, Connections);
        notifyDataSetChanged();
    }

    public void appendConnections(@NonNull Collection<Connection> Connections) {
        int size = mConnections.size();
        mConnections.addAll(Connections);
        notifyItemRangeInserted(size, Connections.size());
    }

    public void prependConnections(@NonNull Collection<Connection> Connections) {
        mConnections.addAll(0, Connections);
        notifyItemRangeInserted(0, Connections.size());
    }

    @Override
    public ConnectionListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_connection, parent, false);
        return new ViewHolder(view);
    }


    private static int[] getTravelTimes(Section[] sections) {
        int[] times = new int[sections.length * 2 - 1];
        int i = 0;
        long lastEnd = 0;
        for(Section section: sections) {
            // Walks can be ignored. They are added to the waiting time.
            if(section.isJourney()) {
                if(lastEnd != 0) {
                    // Waiting time
                    times[i] = (int) (section.getDeparture().getDepartureTime().getTime() - lastEnd);
                    ++i;
                }
                // Travel time
                lastEnd = section.getArrival().getArrival().getTime();
                times[i] = (int) (lastEnd - section.getDeparture().getDepartureTime().getTime());
                ++i;
            }
        }
        if(i != times.length) {
            times = Arrays.copyOf(times, i);
        }
        return times;
    }

    @Override
    public void onBindViewHolder(ConnectionListAdapter.ViewHolder holder, int position) {
        Connection connection = mConnections.get(position);
        Section[] sections = connection.getSections();
        if (sections.length > 0) {
            Section section = sections[0];
            if(section.isWalk()) {
                section = sections[1];
            }
            if(section.isJourney()) {
                Journey journey = section.getJourney();
                holder.firstEndDestination.setText(journey.getTo());
                holder.firstTransportName.setText(journey.getName());
            }

        } else {
            Log.e(TAG, "No sections");
        }


        holder.startTime.setText(TimeDateUtils.formatTime(connection.getFrom().getDepartureTime()));
        holder.endTime.setText(TimeDateUtils.formatTime(connection.getTo().getArrival()));

        int times[] = getTravelTimes(connection.getSections());
        holder.connectionLineView.setLengths(times);

        holder.itemView.setTag(holder);
        holder.itemView.setOnClickListener(mOnViewHolderClickListener);
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.itemView.setTag(null);
        holder.itemView.setOnClickListener(null);
    }

    @Override
    public int getItemCount() {
        return mConnections.size();
    }

    public void clearConnections() {
        mConnections.clear();
        notifyDataSetChanged();
    }

    @MainThread
    public void setOnConnectionClickListener(@Nullable OnConnectionClickListener onConnectionClickListener) {
        this.mOnConnectionClickListener = onConnectionClickListener;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView startTime;
        private final TextView endTime;
        private final TextView firstEndDestination;
        private final ConnectionLineView connectionLineView;
        private final TextView firstTransportName;

        public ViewHolder(View itemView) {
            super(itemView);
            startTime = (TextView) itemView.findViewById(R.id.startTime);
            endTime = (TextView) itemView.findViewById(R.id.endTime);
            firstEndDestination = (TextView) itemView.findViewById(R.id.firstSectionEndDestination);
            connectionLineView = (ConnectionLineView) itemView.findViewById(R.id.connectionLineView);
            firstTransportName = (TextView) itemView.findViewById(R.id.firstTransportName);
        }
    }

    public interface OnConnectionClickListener {
        void onConnectionClicked(Connection connection);
    }

    public class OnViewHolderClickListener implements View.OnClickListener {
        public OnViewHolderClickListener() {
        }

        @Override
        public void onClick(View v) {
            ViewHolder viewHolder = (ViewHolder) v.getTag();
            Connection connection;
            try {
                connection = mConnections.get(viewHolder.getAdapterPosition());
            } catch (RuntimeException e) {
                Log.e(TAG, "Failed to get connection", e);
                return;
            }
            if(mOnConnectionClickListener != null) {
                mOnConnectionClickListener.onConnectionClicked(connection);
            }
        }
    }
}
