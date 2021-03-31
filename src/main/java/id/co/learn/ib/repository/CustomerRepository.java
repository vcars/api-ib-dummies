package id.co.learn.ib.repository;

import id.co.learn.ib.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query(value="select * from customers u where u.username=:username and is_deleted=0 and status = 1", nativeQuery = true)
    Customer getCustomerByAuthentication(@Param("username") String username);

    @Query(value="select * from customers u where u.username=:username ", nativeQuery = true)
    Optional<Customer> getUserByUsername(@Param("username") String username);

}
