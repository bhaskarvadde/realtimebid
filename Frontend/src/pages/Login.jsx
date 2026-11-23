import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { apiFetch } from "../api/client";
import { useAuth } from "../context/AuthContext";
import AuthAside from "../components/AuthAside";

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setSubmitting(true);
    try {
      const data = await apiFetch("/auth/login", {
        method: "POST",
        body: JSON.stringify({ email, password }),
      });
      login(data.token);
      navigate("/");
    } catch (err) {
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="auth-wrap">
      <AuthAside
        heading="Welcome back."
        text="Sign in to track your bids, manage your auctions and watch prices move in real time."
      />
      <div className="auth-main">
        <div className="panel auth-card">
          <h2 className="auth-title">Sign in</h2>
          <p className="muted small">Enter your credentials to continue.</p>
          <form onSubmit={handleSubmit} noValidate>
            {error && <div className="alert alert-error" style={{ marginTop: 16 }}>{error}</div>}
            <div className="field">
              <label htmlFor="email">Email</label>
              <input id="email" className="input" type="email" autoComplete="email"
                     placeholder="you@example.com" value={email}
                     onChange={(e) => setEmail(e.target.value)} />
            </div>
            <div className="field">
              <label htmlFor="password">Password</label>
              <input id="password" className="input" type="password" autoComplete="current-password"
                     placeholder="••••••••" value={password}
                     onChange={(e) => setPassword(e.target.value)} />
            </div>
            <button className="btn btn-primary btn-block" type="submit" disabled={submitting}>
              {submitting ? "Signing in…" : "Sign in"}
            </button>
          </form>
          <p className="auth-foot">New here? <Link to="/register">Create an account</Link></p>
        </div>
      </div>
    </div>
  );
}