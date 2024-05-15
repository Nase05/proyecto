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
			Optional<Usuario> userOptional = usuarioService.findById(idUsuario);
			model.addAttribute("sesion", idUsuario);
			 if (userOptional.isPresent()) {
		            Usuario usuario = userOptional.get();
		            model.addAttribute("usuario", usuario); // Agrega el objeto usuario al modelo
			 }
			// Verificar si el usuario es un administrador
			Optional<Usuario> user = usuarioService.findById(idUsuario);
			if (user.isPresent() && user.get().getTipo().equals("ADMIN")) {
				model.addAttribute("productos", productoService.findAll());
				// Si el usuario es un administrador, redirigir a la página de inicio del
				// administrador
				return "administrador/home";
			}
		} else {
			model.addAttribute("sesion", null);
			model.addAttribute("isAdmin", false);
		}

		log.info("Sesión del usuario: {}", session.getAttribute("idusuario"));
		model.addAttribute("productos", productoService.findAll());
		model.addAttribute("session", session.getAttribute("idusuario"));

		return "usuario/home";
	}

	@GetMapping("usuario/contacto")
	public String contacto(Model model, HttpSession session) {
		Integer idUsuario = (Integer) session.getAttribute("idusuario");
		if (idUsuario != null) {
			model.addAttribute("sesion", idUsuario);
		} else {
			model.addAttribute("sesion", null);
		}

		log.info("Sesión del usuario: {}", session.getAttribute("idusuario"));

		return "usuario/contacto";
	}
	
	@GetMapping("usuario/acercade")
	public String acercade(Model model, HttpSession session) {
		Integer idUsuario = (Integer) session.getAttribute("idusuario");
		if (idUsuario != null) {
			model.addAttribute("sesion", idUsuario);
		} else {
			model.addAttribute("sesion", null);
		}

		log.info("Sesión del usuario: {}", session.getAttribute("idusuario"));

		return "usuario/acercade";
	}

	@GetMapping("usuario/mobiliario")
	public String mobiliario(Model model, HttpSession session) {
		Integer idUsuario = (Integer) session.getAttribute("idusuario");
		if (idUsuario != null) {
			model.addAttribute("sesion", idUsuario);
		} else {
			model.addAttribute("sesion", null);
		}

		log.info("Sesión del usuario: {}", session.getAttribute("idusuario"));

		// Filtrar los productos por tipo "mobiliario"
		List<Producto> productosMobiliario = productoService.findAll().stream()
				.filter(producto -> "mobiliario".equalsIgnoreCase(producto.getTipo())).collect(Collectors.toList());

		model.addAttribute("productos", productosMobiliario);
		model.addAttribute("session", session.getAttribute("idusuario"));

		return "usuario/mobiliario";
	}

	@GetMapping("usuario/piezas")
	public String piezas(Model model, HttpSession session) {
		Integer idUsuario = (Integer) session.getAttribute("idusuario");
		if (idUsuario != null) {
			model.addAttribute("sesion", idUsuario);
		} else {
			model.addAttribute("sesion", null);
		}

		log.info("Sesión del usuario: {}", session.getAttribute("idusuario"));

		// Filtrar los productos por tipo "mobiliario"
		List<Producto> productosMobiliario = productoService.findAll().stream()
				.filter(producto -> "piezas".equalsIgnoreCase(producto.getTipo())).collect(Collectors.toList());

		model.addAttribute("productos", productosMobiliario);
		model.addAttribute("session", session.getAttribute("idusuario"));

		return "usuario/piezas";
	}

	@GetMapping("usuario/cachimbas")
	public String cachimbas(Model model, HttpSession session) {
		Integer idUsuario = (Integer) session.getAttribute("idusuario");
		if (idUsuario != null) {
			model.addAttribute("sesion", idUsuario);
		} else {
			model.addAttribute("sesion", null);
		}

		log.info("Sesión del usuario: {}", session.getAttribute("idusuario"));

		// Filtrar los productos por tipo "mobiliario"
		List<Producto> productosMobiliario = productoService.findAll().stream()
				.filter(producto -> "cachimbas".equalsIgnoreCase(producto.getTipo())).collect(Collectors.toList());

		model.addAttribute("productos", productosMobiliario);
		model.addAttribute("session", session.getAttribute("idusuario"));

		return "usuario/cachimbas";
	}

	@GetMapping("productohome/{id}")
	public String productoHome(@PathVariable("id") Integer id, Model model, HttpSession session) {
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
	public String addCart(@RequestParam("id") Integer id, @RequestParam("cantidad") Integer cantidad, Model model,
			HttpSession session) {
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
	public String searchProduct(@RequestParam("nombre") String nombre, Model model, HttpSession session) {
	    Integer idUsuario = (Integer) session.getAttribute("idusuario");
	    if (idUsuario != null) {
	    	Optional<Usuario> userOptional = usuarioService.findById(idUsuario);
	        Usuario usuario = userOptional.get();
	        if (usuario.getTipo().equals("ADMIN")) {
	            // Si el usuario es un administrador, busca todos los productos y los filtra por nombre
	            List<Producto> productos = productoService.findAll().stream()
	                    .filter(p -> p.getNombre().toLowerCase().contains(nombre.toLowerCase()))
	                    .collect(Collectors.toList());
	            model.addAttribute("productos", productos);
	            return "administrador/home";
	        } else {
	            // Si el usuario no es un administrador, busca productos y los filtra por nombre
	            List<Producto> productos = productoService.findAll().stream()
	                    .filter(p -> p.getNombre().toLowerCase().contains(nombre.toLowerCase()))
	                    .collect(Collectors.toList());
	            model.addAttribute("usuario", usuario); 
	            model.addAttribute("productos", productos);
	            model.addAttribute("sesion", idUsuario);
	            return "usuario/home";
	        }
	    } else {
	        // Si no hay una sesión de usuario o el usuario no existe, redirige a la página de inicio de usuario normal
	        List<Producto> productos = productoService.findAll().stream()
	                .filter(p -> p.getNombre().toLowerCase().contains(nombre.toLowerCase()))
	                .collect(Collectors.toList());
            model.addAttribute("sesion", idUsuario);
	        model.addAttribute("productos", productos);
	        return "usuario/home";
	    }
	}
	


}