import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { apiFetch } from "../api/client";
import AuthAside from "../components/AuthAside";

export default function Register() {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const navigate = useNavigate();

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setSubmitting(true);
    try {
      await apiFetch("/auth/register", {
        method: "POST",
        body: JSON.stringify({ name, email, password }),
      });
      navigate("/login");
    } catch (err) {
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="auth-wrap">
      <AuthAside
        heading="Join the next generation of auctions."
        text="Create your account to bid on live auctions and list your own items."
      />
      <div className="auth-main">
        <div className="panel auth-card">
          <h2 className="auth-title">Create account</h2>
          <p className="muted small">It only takes a few seconds.</p>
          <form onSubmit={handleSubmit} noValidate>
            {error && <div className="alert alert-error" style={{ marginTop: 16 }}>{error}</div>}
            <div className="field">
              <label htmlFor="name">Name</label>
              <input id="name" className="input" autoComplete="name" placeholder="Your name"
                     value={name} onChange={(e) => setName(e.target.value)} />
            </div>
            <div className="field">
              <label htmlFor="email">Email</label>
              <input id="email" className="input" type="email" autoComplete="email"
                     placeholder="you@example.com" value={email}
                     onChange={(e) => setEmail(e.target.value)} />
            </div>
            <div className="field">
              <label htmlFor="password">Password</label>
              <input id="password" className="input" type="password" autoComplete="new-password"
                     placeholder="At least 6 characters" value={password}
                     onChange={(e) => setPassword(e.target.value)} />
            </div>
            <button className="btn btn-primary btn-block" type="submit" disabled={submitting}>
              {submitting ? "Creating…" : "Create account"}
            </button>
          </form>
          <p className="auth-foot">Already registered? <Link to="/login">Sign in</Link></p>
        </div>
      </div>
    </div>
  );
}