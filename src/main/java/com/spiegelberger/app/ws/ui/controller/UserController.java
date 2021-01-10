package com.spiegelberger.app.ws.ui.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spiegelberger.app.ws.service.AddressService;
import com.spiegelberger.app.ws.service.UserService;
import com.spiegelberger.app.ws.shared.Roles;
import com.spiegelberger.app.ws.shared.dto.AddressDto;
import com.spiegelberger.app.ws.shared.dto.UserDto;
import com.spiegelberger.app.ws.ui.model.request.PasswordResetModel;
import com.spiegelberger.app.ws.ui.model.request.PasswordResetRequestModel;
import com.spiegelberger.app.ws.ui.model.request.UserDetailsRequestModel;
import com.spiegelberger.app.ws.ui.model.response.AddressRest;
import com.spiegelberger.app.ws.ui.model.response.OperationStatusModel;
import com.spiegelberger.app.ws.ui.model.response.RequestOperationStatus;
import com.spiegelberger.app.ws.ui.model.response.UserRest;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/users")	//http://localhost:8080/users
public class UserController {
	
	@Autowired
	UserService userService;
	
	@Autowired
	AddressService addressService;
	
	
	
	@PostAuthorize("hasRole('ROLE_ADMIN') or returnObject.userId == principal.userId")
	@ApiOperation(value="Get User Details Web Service Endpoint",
				notes="${userContoller.GetUser.ApiOperation.Notes}")
	@ApiImplicitParams(value = { 
			@ApiImplicitParam(name="authorization", value="${userContoller.authorizationHeader.description}", paramType="header") 
			})
	@GetMapping(path="/{id}",
		produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public UserRest getUser(@PathVariable String id) {
		
		UserRest returnValue = new UserRest();		
		
		UserDto userDto = userService.getUserByUserId(id);
		BeanUtils.copyProperties(userDto, returnValue);
		
		return returnValue;
	}
	
	
	@ApiOperation(value="Create User Web Service Endpoint",
			notes="${userContoller.CreateUser.ApiOperation.Notes}")
	@PostMapping(
			consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
			produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails)throws Exception {
		
		if(userDetails.getFirstName().isEmpty()) throw new 
			NullPointerException("The object is null");
		
		UserRest returnValue=new UserRest();
			
//		 Copy the sourceObject into a target object:	
		ModelMapper modelMapper = new ModelMapper();
		UserDto userDto = modelMapper.map(userDetails, UserDto.class);
		userDto.setRoles(new HashSet<>(Arrays.asList(Roles.ROLE_USER.name())));
		
		UserDto createdUser=userService.createUser(userDto);
		
		returnValue = modelMapper.map(createdUser, UserRest.class);
		
		return returnValue;
	}
	
	
	@ApiOperation(value="Update User Web Service Endpoint",
			notes="${userContoller.UdateUser.ApiOperation.Notes}")
	@ApiImplicitParams(value = { 
			@ApiImplicitParam(name="authorization", value="${userContoller.authorizationHeader.description}", paramType="header") 
			})
	@PutMapping(path="/{id}",
		consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
		produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {
		
		UserRest returnValue=new UserRest();
		UserDto userDto=new UserDto();
		
		// Copies sourceObject into a target object:
		BeanUtils.copyProperties(userDetails, userDto);
		
		UserDto updatedUser=userService.updateUser(id, userDto);
		
		BeanUtils.copyProperties(updatedUser,returnValue );
		
		return returnValue;
	}
	
	
	//Only the Admin and the given user can delete
	@PreAuthorize("hasRole('ROLE_ADMIN') or #id == principal.userId")
//	@PreAuthorize("hasAuthority('DELETE_AUTHORITY')")
//	@Secured("ROLE_ADMIN")
	@ApiOperation(value="Delete User Web Service Endpoint",
			notes="${userContoller.DeleteUser.ApiOperation.Notes}")
	@ApiImplicitParams(value = { 
			@ApiImplicitParam(name="authorization", value="${userContoller.authorizationHeader.description}", paramType="header") 
			})
	@DeleteMapping(path="/{id}",
			produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public OperationStatusModel deleteUser(@PathVariable String id) {
		
		OperationStatusModel returnValue= new OperationStatusModel();
		returnValue.setOperationName(RequestOperationName.DELETE.name());
		
		userService.deleteUser(id);
		
		returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		
		return returnValue;
	}
	
	@ApiOperation(value="Get User Details Web Service Endpoint",
			notes="${userContoller.GetUsers.ApiOperation.Notes}")
	@ApiImplicitParams(value = { 
			@ApiImplicitParam(name="authorization", value="${userContoller.authorizationHeader.description}", paramType="header") 
			})
	@GetMapping(produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public List<UserRest>getUsers(@RequestParam(value="page", defaultValue="0")int page,
			@RequestParam(value="limit", defaultValue = "2")int limit){
		
		List<UserRest>returnValue = new ArrayList<>();
		List<UserDto> users = userService.getUsers(page, limit);
		
		for(UserDto userDto : users) {
			UserRest userModel = new UserRest();
			BeanUtils.copyProperties(userDto, userModel);
			returnValue.add(userModel);
		}
		
		return returnValue;
	}
	
	
	@ApiOperation(value="Get User Addresses Web Service Endpoint",
			notes="${userContoller.GetAddresses.ApiOperation.Notes}")
	@ApiImplicitParams(value = { 
			@ApiImplicitParam(name="authorization", value="${userContoller.authorizationHeader.description}", paramType="header") 
			})
	@GetMapping(path = "/{id}/addresses",
			produces = { MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE })
	public CollectionModel<AddressRest> getUserAddresses(@PathVariable String id) {

		List<AddressRest>returnValue = new ArrayList<>();
		
		List<AddressDto> addressesDto = addressService.getAddresses(id);
		
		if (addressesDto != null && !addressesDto.isEmpty()) {
			Type listType = new TypeToken<List<AddressRest>>() {}.getType();
			returnValue = new ModelMapper().map(addressesDto, listType);
			
			for(AddressRest addressRest : returnValue) {
				Link selfLink=WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class)
						.getUserAddress(addressRest.getAddressId(), id))
						.withSelfRel();
				addressRest.add(selfLink);
			}
			}

//		HATEOAS:		
//		http://localhost:8080/users/<userId>
		Link userLink=WebMvcLinkBuilder.linkTo(UserController.class)
				.slash(id)
				.withRel("user");
		
		Link selfLink=WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class)
				.getUserAddresses(id))
				.withSelfRel();
		
		return CollectionModel.of(returnValue, userLink, selfLink);
		
	}
	
	
	@ApiOperation(value="Get User Address Web Service Endpoint",
			notes="${userContoller.GetAddress.ApiOperation.Notes}")
	@ApiImplicitParams(value = { 
			@ApiImplicitParam(name="authorization", value="${userContoller.authorizationHeader.description}", paramType="header") 
			})
	@GetMapping(path = "/{userId}/addresses/{addressId}", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public EntityModel<AddressRest> getUserAddress(@PathVariable String addressId, @PathVariable String userId) {
		
		AddressDto addressesDto = addressService.getAddress(addressId);

		ModelMapper modelMapper = new ModelMapper();
		AddressRest returnValue=modelMapper.map(addressesDto, AddressRest.class);
		
//		HATEOAS:		
//		http://localhost:8080/users/<userId>/addresses/{addressId}
		Link userLink=WebMvcLinkBuilder.linkTo(UserController.class)
				.slash(userId)
				.withRel("user");
		
		Link userAddressesLink=WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class)
				.getUserAddresses(userId))
//				.slash(userId)
//				.slash("addreses")
				.withRel("addreses");
				
		Link selfLink=WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class)
				.getUserAddress(addressId, userId))
				.withSelfRel();
	
		
		return EntityModel.of(returnValue, Arrays.asList(userLink, userAddressesLink, selfLink));
				 
	}
	
	
	
	@ApiOperation(value="Verify Email Token Web Service Endpoint",
			notes="${userContoller.VerifyEmailToken.ApiOperation.Notes}")
//		http://localhost:8080/mobile-app-ws/email-verification?token=sdfsdf
		@GetMapping(path="/email-verification", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE})
		public OperationStatusModel verifyEmailToken(@RequestParam(value="token") String token) {
			
			OperationStatusModel returnValue = new OperationStatusModel();
			returnValue.setOperationName(RequestOperationName.VERIFY_EMAIL.name());
			
			//check token's validity
			boolean isVerified = userService.verifyEmailToken(token);			
				if(isVerified) {
					returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
				}
				else {
					returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
				}
			
			return returnValue;
		}
		
		
		
		@ApiOperation(value="Request Password Resest Web Service Endpoint",
			notes="${userContoller.RequestReset.ApiOperation.Notes}")
//	    http://localhost:8080/mobile-app-ws/users/password-reset-request	     
	    @PostMapping(path = "/password-reset-request", 
	            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
	            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
	    )
	    public OperationStatusModel requestReset(@RequestBody PasswordResetRequestModel passwordResetRequestModel) {
	    	OperationStatusModel returnValue = new OperationStatusModel();
	 
	        boolean operationResult = userService.requestPasswordReset(passwordResetRequestModel.getEmail());
	        
	        returnValue.setOperationName(RequestOperationName.REQUEST_PASSWORD_RESET.name());
	        returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
	 
	        if(operationResult)
	        {
	            returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
	        }

	        return returnValue;
	    }		
		
		
	    
		@ApiOperation(value="Password Reset Web Service Endpoint",
				notes="${userContoller.ResetPassword.ApiOperation.Notes}")
	    @PostMapping(path = "/password-reset",
	    			consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
	    )
	    public OperationStatusModel resetPassword(@RequestBody PasswordResetModel passwordResetModel) {
	    	OperationStatusModel returnValue = new OperationStatusModel();
	 
	        boolean operationResult = userService.resetPassword(
	                passwordResetModel.getToken(),
	                passwordResetModel.getPassword());
	        
	        returnValue.setOperationName(RequestOperationName.PASSWORD_RESET.name());
	        returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
	 
	        if(operationResult)
	        {
	            returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
	        }

	        return returnValue;
	    }

}
