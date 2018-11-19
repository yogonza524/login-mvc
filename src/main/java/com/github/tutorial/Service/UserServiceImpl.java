package com.github.tutorial.Service;

import com.github.tutorial.Persistence.dao.RoleRepository;
import com.github.tutorial.Persistence.dao.UserRepository;
import com.github.tutorial.Persistence.model.Role;
import com.github.tutorial.Persistence.model.User;
import com.github.tutorial.Web.dto.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;


@Service("userService")
public class UserServiceImpl implements UserService {

    private final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Autowired
    private HttpServletRequest request;

    @Override
    public User findUserByEmail(String email) {
        String ip = getClientIP();
        if (loginAttemptService.isBlocked(ip)) {
            throw new RuntimeException("blocked");
        }
        return userRepository.findByEmail(email);
    }

    @PostConstruct
    public void init() {
        log.info("Post construct UserServiceImpl, listing roles");

        Long total = roleRepository.count();

        if (total == 0) {
            log.warn("No se encontraron roles");
            roleRepository.save(Role.builder().id(1L).role("ADMIN").build());
            roleRepository.save(Role.builder().id(2L).role("USER").build());
        }

        Iterator<Role> i = roleRepository.findAll().iterator();
        while(i.hasNext()) {
            log.warn("Role: " + i.next());
        }
    }

    @Override
    public void createUserAccount(UserDto accountDto) {
        final User user = new User();
        user.setName(accountDto.getFirstName());
        user.setLastName(accountDto.getLastName());
        user.setEmail(accountDto.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(accountDto.getPassword()));
        user.setActive(1);
        Role userRole = roleRepository.findByRole("USER");
        if (accountDto.getRole() != null) {
            userRole = roleRepository.findOne(accountDto.getRole());
        }
        else {
            log.warn("Setting user role by default for " + accountDto.getEmail());
        }

        log.info("Role setted: "  + userRole);

        user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
        userRepository.save(user);
    }

    private String getClientIP() {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

}
