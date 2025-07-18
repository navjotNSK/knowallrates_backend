package com.knowallrates.goldapi.service;

import com.knowallrates.goldapi.dto.AddressRequest;
import com.knowallrates.goldapi.dto.AddressResponse;
import com.knowallrates.goldapi.model.Address;
import com.knowallrates.goldapi.model.User;
import com.knowallrates.goldapi.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    public List<AddressResponse> getUserAddresses(User user) {
        List<Address> addresses = addressRepository.findByUserOrderByIsDefaultDescCreatedAtDesc(user);
        return addresses.stream()
                .map(AddressResponse::new)
                .collect(Collectors.toList());
    }

    public AddressResponse createAddress(User user, AddressRequest request) {
        // If this is set as default, reset all other default addresses
        if (request.getIsDefault() != null && request.getIsDefault()) {
            addressRepository.resetAllDefaultAddresses(user);
        }

        Address address = new Address();
        address.setUser(user);
        address.setFullName(request.getFullName());
        address.setPhoneNumber(request.getPhoneNumber());
        address.setAddressLine1(request.getAddressLine1());
        address.setAddressLine2(request.getAddressLine2());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPincode(request.getPincode());
        address.setIsDefault(request.getIsDefault() != null ? request.getIsDefault() : false);

        // If this is the first address, make it default
        if (addressRepository.countByUser(user) == 0) {
            address.setIsDefault(true);
        }

        Address savedAddress = addressRepository.save(address);
        return new AddressResponse(savedAddress);
    }

    public AddressResponse updateAddress(User user, Long addressId, AddressRequest request) {
        Optional<Address> addressOpt = addressRepository.findByIdAndUser(addressId, user);
        if (addressOpt.isEmpty()) {
            throw new RuntimeException("Address not found or doesn't belong to user");
        }

        Address address = addressOpt.get();

        // If this is set as default, reset all other default addresses
        if (request.getIsDefault() != null && request.getIsDefault() && !address.getIsDefault()) {
            addressRepository.resetDefaultAddresses(user, addressId);
        }

        address.setFullName(request.getFullName());
        address.setPhoneNumber(request.getPhoneNumber());
        address.setAddressLine1(request.getAddressLine1());
        address.setAddressLine2(request.getAddressLine2());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPincode(request.getPincode());
        if (request.getIsDefault() != null) {
            address.setIsDefault(request.getIsDefault());
        }

        Address savedAddress = addressRepository.save(address);
        return new AddressResponse(savedAddress);
    }

    public void deleteAddress(User user, Long addressId) {
        Optional<Address> addressOpt = addressRepository.findByIdAndUser(addressId, user);
        if (addressOpt.isEmpty()) {
            throw new RuntimeException("Address not found or doesn't belong to user");
        }

        Address address = addressOpt.get();
        boolean wasDefault = address.getIsDefault();
        
        addressRepository.delete(address);

        // If deleted address was default, make another address default
        if (wasDefault) {
            List<Address> remainingAddresses = addressRepository.findByUserOrderByIsDefaultDescCreatedAtDesc(user);
            if (!remainingAddresses.isEmpty()) {
                Address newDefault = remainingAddresses.get(0);
                newDefault.setIsDefault(true);
                addressRepository.save(newDefault);
            }
        }
    }

    public AddressResponse getAddressById(User user, Long addressId) {
        Optional<Address> addressOpt = addressRepository.findByIdAndUser(addressId, user);
        if (addressOpt.isEmpty()) {
            throw new RuntimeException("Address not found or doesn't belong to user");
        }
        return new AddressResponse(addressOpt.get());
    }

    public AddressResponse getDefaultAddress(User user) {
        Optional<Address> defaultAddress = addressRepository.findByUserAndIsDefaultTrue(user);
        return defaultAddress.map(AddressResponse::new).orElse(null);
    }
}
