package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.dto.CreateGroupDto;
import de.uniks.stpmon.k.dto.UpdateGroupDto;
import de.uniks.stpmon.k.models.Group;
import de.uniks.stpmon.k.rest.GroupApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton
public class GroupService {

    private final GroupApiService groupApiService;

    @Inject
    public GroupService(GroupApiService groupApiService) {
        this.groupApiService = groupApiService;
    }

    // creates a new group: caller has to provide a list of members and a name for the group
    public Observable<Group> createGroup(String name, List<String> members) {
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
        return groupApiService.getGroups(String.join(",", members));
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
     * Deletes or updates a group depending on the current members of the group.
     *
     * @param group   the group to be deleted or updated
     * @param name    the new name of the group
     * @param members the new members of the group
     * @return observable with the updated group from the server
     */
    public Observable<Group> deleteOrUpdateGroup(Group group, String name, Collection<String> members) {
        // Use old group members so the current edit of the user ha no affect
        Set<String> currentMembers = new HashSet<>(group.members());
        // Check if only the current user is left in the group
        if (currentMembers.size() == 1) {
            return deleteGroup(group);
        }
        return updateGroup(group, name, members);
    }

    /**
     * updates a group either by changing the name or updating the member list or both.
     */
    public Observable<Group> updateGroup(Group group, String name, Collection<String> members) {
        return groupApiService.editGroup(
                group._id(),
                new UpdateGroupDto(name, members.stream().distinct().toList())
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
