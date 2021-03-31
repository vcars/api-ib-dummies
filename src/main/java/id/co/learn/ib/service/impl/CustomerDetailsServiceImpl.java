package id.co.learn.ib.service.impl;

import id.co.learn.ib.entity.Customer;
import id.co.learn.ib.service.CustomerDetailService;
import id.co.learn.ib.util.UserPrinciple;
import id.co.learn.ib.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
* @author  Adinandra Dharmasurya
* @version 1.0
* @since   2020-12-08
*/
@Service
public class CustomerDetailsServiceImpl implements CustomerDetailService {

    @Autowired
	private CustomerRepository customerRepository;

	@Override
	public UserDetails loadUserByUsername(String username) {
		Customer user = customerRepository.getCustomerByAuthentication(username);
		return UserPrinciple.build(user);
	}
    
}
