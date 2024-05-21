		// Obtener la imagen y la ventana modal
		var img = document.getElementById('producto-imagen');
		var modal = document.createElement('div');
		var modalImg = document.createElement('img');

		// Agregar estilos y propiedades a la ventana modal
		modal.style.display = 'none';
		modal.style.position = 'fixed';
		modal.style.zIndex = '1';
		modal.style.paddingTop = '100px';
		modal.style.left = '0';
		modal.style.top = '0';
		modal.style.width = '100%';
		modal.style.height = '100%';
		modal.style.overflow = 'auto';
		modal.style.backgroundColor = 'rgb(0,0,0)';
		modal.style.backgroundColor = 'rgba(0,0,0,0.9)';
		modalImg.style.margin = 'auto';
		modalImg.style.display = 'block';
		modalImg.style.width = '80%';
		modalImg.style.maxWidth = '700px';

		// Agregar la imagen agrandada a la ventana modal
		modal.appendChild(modalImg);

		// Agregar la ventana modal al cuerpo del documento
		document.body.appendChild(modal);

		// Agregar evento clic a la imagen para mostrarla agrandada
		img.onclick = function () {
			modal.style.display = 'block';
			modalImg.src = this.src;
		};

		// Agregar evento clic a la ventana modal para cerrarla
		modal.onclick = function () {
			modal.style.display = 'none';
		};
	