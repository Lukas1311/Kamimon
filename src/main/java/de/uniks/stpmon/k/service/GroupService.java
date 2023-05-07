package de.uniks.stpmon.k.service;

import java.util.ArrayList;
import java.util.stream.Collectors;

import javax.inject.Inject;

import de.uniks.stpmon.k.dto.Group;
import de.uniks.stpmon.k.dto.CreateGroupDto;
import de.uniks.stpmon.k.dto.UpdateGroupDto;
import de.uniks.stpmon.k.rest.GroupApiService;
import io.reactivex.rxjava3.core.Observable;

public class GroupService {

    private final GroupApiService groupApiService;

    @Inject
    public GroupService(GroupApiService groupApiService) {
        this.groupApiService = groupApiService;
    }

    // creates a new group: caller has to provide a list of members and a name for the group
    public Observable<Group> createGroup(String name, ArrayList<String> members) {
        return groupApiService.createGroup(
            new CreateGroupDto(name, members)
        );
    }

    /** 
     * returns all groups where the current user is member of
     */
    public Observable<ArrayList<Group>> getOwnGroups() {
        return groupApiService.getGroups();
    }

    /**
     * returns one or more groups that have all the users given in the parameter inside
     * 
     * @param members is an ArrayList of user id's
     * @return group/s with the exact members list
     */
    public Observable<ArrayList<Group>> getGroupsByMembers(ArrayList<String> members) {
        return groupApiService.getGroups(members);
    }

    /**
     * searches for groups that contain the given name
     * 
     * @param name is part of the search query that is used to search for group names
     * @return all groups that contain the given name
    */
    public Observable<Group> searchGroup(String name) {
        return groupApiService.getGroups()
            // converts each ArrayList<Group> into an Observable<Group> that we can work with directly
            .flatMap(Observable::fromIterable)
            .filter(group -> group.name().contains(name));
    }

    /**
     * updates a group either by changing the name or updating the member list or both.
     */
    public Observable<Group> updateGroupName(Group group, String name) {
        return groupApiService.editGroup(
            group._id(),
            new UpdateGroupDto(name, group.members())
        );
    }

    public Observable<Group> addMembers(Group group, ArrayList<String> newMembers) {
        ArrayList<String> updatedMembers = group.members();
        updatedMembers.addAll(newMembers);
        return updateMembers(group, updatedMembers);
    }

    public Observable<Group> removeMembers(Group group, ArrayList<String> membersToBeRemoved) {
        ArrayList<String> updatedMembers = group.members();
        updatedMembers.removeAll(membersToBeRemoved);
        return updateMembers(group, updatedMembers);
    }

    // boiler plate function for add- and removeMembers
    public Observable<Group> updateMembers(Group group, ArrayList<String> members) {
        // prevents any duplicate members in the array list
        // distinct() filters out any duplicates,
        // collect() recollects these distinct elements
        members = new ArrayList<>(members.stream().distinct().collect(Collectors.toList()));
        return groupApiService.editGroup(
            group._id(),
            new UpdateGroupDto(group.name(), members)
        );
    }

    /**
     * deletes a group that is given by parameter
     * 
     * @param group the group to be deleted
     * @return the deleted group
     */
    public Observable<Group> deleteGroup(Group group) {
        return groupApiService.deleteGroup(group._id());
    }
}
