package local.tmall_springboot.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import local.tmall_springboot.pojo.User;

public interface UserDAO extends JpaRepository<User, Integer> {

    User findByName(String name);

    User getByNameAndPassword(String name, String password);
}