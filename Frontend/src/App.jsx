import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import Navbar from "./components/Navbar";
import ProtectedRoute from "./components/ProtectedRoute";
import Home from "./components/Home";
import Login from "./pages/Login";
import Register from "./pages/Register";
import AuctionList from "./pages/AuctionList";
import AuctionDetail from "./pages/AuctionDetail";
import CreateAuction from "./pages/CreateAuction";
import Dashboard from "./pages/Dashboard";

function Footer() {
  return (
    <footer className="footer">
      <div className="container">
        © {new Date().getFullYear()} <span className="footer-brand">REALTIMEBID</span> · Real-time auctions built with Spring Boot and React
      </div>
    </footer>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Navbar />
        <main>
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/auctions" element={<AuctionList />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/auctions/:id" element={<ProtectedRoute><AuctionDetail /></ProtectedRoute>} />
            <Route path="/create" element={<ProtectedRoute><CreateAuction /></ProtectedRoute>} />
            <Route path="/dashboard" element={<ProtectedRoute><Dashboard /></ProtectedRoute>} />
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </main>
        <Footer />
      </BrowserRouter>
    </AuthProvider>
  );
}
