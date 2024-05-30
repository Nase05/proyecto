const btn = document.getElementById('button');

document.getElementById('form')
	.addEventListener('submit', function(event) {
		event.preventDefault();

		btn.value = 'Sending...';
debugger;
		const serviceID = 'default_service';
		const templateID = 'template_4wckh9l';

		emailjs.sendForm(serviceID, templateID, this)
			.then(() => {
				btn.value = 'Send Email';
				alert('Enviado con exito');
			}, (err) => {
				btn.value = 'Send Email';
				alert(JSON.stringify(err));
			});
	});
