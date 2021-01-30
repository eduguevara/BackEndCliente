package com.eguevara.springboot.backend.apirest.models.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eguevara.springboot.backend.apirest.models.entity.Cliente;

public interface IClienteService {
/*Metodo por defecto que no es obligatorio implementarlo desde una clase// También puede ser modificado o rescrito
	default public void methodDefault() {
		System.out.println("Metodo por defecto");
	}*/
	public List<Cliente> findAll();
	public Page<Cliente> findAll(Pageable pageable);
	public Cliente save (Cliente cliente);
	public void delete (Long id);
	public Cliente findById (Long id);

}
