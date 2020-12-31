package com.spiegelberger.app.ws.service;

import java.util.List;

import com.spiegelberger.app.ws.shared.dto.AddressDto;

public interface AddressService {

	List<AddressDto>getAddresses(String userId);
}
