package com.bsf.controller;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.bsf.model.Comentarios;
import com.bsf.model.Producto;
import com.bsf.model.Usuario;
import com.bsf.service.ComentarioService;
import com.bsf.service.ProductoServiceImpl;
import com.bsf.service.UploadFileService;
import com.bsf.service.UsuarioServiceImpl;

import jakarta.servlet.http.HttpSession;

@Controller
public class ComentarioController {

	@Autowired
	private ComentarioService comentarioService;

	@Autowired
	private ProductoServiceImpl productoService;

	@Autowired
	private UsuarioServiceImpl usuarioService;
	@Autowired
	private UploadFileService upload;

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

	@PostMapping("/comentar")
	public String comentar(@RequestParam String contenido, @RequestParam("imagen") MultipartFile imagenFile, // Nueva
																												// variable
																												// para
																												// la
																												// imagen
			@RequestParam Integer productoId, HttpSession session) {
		Integer userId = (Integer) session.getAttribute("idusuario");
		if (userId == null) {
			return "redirect:/usuario/login";
		}

		Optional<Producto> productoOpt = productoService.findById(productoId);
		Optional<Usuario> usuarioOpt = usuarioService.findById(userId);

		if (productoOpt.isPresent() && usuarioOpt.isPresent()) {
			Producto producto = productoOpt.get();
			Usuario usuario = usuarioOpt.get();

			try {
				// Guarda la imagen y obtén el nombre del archivo guardado
				String nombreArchivo = upload.saveImage(imagenFile);

				// Crea el comentario con la ruta de la imagen guardada
				Comentarios comentario = new Comentarios();
				comentario.setUsuario(usuario);
				comentario.setProducto(producto);
				comentario.setContenido(contenido);
				comentario.setImagen(nombreArchivo);
				comentario.setFecha(new Date());

				// Guarda el comentario en la base de datos
				comentarioService.guardarComentario(comentario);
			} catch (IOException e) {
				e.printStackTrace(); // Maneja cualquier excepción de almacenamiento de archivos
			}
		}

		return "redirect:/usuario/productohome/" + productoId;
	}

}
