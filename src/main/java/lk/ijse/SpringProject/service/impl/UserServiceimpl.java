package lk.ijse.SpringProject.service.impl;

import jakarta.transaction.Transactional;
import lk.ijse.SpringProject.dto.UserDto;
import lk.ijse.SpringProject.entity.User;
import lk.ijse.SpringProject.repository.Userresposistory;
import lk.ijse.SpringProject.service.UserService;
import lk.ijse.SpringProject.util.Varlist;
import org.modelmapper.ModelMapper;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Set;


@Service
@Transactional
public class UserServiceimpl implements UserDetailsService, UserService {
    @Autowired
    private Userresposistory userresposistory;
    @Autowired
    private ModelMapper modelMapper;
    @Override
    public int saveUser(UserDto userDTO) {
        if (userresposistory.existsByEmail(userDTO.getEmail())) {
            return Varlist.Not_Acceptable;
        }else{
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            userDTO.setPassword(passwordEncoder.encode(userDTO.getEmail()));
            userDTO.setRole("Dash_ADMIN");
            userresposistory.save(modelMapper.map(userDTO,User.class));
            return Varlist.Created;
        }
    }

    @Override
    public UserDto searhUser(String username) {
        if (userresposistory.existsByEmail(username)){
            User user = userresposistory.findByEmail(username);
            return modelMapper.map(user,UserDto.class);

        }else {
            return null;
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userresposistory.findByEmail(username);
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), getAuthority(user));
}

    public UserDto loadUserDetailsByUsername(String username) throws UsernameNotFoundException {
        User user = userresposistory.findByEmail(username);
        return modelMapper.map(user, UserDto.class);
}
     private Set<SimpleGrantedAuthority> getAuthority(User user){
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole()));
        return authorities;
    }

}
