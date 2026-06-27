import { useState, type FormEvent } from "react";
import { useNavigate } from "react-router-dom";
import { login, signup } from "../api/auth";
import { ApiError } from "../api/client";
import "../styles/login.css";

export default function LoginPage() {
  const navigate = useNavigate();
  const [loginEmail, setLoginEmail] = useState("");
  const [loginPassword, setLoginPassword] = useState("");
  const [signupName, setSignupName] = useState("");
  const [signupEmail, setSignupEmail] = useState("");
  const [signupPassword, setSignupPassword] = useState("");
  const [loading, setLoading] = useState(false);

  async function handleLogin(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setLoading(true);

    try {
      await login({ email: loginEmail, password: loginPassword });
      navigate("/dashboard");
    } catch (error) {
      const message =
        error instanceof ApiError ? error.message : "Login failed";
      alert(message);
    } finally {
      setLoading(false);
    }
  }

  async function handleSignup(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setLoading(true);

    try {
      const response = await signup({
        name: signupName,
        email: signupEmail,
        password: signupPassword,
      });
      alert(response.message || "Signup complete! Now login.");
    } catch (error) {
      const message =
        error instanceof ApiError ? error.message : "Signup failed";
      alert(message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="wrapper">
      <div className="card">
        <h2>Login</h2>
        <form onSubmit={handleLogin}>
          <input
            type="email"
            placeholder="Email"
            value={loginEmail}
            onChange={(event) => setLoginEmail(event.target.value)}
            required
          />
          <input
            type="password"
            placeholder="Password"
            value={loginPassword}
            onChange={(event) => setLoginPassword(event.target.value)}
            required
          />
          <button className="btn" type="submit" disabled={loading}>
            {loading ? "Logging in..." : "Login"}
          </button>
        </form>

        <div className="divider" />

        <h3>Signup</h3>
        <form onSubmit={handleSignup}>
          <input
            type="text"
            placeholder="Full Name"
            value={signupName}
            onChange={(event) => setSignupName(event.target.value)}
            required
          />
          <input
            type="email"
            placeholder="Email"
            value={signupEmail}
            onChange={(event) => setSignupEmail(event.target.value)}
            required
          />
          <input
            type="password"
            placeholder="Password"
            value={signupPassword}
            onChange={(event) => setSignupPassword(event.target.value)}
            required
          />
          <button className="btn secondary" type="submit" disabled={loading}>
            Signup
          </button>
        </form>
      </div>
    </div>
  );
}
