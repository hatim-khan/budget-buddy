package area51.budgetbuddy;

import android.support.v7.widget.RecyclerView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by natalieshum on 4/30/17.
 */

public class GroupMembersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<User> groupMembers = new ArrayList<>();
    private Context groupContext;
    private static final int MEMBERS = 0;

    //constructor to get contexts and arraylist passed in
    public GroupMembersAdapter(Context context, ArrayList<User> group) {
        groupContext = context;
        groupMembers = group;
    }

    private Context getContext() {return groupContext;}

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == MEMBERS) {
            LayoutInflater inflater = LayoutInflater.from(groupContext);
            View membersView = inflater.inflate(R.layout.activity_edit_group_row_content, parent, false);
            return new MemberViewHolder(membersView);
        }
        throw new RuntimeException("there is no type that matches" + viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof MemberViewHolder) {
            User user = groupMembers.get(position);
            MemberViewHolder memViewHolder = (MemberViewHolder) viewHolder;
            TextView userName = memViewHolder.userNameView;
            userName.setText(user.getUsername());
            ImageButton button = memViewHolder.clearButtonView;
        }
    }

    @Override
    public int getItemCount() {
        return groupMembers.size();
    }

    @Override
    public int getItemViewType(int position) {
        return MEMBERS;
    }

    //viewholder for group member content
    public static class MemberViewHolder extends RecyclerView.ViewHolder {
        TextView userNameView;
        ImageButton clearButtonView;

        public MemberViewHolder(View itemView) {
            super(itemView);

            userNameView = (TextView) itemView.findViewById(R.id.username);
            clearButtonView = (ImageButton) itemView.findViewById(R.id.x_button);
        }
    }









}
