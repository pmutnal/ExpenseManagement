package com.chaitanya.login.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chaitanya.base.BaseDTO;
import com.chaitanya.base.BaseDTO.ServiceStatus;
import com.chaitanya.employee.convertor.EmployeeConvertor;
import com.chaitanya.employee.dao.IEmployeeDAO;
import com.chaitanya.employee.model.EmployeeDTO;
import com.chaitanya.jpa.EmployeeJPA;
import com.chaitanya.jpa.LoginJPA;
import com.chaitanya.jpa.UserRoleJPA;
import com.chaitanya.login.convertor.LoginConvertor;
import com.chaitanya.login.dao.ILoginDAO;
import com.chaitanya.login.model.LoginDTO;
import com.chaitanya.login.model.LoginUserDetails;
import com.chaitanya.utility.MailServiceImpl;
import com.chaitanya.utility.Utility;

@Service("loginDetailsService")
public class LoginService implements UserDetailsService,ILoginService {

	@Autowired
	private ILoginDAO loginDAO;
	
	@Autowired
	private IEmployeeDAO employeeDAO;
	
	@Autowired
	private MailServiceImpl mailService;
	
    @Autowired
    private PasswordEncoder passwordEncoder;
	
	private Logger logger= LoggerFactory.getLogger(LoginService.class);
	
	private boolean validateLoginMasterDTO(BaseDTO baseDTO) {
		return baseDTO == null  || !(baseDTO instanceof LoginDTO);
	}

	@Transactional(readOnly=true)
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
		logger.debug("LoginService: loadUserByUsername-Start");
		LoginJPA loginDetails = loginDAO.findByUserName(username);
		List<GrantedAuthority> authorities=null;
		
		authorities = buildUserAuthority(loginDetails.getUserRole());
		LoginDTO loginDTO=LoginConvertor.setLoginJPAToDTO(loginDetails);
		logger.debug("LoginService: loadUserByUsername-End");
		return buildUserForAuthentication(loginDetails, authorities,loginDTO);
	}

	// Converts com.mkyong.users.model.User user to
	// org.springframework.security.core.userdetails.User
	private User buildUserForAuthentication(LoginJPA loginDetails, List<GrantedAuthority> authorities,LoginDTO loginDTO) {
		return new LoginUserDetails(loginDetails.getUserName(), loginDetails.getPassword(), true, true, true, true, authorities,loginDTO);
	}

	private List<GrantedAuthority> buildUserAuthority(Set<UserRoleJPA> userRoles) {
		logger.debug("LoginService: forgotPassword-Start");
		Set<GrantedAuthority> setAuths = new HashSet<GrantedAuthority>();

		// Build user's authorities
		for (UserRoleJPA userRole : userRoles) {
			setAuths.add(new SimpleGrantedAuthority(userRole.getRole()));
		}

		List<GrantedAuthority> Result = new ArrayList<GrantedAuthority>(setAuths);
		logger.debug("LoginService: forgotPassword-End");
		return Result;
	}

	@Override
	public BaseDTO forgotPassword(BaseDTO baseDTO) throws ParseException {
		logger.debug("LoginService: forgotPassword-Start");
		if(validateLoginMasterDTO(baseDTO)){
			throw new IllegalArgumentException("Object expected of LoginDTO type.");
		}
		
		LoginDTO loginDTO=(LoginDTO) baseDTO;
		EmployeeJPA employeeJPA = employeeDAO.findEmployeeByEmailId(loginDTO.getEmployeeDTO());
		EmployeeDTO employeeDTO= EmployeeConvertor.setEmployeeJPAToEmployeeDTO(employeeJPA);
		
		String password= Utility.SessionIdentifierGenerator.nextSessionId();
		int result = loginDAO.updatePassword(employeeDTO,passwordEncoder.encode(password));
		if(result == 1){
			mailService.sendAutoGeneratePassword(employeeDTO,password);
			baseDTO.setServiceStatus(ServiceStatus.SUCCESS);;
		}
		else{
			baseDTO.setServiceStatus(ServiceStatus.SYSTEM_FAILURE);
		}
		
		logger.debug("LoginService: forgotPassword-End");
		return baseDTO;
	}
	
}