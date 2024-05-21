const btn = document.getElementById('button');

document.getElementById('form')
	.addEventListener('submit', function(event) {
		event.preventDefault();

		btn.value = 'Sending...';

		const serviceID = 'default_service';
		const templateID = 'template_4wckh9l';

		emailjs.sendForm(serviceID, templateID, this)
			.then(() => {
				btn.value = 'Send Email';
				alert('Sent!');
			}, (err) => {
				btn.value = 'Send Email';
				alert(JSON.stringify(err));
			});
	});

var stripe = Stripe('TU_CLAVE_PUBLICA_DE_STRIPE');
var elements = stripe.elements();

// Estilo personalizado del elemento de tarjeta de crédito
var style = {
	base: {
		fontSize: '16px',
		color: '#32325d',
		fontFamily: '"Helvetica Neue", Helvetica, sans-serif',
		fontSmoothing: 'antialiased',
		'::placeholder': {
			color: '#aab7c4'
		}
	},
	invalid: {
		color: '#fa755a',
		iconColor: '#fa755a'
	}
};

// Crear un elemento de tarjeta de crédito
var card = elements.create('card', {
	style: style
});

// Montar el elemento de tarjeta de crédito en el contenedor con el id "card-element"
card.mount('#card-element');

// Manejar los cambios en el elemento de tarjeta de crédito
card.on('change', function(event) {
	var displayError = document.getElementById('card-errors');
	if (event.error) {
		displayError.textContent = event.error.message;
	} else {
		displayError.textContent = '';
	}
});

// Manejar el envío del formulario
var form = document.getElementById('payment-form');
form.addEventListener('submit', function(event) {
	event.preventDefault();

	// Crear un token de tarjeta de crédito
	stripe.createToken(card).then(function(result) {
		if (result.error) {
			// Mostrar errores al usuario
			var errorElement = document.getElementById('card-errors');
			errorElement.textContent = result.error.message;
		} else {
			// Insertar el token en un campo oculto y enviar el formulario
			var tokenInput = document.createElement('input');
			tokenInput.setAttribute('type', 'hidden');
			tokenInput.setAttribute('name', 'stripeToken');
			tokenInput.setAttribute('value', result.token.id);
			form.appendChild
				(tokenInput);
			form.submit();
		}
	});
});