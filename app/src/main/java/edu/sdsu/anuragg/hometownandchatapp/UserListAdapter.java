package edu.sdsu.anuragg.hometownandchatapp;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by AnuragG on 09-Apr-17.
 */

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListHolder> {

    private ArrayList<UserDataModel> userList;

    class UserListHolder extends RecyclerView.ViewHolder {
        TextView mNickname, mLocation, mYear;

        UserListHolder(View view) {
            super(view);
            mNickname = (TextView) view.findViewById(R.id.usernicknameid);
            mLocation = (TextView) view.findViewById(R.id.user_location);
            mYear = (TextView) view.findViewById(R.id.user_year);
        }
    }

    UserListAdapter(ArrayList<UserDataModel> userList) {
        this.userList = userList;
    }

    @Override
    public UserListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.users_list_row, parent, false);

        return new UserListHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UserListHolder holder, int position) {
        UserDataModel user = userList.get(position);
        holder.mNickname.setText(user.nickname);
        holder.mLocation.setText((user.city!=null?user.city:"")+", "+user.state+", "+user.country);
        holder.mYear.setText(String.valueOf(user.year));

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

}
