package com.sweetshop.repository;
import com.sweetshop.model.Sweet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SweetRepository extends JpaRepository<Sweet, Long> {
    List<Sweet> findByNameContainingIgnoreCase(String name);
}
