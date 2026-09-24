const socket = new WebSocket("ws://localhost:8080/");

const button = document.getElementById("sendBtn");
const input = document.getElementById("messageInput");
const displayWindow = document.getElementById("displayWindow");

socket.onopen = event => {
  console.log("WebSocket connection established.");
};

socket.onmessage = event => {
  console.log("Message received from server: ", event.data);
  displayWindow.textContent = event.data;
};

socket.onclose = event => {
  console.log("WebSocket connection closed.");
};

socket.onerror = error => {
  console.log("WebSocket error: ", error);
};

button.addEventListener("click", event => {
  const message = input.value;
  socket.send(message);
  console.log("Message sent to server: ", message);
});