import { useState } from "react";
import { Link, NavLink, useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { initials } from "../utils/format";

export default function Navbar() {
  const { isLoggedIn, logout, user } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [open, setOpen] = useState(false);

  const close = () => setOpen(false);
  const handleLogout = () => {
    logout();
    close();
    navigate("/login");
  };

  const linkClass = ({ isActive }) => `nav-link ${isActive ? "active" : ""}`;
  const onDashboard = location.pathname === "/dashboard";
  const tab = new URLSearchParams(location.search).get("tab") || "bids";

  return (
    <header className="nav">
      <div className="container nav-inner">
        <Link to="/" className="logo" onClick={close}>
          <span className="logo-mark" />
          <span>
            REALTIME<span className="accent">BID</span>
          </span>
        </Link>

        <button className="nav-toggle" aria-label="Toggle menu" onClick={() => setOpen(!open)}>
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round">
            {open ? <path d="M6 6l12 12M18 6L6 18" /> : <path d="M4 7h16M4 12h16M4 17h16" />}
          </svg>
        </button>

        <nav className={`nav-menu ${open ? "open" : ""}`}>
          <div className="nav-links">
            <NavLink to="/" end className={linkClass} onClick={close}>Home</NavLink>
            <NavLink to="/auctions" className={linkClass} onClick={close}>Explore Auctions</NavLink>
            {isLoggedIn && (
              <>
                <Link
                  to="/dashboard?tab=bids"
                  className={`nav-link ${onDashboard && tab === "bids" ? "active" : ""}`}
                  onClick={close}
                >
                  My Bids
                </Link>
                <Link
                  to="/dashboard?tab=auctions"
                  className={`nav-link ${onDashboard && tab === "auctions" ? "active" : ""}`}
                  onClick={close}
                >
                  My Auctions
                </Link>
              </>
            )}
          </div>

          <div className="nav-actions">
            <Link to="/create" className="btn btn-primary btn-sm" onClick={close}>
              + Create Auction
            </Link>
            {isLoggedIn ? (
              <>
                <Link to="/dashboard" className="nav-profile" onClick={close}>
                  <span className="avatar">{initials(user?.name)}</span>
                  <span className="nav-profile-name">{user?.name || "Account"}</span>
                </Link>
                <button className="btn btn-ghost btn-sm" onClick={handleLogout}>Logout</button>
              </>
            ) : (
              <>
                <Link to="/login" className="btn btn-ghost btn-sm" onClick={close}>Login</Link>
                <Link to="/register" className="btn btn-ghost btn-sm" onClick={close}>Register</Link>
              </>
            )}
          </div>
        </nav>
      </div>
    </header>
  );
}