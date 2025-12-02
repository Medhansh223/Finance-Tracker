// app.js
const API = API_URL;
let accessToken = null;

// -----------------------------------------
// LOGIN
// -----------------------------------------
async function login() {
  const email = document.getElementById("loginEmail").value;
  const password = document.getElementById("loginPassword").value;

  const res = await fetch(`${API}/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    credentials: "include",
    body: JSON.stringify({ email, password })
  });

  const data = await res.json();
  if (data.accessToken) {
    accessToken = data.accessToken;
    localStorage.setItem("accessToken", accessToken);
    window.location.href = "dashboard.html";
  } else {
    alert(data.message || "Login failed");
  }
}

// -----------------------------------------
// SIGNUP
// -----------------------------------------
async function signup() {
  const name = document.getElementById("signupName").value;
  const email = document.getElementById("signupEmail").value;
  const password = document.getElementById("signupPassword").value;

  const res = await fetch(`${API}/auth/signup`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ name, email, password })
  });

  const data = await res.json();
  alert(data.message || "Signup complete! Now login.");
}

// -----------------------------------------
// LOGOUT
// -----------------------------------------
async function logout() {
  await fetch(`${API}/auth/logout`, {
    method: "POST",
    credentials: "include"
  });

  localStorage.removeItem("accessToken");
  window.location.href = "index.html";
}

// -----------------------------------------
// REFRESH TOKEN
// -----------------------------------------
async function refreshToken() {
  const res = await fetch(`${API}/auth/refresh`, {
    credentials: "include"
  });

  if (res.ok) {
    const data = await res.json();
    accessToken = data.accessToken;
    localStorage.setItem("accessToken", accessToken);
  }
}

// -----------------------------------------
// AUTH REQUEST WRAPPER
// -----------------------------------------
async function authFetch(url, options = {}) {
  accessToken = localStorage.getItem("accessToken");

  const res = await fetch(url, {
    ...options,
    headers: {
      ...options.headers,
      Authorization: `Bearer ${accessToken}`
    },
    credentials: "include"
  });

  if (res.status === 401) {
    await refreshToken();
    return authFetch(url, options); // retry
  }

  return res;
}

// -----------------------------------------
// ADD TRANSACTION
// -----------------------------------------
async function addTransaction() {
  const amount = document.getElementById("amount").value;
  const type = document.getElementById("type").value;
  const category = document.getElementById("category").value;
  const description = document.getElementById("description").value;
  const date = document.getElementById("date").value;

  const res = await authFetch(`${API}/transactions`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ amount, type, category, description, date })
  });

  const data = await res.json();
  alert("Transaction Added!");
  loadTransactions();
}

// -----------------------------------------
// LOAD TRANSACTIONS
// -----------------------------------------
async function loadTransactions() {
  const res = await authFetch(`${API}/transactions`);
  const data = await res.json();

  const list = document.getElementById("transactions-list");
  list.innerHTML = "";

  data.forEach(tx => {
    list.innerHTML += `
      <div>
        <b>${tx.type.toUpperCase()}</b> - ₹${tx.amount}<br>
        Category: ${tx.category}<br>
        ${tx.description}<br>
        <small>${new Date(tx.date).toLocaleDateString()}</small>
      </div>
    `;
  });
}

// auto-load transactions when on dashboard
if (window.location.pathname.includes("dashboard.html")) {
  loadTransactions();
}