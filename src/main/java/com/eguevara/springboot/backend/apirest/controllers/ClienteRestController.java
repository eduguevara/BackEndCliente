package com.eguevara.springboot.backend.apirest.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.eguevara.springboot.backend.apirest.models.entity.Cliente;
import com.eguevara.springboot.backend.apirest.models.services.IClienteService;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/api")
public class ClienteRestController {

	@Autowired
	private IClienteService clienteService;

	@GetMapping("/clientes") 
	public List<Cliente> index() {

		return clienteService.findAll();

	}
	
	@GetMapping("/clientes/page/{page}")
	public Page<Cliente> index(@PathVariable Integer page) {
		Pageable pageable = PageRequest.of(page, 4);
		//return clienteService.findAll(PageRequest.of(page, 4));
		return clienteService.findAll(pageable);
	}
	
	//ResponseEntity<?> Se devuelve según la respuesta Http. ? es para devolver cualquier tipo de dato.
	@GetMapping("/clientes/{id}")
	//@ResponseStatus(HttpStatus.OK) No hace falta ya que es lo que se devuelve
	public ResponseEntity<?> show(@PathVariable Long id) {
		Cliente cliente = null;
		Map<String, Object> respuesta = new HashMap<>();
		
		try {
			cliente = clienteService.findById(id);
		} catch (DataAccessException e) {
			respuesta.put("Mensaje", "Error al realizar la consulta a la BBDD");
			respuesta.put("Mensaje",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(respuesta, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(cliente == null) {
			respuesta.put("Mensaje", "El Cliente ID: ".concat(id.toString().concat(" no existe en la base de datos!")));
			return new ResponseEntity<Map<String, Object>>(respuesta, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Cliente>(cliente, HttpStatus.OK);

	}
	
	//Es necesario @Valid para que las validaciones de la clase se validen(ejem: @NotEmpty)
	@PostMapping("/clientes")
	public ResponseEntity<?> create(@Valid @RequestBody Cliente cliente, BindingResult result) {
		
		Cliente newCliente = null;
		Map<String, Object> respuesta = new HashMap<>();
		//si existen errores en el @Valid los recogemos con el .stream() y los guardamos en un map y con .collect los tranformamos a 
		//una List<String>
		if(result.hasErrors()) {
			/*Anterior a java 8
			 * List<String> errors = new ArrayList<>();
			for(FieldError err : result.getFieldErrors()) {
				errors.add("El campo '" + err.getField() + "' "+err.getDefaultMessage());
			}*/
			List<String> errors = result.getFieldErrors()
					.stream()
					.map(err -> "El campo '" + err.getField() + "' "+err.getDefaultMessage())
					.collect(Collectors.toList());
			
			respuesta.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(respuesta, HttpStatus.BAD_REQUEST);
		}
		
		try {
			newCliente = clienteService.save(cliente);
		} catch (DataAccessException e) {
			respuesta.put("Mensaje", "Error al crear el cliente en la BBDD");
			respuesta.put("Error",e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(respuesta, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		respuesta.put("Mensaje", "El cliente ha sido creado con éxito!");
		respuesta.put("Cliente", newCliente);
		return new ResponseEntity<Map<String, Object>>(respuesta, HttpStatus.CREATED);
	}
	
	@PutMapping("/clientes/{id}")
	@ResponseStatus (HttpStatus.CREATED)
	public ResponseEntity<?> update (@Valid @RequestBody Cliente cliente, BindingResult result, @PathVariable Long id) {
		Cliente clienteActual = clienteService.findById(id);
		Map<String, Object> respuesta = new HashMap<>();
		Cliente updateCliente = null;
		
		if(result.hasErrors()) {
			/*Anterior a java 8
			 * List<String> errors = new ArrayList<>();
			for(FieldError err : result.getFieldErrors()) {
				errors.add("El campo '" + err.getField() + "' "+err.getDefaultMessage());
			}*/
			List<String> errors = result.getFieldErrors()
					.stream()
					.map(err -> "El campo '" + err.getField() + "' "+err.getDefaultMessage())
					.collect(Collectors.toList());
			
			respuesta.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(respuesta, HttpStatus.BAD_REQUEST);
		}
		
		if (clienteActual == null) {
			respuesta.put("Mensaje", "No se puede modificar el Cliente ID: ".concat(id.toString().concat(" no existe en la base de datos!")));
			return new ResponseEntity<Map<String, Object>>(respuesta, HttpStatus.NOT_FOUND);
		}
		
		try {
			clienteActual.setApellido(cliente.getApellido());
			clienteActual.setNombre(cliente.getNombre());
			clienteActual.setEmail(cliente.getEmail());
			clienteActual.setCreateAt(cliente.getCreateAt());
			updateCliente = clienteService.save(clienteActual);
		} catch (DataAccessException e) {
			respuesta.put("Mensaje", "Error al modificar el cliente en la BBDD");
			respuesta.put("Error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(respuesta, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		respuesta.put("Mensaje", "El cliente se ha modificado con éxito!");
		respuesta.put("Cliente", updateCliente);
		return new ResponseEntity<Map<String, Object>>(respuesta, HttpStatus.CREATED);
	}
	
	@DeleteMapping("/clientes/{id}")
	public ResponseEntity<?> delete (@PathVariable Long id) {
		
		Map<String, Object> respuesta = new HashMap<>();
		
		try {
			clienteService.delete(id);
		} catch (DataAccessException e) {
			respuesta.put("Mensaje", "No se puede eliminar el cliente ID: ".concat(id.toString()).concat(" no existe en la BBDD"));
			respuesta.put("Error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(respuesta, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		//TODO revisar servicios
		respuesta.put("Mensaje", "El cliente se ha borrado con éxito!");
		return new ResponseEntity<Map<String, Object>>(respuesta, HttpStatus.OK);
		
	}
}
