package de.uniks.stpmon.k.service.dummies;

import de.uniks.stpmon.k.dto.CreateGroupDto;
import de.uniks.stpmon.k.dto.UpdateGroupDto;
import de.uniks.stpmon.k.models.Group;
import de.uniks.stpmon.k.rest.GroupApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Singleton
public class GroupApiDummy implements GroupApiService {


    // all are created at:2023-01-01T00:00:00.000Z
    // all are updated at: 2023-02-02T00:00:00.000Z
    ArrayList<Group> groups = new ArrayList<>();

    @Inject
    public GroupApiDummy() {
    }

    public void initDummyGroups() {
        if (groups.isEmpty()) {
            ArrayList<Group> dummyGroups = getDummyGroups();
            groups = new ArrayList<>(dummyGroups);
        }
    }

    /**
     * Returns dummy groups:
     * all are created at:2023-01-01T00:00:00.000Z
     * all are updated at: 2023-02-02T00:00:00.000Z
     * Names: TestGroup0, TestGroup1...
     * ids: id0, id1,..
     * members: all have 2 members starting by ["id0","id1"], then ["id1","id2"]...
     */
    private ArrayList<Group> getDummyGroups() {
        ArrayList<Group> groups = new ArrayList<>();
        int amount = 3;
        for (int i = 0; i < amount; i++) {
            String[] memberIds = {"id" + i, "id" + (i + 1)};
            ArrayList<String> members = new ArrayList<>(Arrays.asList(memberIds));
            Group group = new Group(
                    "2023-01-01T00:00:00.000Z",
                    "2023-02-02T00:00:00.000Z",
                    "id" + i,
                    "TestGroup" + i,
                    members);
            groups.add(group);
        }
        return groups;
    }

    /**
     * Creates a group and adds it to groups
     */
    @Override
    public Observable<Group> createGroup(CreateGroupDto group) {
        //create new group
        String id = String.valueOf(groups.size());
        Group newGroup = new Group(
                "2023-01-01T00:00:00.000Z",
                "2023-02-02T00:00:00.000Z",
                id,
                group.name(),
                group.members()
        );
        groups.add(newGroup);
        return Observable.just(newGroup);
    }

    /**
     * If no groups are added before, 3 dummyGroups are returned
     */
    @Override
    public Observable<ArrayList<Group>> getGroups() {
        initDummyGroups();
        return Observable.just(groups);
    }


    /**
     * Returns the all groups which contain all given member
     * If there are no groups, dummyGroups are added
     *
     * @param members: ArrayList of id's (look above for better understanding)
     */
    @Override
    public Observable<ArrayList<Group>> getGroups(String members) {
        ArrayList<String> membersList = new ArrayList<>(Arrays.asList(members.split(",")));

        List<Group> returnGroups = groups
                .stream()
                .filter(g -> g.members().containsAll(membersList))
                .toList();

        return Observable.just(new ArrayList<>(returnGroups));
    }

    /**
     * Edit the group with the given id
     *
     * @param id:    id of group to edit
     * @param group: UpdateGroupDto (method can handle null values)
     */
    @Override
    public Observable<Group> editGroup(String id, UpdateGroupDto group) {
        Group groupToDelete;
        Group newGroup;

        for (Group g : groups) {
            if (g._id().equals(id)) {
                groupToDelete = g;

                String newName = group.name();
                if (newName == null) {
                    newName = groupToDelete.name();
                }

                List<String> newMembers = group.members();
                if (newMembers == null) {
                    newMembers = groupToDelete.members();
                }

                newGroup = new Group(
                        "2023-01-01T00:00:00.000Z",
                        "2023-02-02T00:00:00.000Z",
                        id,
                        newName,
                        newMembers
                );
                groups.remove(groupToDelete);
                groups.add(newGroup);
                return Observable.just(newGroup);
            }
        }

        //returns second group of dummy groups
        return Observable.error(new Throwable("404 Not Found"));
    }

    /**
     * Delete a group; if you don't want to add groups manually before, use initDummyGroups()
     */
    @Override
    public Observable<Group> deleteGroup(String id) {
        for (Group group : groups) {
            if (group._id().equals(id)) {
                groups.remove(group);
                return Observable.just(group);
            }
        }
        //returns second group of dummy groups
        return Observable.error(new Throwable("404 Not Found"));
    }
}
