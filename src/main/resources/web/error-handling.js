document.body.addEventListener('htmx:responseError', function(evt) {
  alert(evt.detail.xhr.responseText);
});

document.body.addEventListener('htmx:sendError', function(evt) {
  alert('Server unavailable!');
});
