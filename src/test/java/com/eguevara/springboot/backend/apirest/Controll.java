package com.eguevara.springboot.backend.apirest;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.eguevara.springboot.backend.apirest.models.entity.Cliente;
import com.eguevara.springboot.backend.apirest.models.services.ClienteServiceImpl;
import com.eguevara.springboot.backend.apirest.models.services.IClienteService;

@RunWith(MockitoJUnitRunner.class)
class Controll {

	@InjectMocks
	ClienteServiceImpl cliente = new ClienteServiceImpl();

	@Mock
	private IClienteService clienteService;

	@Test
	public void test1() {
		System.out.println("Test1");
		Long result = (long) 5;

		when(clienteService.findById(result)).thenReturn(new Cliente(result, "Edu", "Guevara", "Email@gmail.com"));

		assertEquals(cliente, cliente.findById(result));
		// fail("Not yet implemented");
	}

	public void test() {
		fail("Not yet implemented");
	}

}