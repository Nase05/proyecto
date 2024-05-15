package com.bsf.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bsf.model.Orden;
import com.bsf.model.Producto;
import com.bsf.model.Usuario;
import com.bsf.service.IOrdenService;
import com.bsf.service.IProductoService;
import com.bsf.service.IUsuarioService;



@Controller
@RequestMapping("/administrador")
public class AdministradorController {
 
	@Autowired
	private IProductoService productoService;
	
	@Autowired
	private IUsuarioService usuarioService;
	
	@Autowired
	private IOrdenService ordenService;
	
	private Logger logg = LoggerFactory.getLogger(AdministradorController.class);
	

	@GetMapping("")
	public String home(Model model) {
		
		List<Producto> productos = productoService.findAll();
		model.addAttribute("productos", productos);
		
		return "administrador/home";

	}
	
	@GetMapping("/usuarios")
	public String usuarios(Model model) {
		model.addAttribute("usuarios", usuarioService.findAll());
		return "administrador/usuarios";
		
	}
	
	@GetMapping("/ordenes")
	public String ordenes(Model model) {
		model.addAttribute("ordenes", ordenService.findAll());
		return "administrador/ordenes";
		
	}
	
	@GetMapping("/detalle/{id}")
	public String detalle(Model model, @PathVariable("id") Integer id) {
	    logg.info("Id de la orden {}", id);
	    // Obtener la orden correspondiente al id proporcionado
	    Orden orden = ordenService.findById(id).orElse(null);
	    
	    // Verificar si la orden existe
	    if (orden != null) {
	        // Obtener el usuario asociado a la orden
	        Usuario usuario = orden.getUsuario();
	        
	        // Agregar la orden y el usuario al modelo
	        model.addAttribute("orden", orden);
	        model.addAttribute("usuario", usuario);
	        
	        // Obtener y agregar los detalles de la orden al modelo
	        model.addAttribute("detalles", orden.getDetalle());
	    } else {
	        // En caso de que la orden no exista, redirigir a una página de error o manejar la situación apropiadamente
	        return "error";
	    }
	    
	    return "administrador/detalleorden";
	}

}