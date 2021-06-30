package com.devsuperior.bds04.controllers;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.devsuperior.bds04.dto.UserDTO;
import com.devsuperior.bds04.dto.UserInsertDTO;
import com.devsuperior.bds04.dto.UserUpdateDTO;
import com.devsuperior.bds04.service.UserService;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    @Autowired
    private UserService productService;

    @GetMapping
    public ResponseEntity<Page<UserDTO>> findAll( Pageable pageable) {
        Page<UserDTO> list = productService.findAllPaged(pageable);
        return ResponseEntity.ok().body(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> findByID(@PathVariable Long id){
        UserDTO productDTO = productService.findById(id);

        return ResponseEntity.ok().body(productDTO);
    }

    @PostMapping
    public ResponseEntity<UserDTO> insertUser(@Valid @RequestBody UserInsertDTO dto){
        UserDTO newDto = productService.insert(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
            .buildAndExpand(newDto.getId()).toUri();

        return ResponseEntity.created(uri).body(newDto);

    }


    @PutMapping(value = "/{id}")
    public ResponseEntity<UserDTO> updateCategory(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO dto){
        UserDTO newDto = productService.update(id, dto);

        return ResponseEntity.ok().body(newDto);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<UserDTO> deleteUserById(@PathVariable Long id){
        productService.deleteUserById(id);

        return ResponseEntity.noContent().build();
    }

}