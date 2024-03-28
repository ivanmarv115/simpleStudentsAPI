package ivanmartinez.simpleStudentsAPI.Service;

import ivanmartinez.simpleStudentsAPI.DTO.CreateUserRequest;
import ivanmartinez.simpleStudentsAPI.DTO.LongIdRequest;
import ivanmartinez.simpleStudentsAPI.Entity.Role;
import ivanmartinez.simpleStudentsAPI.Entity.User;
import ivanmartinez.simpleStudentsAPI.Exception.ResourceAlreadyExistsException;
import ivanmartinez.simpleStudentsAPI.Exception.ResourceNotFoundException;
import ivanmartinez.simpleStudentsAPI.Repository.UserRepository;
import ivanmartinez.simpleStudentsAPI.Service.Implementations.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserServiceImpl underTest;

    @Test
    void shouldCreateUser() throws ResourceAlreadyExistsException {
        //given
        CreateUserRequest request = CreateUserRequest.builder()
                .username("admin")
                .password("admin")
                .role(Role.ADMIN)
                .build();

        User user = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin"))
                .role(Role.ADMIN)
                .build();

        given(userRepository.findByUsername(request.getUsername())).willReturn(
                Optional.empty());

        //when
        underTest.addUser(request);

        //test
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());

        assertThat(userArgumentCaptor.getValue())
                .usingRecursiveComparison()
                .isEqualTo(user);
    }

    @Test
    void shouldLockUser() throws ResourceNotFoundException {
        //given
        LongIdRequest request = LongIdRequest.builder()
                .longId(1L)
                .build();

        User user = User.builder()
                .username("admin")
                .isNonLocked(false)
                .role(Role.ADMIN)
                .build();

        given(userRepository.findById(request.getLongId())).willReturn(
                Optional.of(user));

        //when
        underTest.lockUser(request);

        //test
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());

        assertThat(userArgumentCaptor.getValue().getIsNonLocked())
                .isEqualTo(true);
    }

    @Test
    void shouldUnlockUser() throws ResourceNotFoundException {
        //given
        LongIdRequest request = LongIdRequest.builder()
                .longId(1L)
                .build();

        User user = User.builder()
                .username("admin")
                .isNonLocked(true)
                .role(Role.ADMIN)
                .build();

        given(userRepository.findById(request.getLongId())).willReturn(
                Optional.of(user));

        //when
        underTest.unlockUser(request);

        //test
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());

        assertThat(userArgumentCaptor.getValue().getIsNonLocked())
                .isEqualTo(false);
    }
}