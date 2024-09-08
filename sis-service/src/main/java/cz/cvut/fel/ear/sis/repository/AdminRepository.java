package cz.cvut.fel.ear.sis.repository;

import cz.cvut.fel.ear.sis.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
}
