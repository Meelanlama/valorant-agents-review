package com.milan.agents_review_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.milan.agents_review_api.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
   User findByEmail(String username);

    Boolean existsByUserName(String userName);

    boolean existsByEmail(String email);


}
