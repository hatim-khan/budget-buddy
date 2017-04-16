package area51.budgetbuddy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paige on 4/16/17.
 */

public class Group {

    public ArrayList<User> groupMembers;
    public String name;

    // temporary initializer for a group
    // we'll need to do some database stuff here in the future
    public Group(String groupName) {
        this.name = groupName;
        this.groupMembers = new ArrayList<>();
    }

    public void addUserToGroup(User newUser) {
        groupMembers.add(newUser);
    }
}
