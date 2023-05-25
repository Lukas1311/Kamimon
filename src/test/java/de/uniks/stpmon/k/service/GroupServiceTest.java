package de.uniks.stpmon.k.service;

import de.uniks.stpmon.k.dto.CreateGroupDto;
import de.uniks.stpmon.k.dto.UpdateGroupDto;
import de.uniks.stpmon.k.models.Group;
import de.uniks.stpmon.k.rest.GroupApiService;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class GroupServiceTest {

    @Mock
    GroupApiService groupApiService;
    @InjectMocks
    GroupService groupService;

    @Test
    void createGroup() {
        final ArgumentCaptor<CreateGroupDto> captor = ArgumentCaptor.forClass(CreateGroupDto.class);
        when(groupApiService.createGroup(ArgumentMatchers.any(CreateGroupDto.class)))
                .thenReturn(Observable.just(new Group(
                        "01.01.2023",
                        "02.02.2023",
                        "1",
                        "Test",
                        new ArrayList<>()
                )));

        //action
        final Group newGroup = groupService.createGroup("Test", new ArrayList<>()).blockingFirst();

        //check values
        assertEquals("01.01.2023", newGroup.createdAt());
        assertEquals("02.02.2023", newGroup.updatedAt());
        assertEquals("1", newGroup._id());
        assertEquals("Test", newGroup.name());
        assertEquals(new ArrayList<String>(), newGroup.members());

        //check mocks
        verify(groupApiService).createGroup(captor.capture());
    }

    @Test
    void getOwnGroups() {
        ArrayList<Group> groups = new ArrayList<>();
        groups.add(new Group(
                "01.01.2023",
                "02.02.2023",
                "1",
                "Test",
                new ArrayList<>()
        ));
        groups.add(new Group(
                "01.01.2023",
                "02.02.2023",
                "2",
                "Test2",
                new ArrayList<>()
        ));
        when(groupApiService.getGroups()).thenReturn(Observable.just(groups));

        //action
        Observable<ArrayList<Group>> ownGroups = groupService.getOwnGroups();

        //check values
        assertEquals(groups, ownGroups.blockingFirst());
        assertEquals(2, ownGroups.blockingFirst().size());

        //check mocks
        verify(groupApiService).getGroups();
    }

    @Test
    void getGroupsByMembers() {
        ArrayList<String> members = new ArrayList<>();
        members.add("01");
        ArrayList<Group> groups = new ArrayList<>();
        groups.add(new Group(
                "01.01.2023",
                "02.02.2023",
                "1",
                "Test",
                members
        ));
        when(groupApiService.getGroups("01")).thenReturn(Observable.just(groups));

        //action
        Observable<ArrayList<Group>> groupsByMembers = groupService.getGroupsByMembers(members);

        //check values
        assertEquals(groups, groupsByMembers.blockingFirst());
        assertEquals("01", groupsByMembers.blockingFirst().get(0).members().get(0));

        //check mocks
        verify(groupApiService).getGroups("01");
    }

    @Test
    void searchGroup() {
        ArrayList<Group> groups = new ArrayList<>();
        groups.add(new Group(
                "01.01.2023",
                "02.02.2023",
                "1",
                "Alice",
                new ArrayList<>()
        ));
        groups.add(new Group(
                "01.01.2023",
                "02.02.2023",
                "2",
                "Bob",
                new ArrayList<>()
        ));
        when(groupApiService.getGroups()).thenReturn(Observable.just(groups));

        //action
        Observable<Group> searchGroup = groupService.searchGroup("Alice");

        //check values
        assertEquals("Alice", searchGroup.blockingFirst().name());
        assertEquals("1", searchGroup.blockingFirst()._id());

        //check mocks
        verify(groupApiService).getGroups();
    }

    @Test
    void updateGroup() {
        ArrayList<String> members = new ArrayList<>();
        members.add("01");
        Group group = new Group(
                "01.01.2023",
                "02.02.2023",
                "1",
                "Alice",
                new ArrayList<>()
        );
        Group editedGroup = new Group(
                "01.01.2023",
                "02.02.2023",
                "1",
                "Bob",
                members
        );

        UpdateGroupDto updateGroupDto = new UpdateGroupDto("Bob", members);
        when(groupApiService.editGroup("1", updateGroupDto)).thenReturn(Observable.just(editedGroup));

        //action
        Observable<Group> updatedGroup = groupService.updateGroup(group, "Bob", members);

        //check values
        assertEquals("Bob", updatedGroup.blockingFirst().name());
        assertEquals("01", updatedGroup.blockingFirst().members().get(0));

        //check mocks
        verify(groupApiService).editGroup("1", updateGroupDto);
    }

    @Test
    void deleteGroup() {
        Group group = new Group(
                "01.01.2023",
                "02.02.2023",
                "1",
                "Alice",
                new ArrayList<>()
        );

        when(groupApiService.deleteGroup("1")).thenReturn(Observable.just(group));

        //action
        Observable<Group> deletedGroup = groupService.deleteGroup(group);

        //check values
        assertEquals(group, deletedGroup.blockingFirst());

        //check mocks
        verify(groupApiService).deleteGroup("1");
    }


}
