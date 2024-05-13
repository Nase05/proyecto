package com.bsf.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bsf.model.DetalleOrden;
import com.bsf.model.Orden;
import com.bsf.model.Producto;
import com.bsf.model.Usuario;
import com.bsf.service.IDetalleOrdenService;
import com.bsf.service.IOrdenService;
import com.bsf.service.IProductoService;
import com.bsf.service.IUsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/")
public class HomeController {

	private final Logger log = LoggerFactory.getLogger(HomeController.class);

	@Autowired
	private IProductoService productoService;

	@Autowired
	private IUsuarioService usuarioService;

	@Autowired
	private IOrdenService ordenService;

	@Autowired
	private IDetalleOrdenService detalleOrdenService;

	List<DetalleOrden> detalles = new ArrayList<DetalleOrden>();

	Orden orden = new Orden();

	@GetMapping("")
	public String home(Model model, HttpSession session) {
		Integer idUsuario = (Integer) session.getAttribute("idusuario");
		if (idUsuario != null) {
		    model.addAttribute("sesion", idUsuario);
		} else {
		    model.addAttribute("sesion", null);
		}

		log.info("Sesión del usuario: {}", session.getAttribute("idusuario"));
		model.addAttribute("productos", productoService.findAll());
		model.addAttribute("session", session.getAttribute("idusuario"));

		return "usuario/home";
	}

	@GetMapping("productohome/{id}")
	public String productoHome(@PathVariable("id") Integer id, Model model,  HttpSession session) {
		Integer idUsuario = (Integer) session.getAttribute("idusuario");
		if (idUsuario != null) {
		    model.addAttribute("sesion", idUsuario);
		} else {
		    model.addAttribute("sesion", null);
		}

		log.info("Id producto enviado como parámetro {}", id);
		Producto producto = new Producto();
		Optional<Producto> productoOptional = productoService.get(id);
		producto = productoOptional.get();

		model.addAttribute("producto", producto);

		return "usuario/productohome";
	}

	@PostMapping("/cart")
	public String addCart(@RequestParam("id") Integer id, @RequestParam("cantidad") Integer cantidad, Model model, HttpSession session) {
		DetalleOrden detalleOrden = new DetalleOrden();
		Producto producto = new Producto();
		double sumaTotal = 0;
		Integer idUsuario = (Integer) session.getAttribute("idusuario");
		if (idUsuario != null) {
		    model.addAttribute("sesion", idUsuario);
		} else {
		    model.addAttribute("sesion", null);
		}


		Optional<Producto> optionalProducto = productoService.get(id);
		log.info("Producto añadido {}", optionalProducto.get());
		log.info("Cantidad {}", cantidad);
		producto = optionalProducto.get();
		detalleOrden.setCantidad(cantidad);
		detalleOrden.setPrecio(producto.getPrecio());
		detalleOrden.setNombre(producto.getNombre());
		detalleOrden.setTotal(producto.getPrecio() * cantidad);
		detalleOrden.setProducto(producto);

		Integer idProducto = producto.getId();
		boolean ingresado = detalles.stream().anyMatch(p -> p.getProducto().getId() == idProducto);

		if (!ingresado) {
			detalles.add(detalleOrden);
		}

		sumaTotal = detalles.stream().mapToDouble(dt -> dt.getTotal()).sum();
		orden.setTotal(sumaTotal);
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);

		return "usuario/carrito";
	}

	@GetMapping("/delete/cart/{id}")
	public String deleteProducto(@PathVariable("id") Integer id, Model model) {
		List<DetalleOrden> ordenesNueva = new ArrayList<DetalleOrden>();
		for (DetalleOrden detalleOrden : detalles) {
			if (detalleOrden.getProducto().getId() != id) {
				ordenesNueva.add(detalleOrden);
			}
		}

		detalles = ordenesNueva;

		double sumaTotal = 0;
		sumaTotal = detalles.stream().mapToDouble(dt -> dt.getTotal()).sum();
		orden.setTotal(sumaTotal);
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);

		return "usuario/carrito";
	}

	@GetMapping("/getCart")
	public String getCart(Model model, HttpSession session) {
	
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);
		
		model.addAttribute("sesion", session.getAttribute("idusuario"));
		return "usuario/carrito";
	}

	@GetMapping("/order")
	public String order(Model model, HttpSession session) {

		Integer idUsuario = (Integer) session.getAttribute("idusuario");
		if (idUsuario != null) {
		    model.addAttribute("sesion", idUsuario);
		} else {
		    model.addAttribute("sesion", null);
		}

		Usuario usuario = usuarioService.findById(Integer.parseInt(session.getAttribute("idusuario").toString())).get();

		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);
		model.addAttribute("usuario", usuario);
		return "usuario/resumenorden";
	}

	@GetMapping("/saveOrder")
	public String saveOrder(HttpSession session) {
		Date fechaCreacion = new Date();
		orden.setFechaCreacion(fechaCreacion);
		orden.setNumero(ordenService.generarNumeroOrden());

		Usuario usuario = usuarioService.findById(Integer.parseInt(session.getAttribute("idusuario").toString())).get();

		orden.setUsuario(usuario);
		ordenService.save(orden);

		for (DetalleOrden dt : detalles) {
			dt.setOrden(orden);
			detalleOrdenService.save(dt);
		}

		orden = new Orden();
		detalles.clear();
		return "redirect:/";
	}
	
	@PostMapping("/search")
	public String searchProduct(@RequestParam("nombre") String nombre, Model model) {
		log.info("Nombre del producto: {}", nombre);
		List<Producto> productos = productoService.findAll().stream().filter(p -> p.getNombre().contains(nombre)).collect(Collectors.toList());
		model.addAttribute("productos", productos);
		return "usuario/home";
	}

}