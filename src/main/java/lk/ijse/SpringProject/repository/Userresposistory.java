package lk.ijse.SpringProject.repository;

import lk.ijse.SpringProject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Userresposistory extends JpaRepository<User,String> {
    User findByEmail(String userName);

    boolean existsByEmail(String userName);

    int deleteByEmail(String userName);
}
