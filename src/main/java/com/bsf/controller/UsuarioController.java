package com.bsf.controller;

import java.util.List;
import java.util.Optional;

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

import com.bsf.model.Orden;
import com.bsf.model.Usuario;
import com.bsf.service.IOrdenService;
import com.bsf.service.IUsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

	private final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

	@Autowired
	private IUsuarioService usuarioService;

	@Autowired
	private IOrdenService ordenService;

	@GetMapping("/registro")
	public String create() {
		return "usuario/registro";
	}

	@PostMapping("/save")
	public String save(Usuario usuario) {
		logger.info("Usuario registro {}", usuario);
		usuario.setTipo("USER");
		usuarioService.save(usuario);
		return "redirect:/";
	}

	@GetMapping("/login")
	public String login() {
		return "usuario/login";
	}

	@PostMapping("/acceder")
	public String acceder(@RequestParam String email, @RequestParam String password, HttpSession session, Model model) {
	    logger.info("Acceso : {}", email);
	    Optional<Usuario> user = usuarioService.findByEmail(email);

	    if (user.isPresent() && user.get().getPassword().equals(password)) {
	        session.setAttribute("idusuario", user.get().getId());
	        if (user.get().getTipo().equals("ADMIN")) {
	            session.setAttribute("rol", "ADMIN"); // Establecer el rol del usuario en la sesión
	            return "redirect:/"; // Redirigir al usuario a una página apropiada para los administradores
	        } else {
	            return "redirect:/";
	        }
	    } else {
	        logger.info("Usuario o contraseña incorrectos");
	        model.addAttribute("error", "Usuario o contraseña incorrectos");
	        return "usuario/login";
	    }
	}


	@GetMapping("/compras")
	public String obtenerCompras(Model model, HttpSession session) {
		model.addAttribute("sesion", session.getAttribute("idusuario"));
		Integer idUsuario = (Integer) session.getAttribute("idusuario");
		Usuario usuario = usuarioService.findById(Integer.parseInt(session.getAttribute("idusuario").toString())).get();
		List<Orden> ordenes = ordenService.findByUsuario(usuario);
		Optional<Usuario> userOptional = usuarioService.findById(idUsuario);
		Usuario usuario1 = userOptional.get();

		model.addAttribute("ordenes", ordenes);
		model.addAttribute("usuario", usuario);
		return "usuario/compras";
	}

	@GetMapping("/detalle/{id}")
	public String detalleCompra(@PathVariable("id") Integer id, Model model, HttpSession session) {
	    Integer idUsuario = (Integer) session.getAttribute("idusuario");
	    Usuario usuario = usuarioService.findById(idUsuario).orElse(null);

	    if (usuario != null) {
	        Optional<Orden> orden = ordenService.findById(id);
	        if (orden.isPresent()) {
	            model.addAttribute("detalles", orden.get().getDetalle());
	        }
	        model.addAttribute("sesion", idUsuario);
	        model.addAttribute("usuario", usuario);
	    }

	    return "usuario/detallecompra";
	}


	@GetMapping("/cerrar")
	public String cerrarSesion(HttpSession session) {
		session.removeAttribute("idusuario");
		return "redirect:/";
	}
}
