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

import com.bsf.model.Comentarios;
import com.bsf.model.DetalleOrden;
import com.bsf.model.Orden;
import com.bsf.model.Producto;
import com.bsf.model.Usuario;
import com.bsf.service.ComentarioService;
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
    private ComentarioService comentarioService;
	
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

	private Usuario getUsuarioFromSession(HttpSession session) {
		Integer idUsuario = (Integer) session.getAttribute("idusuario");
		if (idUsuario != null) {
			Optional<Usuario> userOptional = usuarioService.findById(idUsuario);
			if (userOptional.isPresent()) {
				return userOptional.get();
			}
		}
		return null;
	}

	private void addSessionAttributes(Model model, HttpSession session) {
		Integer idUsuario = (Integer) session.getAttribute("idusuario");
		model.addAttribute("sesion", idUsuario);
		if (idUsuario != null) {
			Usuario usuario = getUsuarioFromSession(session);
			if (usuario != null) {
				model.addAttribute("usuario", usuario);
			}
		}
	}

	@GetMapping("")
	public String home(Model model, HttpSession session) {
		addSessionAttributes(model, session);
		Usuario usuario = getUsuarioFromSession(session);
		if (usuario != null && "ADMIN".equals(usuario.getTipo())) {
			model.addAttribute("productos", productoService.findAll());
			return "administrador/home";
		} else {
			List<Producto> productosPopulares = productoService.findAll().stream()
					.filter(Producto::isPopular)
					.collect(Collectors.toList());
			model.addAttribute("productos", productosPopulares);
		}
		log.info("Sesión del usuario: {}", session.getAttribute("idusuario"));
		return "usuario/home";
	}

	@GetMapping("usuario/contacto")
	public String contacto(Model model, HttpSession session) {
		addSessionAttributes(model, session);
		log.info("Sesión del usuario: {}", session.getAttribute("idusuario"));
		return "usuario/contacto";
	}

	@GetMapping("usuario/acercade")
	public String acercade(Model model, HttpSession session) {
		addSessionAttributes(model, session);
		log.info("Sesión del usuario: {}", session.getAttribute("idusuario"));
		return "usuario/acercade";
	}

	@GetMapping("usuario/piezas")
	public String piezas(Model model, HttpSession session) {
		addSessionAttributes(model, session);
		List<Producto> productosMobiliario = productoService.findAll().stream()
				.filter(producto -> "piezas".equalsIgnoreCase(producto.getTipo()))
				.collect(Collectors.toList());
		model.addAttribute("productos", productosMobiliario);
		return "usuario/piezas";
	}

	@GetMapping("usuario/cachimbas")
	public String cachimbas(Model model, HttpSession session) {
		addSessionAttributes(model, session);
		List<Producto> productosMobiliario = productoService.findAll().stream()
				.filter(producto -> "cachimbas".equalsIgnoreCase(producto.getTipo()))
				.collect(Collectors.toList());
		model.addAttribute("productos", productosMobiliario);
		return "usuario/cachimbas";
	}
	
	@GetMapping("usuario/mobiliario")
	public String mobiliario(Model model, HttpSession session) {
		addSessionAttributes(model, session);
		List<Producto> productosMobiliario = productoService.findAll().stream()
				.filter(producto -> "mobiliario".equalsIgnoreCase(producto.getTipo()))
				.collect(Collectors.toList());
		model.addAttribute("productos", productosMobiliario);
		return "usuario/mobiliario";
	}

	@GetMapping("/usuario/productohome/{id}")
	public String productoHome(@PathVariable("id") Integer id, Model model, HttpSession session) {
	    addSessionAttributes(model, session);
	    Optional<Producto> productoOptional = productoService.get(id);
	    if (productoOptional.isPresent()) {
	        Producto producto = productoOptional.get();
	        model.addAttribute("producto", producto);
	        
	        // Obtener los comentarios del producto
	        List<Comentarios> comentarios = comentarioService.obtenerComentariosPorProducto(producto);
	        model.addAttribute("comentarios", comentarios);
	    }
	    log.info("Id producto enviado como parámetro {}", id);
	    return "usuario/productohome";
	}


	@PostMapping("/cart")
	public String addCart(@RequestParam("id") Integer id, @RequestParam("cantidad") Integer cantidad, Model model,
			HttpSession session) {
		addSessionAttributes(model, session);
		Optional<Producto> optionalProducto = productoService.get(id);
		if (optionalProducto.isPresent()) {
			Producto producto = optionalProducto.get();
			DetalleOrden detalleOrden = new DetalleOrden();
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

			double sumaTotal = detalles.stream().mapToDouble(dt -> dt.getTotal()).sum();
			orden.setTotal(sumaTotal);
			model.addAttribute("cart", detalles);
			model.addAttribute("orden", orden);
		}
		return "usuario/carrito";
	}

	@GetMapping("/delete/cart/{id}")
	public String deleteProducto(@PathVariable("id") Integer id, Model model) {
		detalles.removeIf(detalle -> detalle.getProducto().getId() == id);
		double sumaTotal = detalles.stream().mapToDouble(dt -> dt.getTotal()).sum();
		orden.setTotal(sumaTotal);
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);
		return "usuario/carrito";
	}

	@GetMapping("/getCart")
	public String getCart(Model model, HttpSession session) {
		addSessionAttributes(model, session);
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);
		return "usuario/carrito";
	}

	@GetMapping("/order")
	public String order(Model model, HttpSession session) {
	    addSessionAttributes(model, session); // Asegúrate de que este método añade el usuario al modelo
	    Usuario usuario = getUsuarioFromSession(session);
	    if (usuario == null) {
	        // Si el usuario no está logueado, redirige a la página de login con un mensaje de error
	        model.addAttribute("error", "Debes iniciar sesión para continuar.");
	        return "redirect:/usuario/login";
	    } else {
	        model.addAttribute("usuario", usuario); // Añade el usuario al modelo solo si no es null
	        model.addAttribute("cart", detalles);
	        model.addAttribute("orden", orden);
	        return "usuario/resumenorden";
	    }
	}


	@GetMapping("/saveOrder")
	public String saveOrder(HttpSession session) {
		Date fechaCreacion = new Date();
		orden.setFechaCreacion(fechaCreacion);
		orden.setNumero(ordenService.generarNumeroOrden());
		Usuario usuario = getUsuarioFromSession(session);
		if (usuario != null) {
			orden.setUsuario(usuario);
			ordenService.save(orden);
			detalles.forEach(detalle -> {
				detalle.setOrden(orden);
				detalleOrdenService.save(detalle);
			});
			orden = new Orden();
			detalles.clear();
		}
		return "redirect:/";
	}

	@PostMapping("/search")
	public String searchProduct(@RequestParam("nombre") String nombre, Model model, HttpSession session) {
		addSessionAttributes(model, session);
		List<Producto> productos = productoService.findAll().stream()
				.filter(p -> p.getNombre().toLowerCase().contains(nombre.toLowerCase()))
				.collect(Collectors.toList());
		model.addAttribute("productos", productos);
		Usuario usuario = getUsuarioFromSession(session);
		if (usuario != null && "ADMIN".equals(usuario.getTipo())) {
			return "administrador/home";
		} else {
			return "usuario/home";
		}
	}
}
