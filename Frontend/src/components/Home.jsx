import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { apiFetch } from "../api/client";
import AuctionCard from "../components/AuctionCard";

const STEPS = [
  { n: "01", title: "Create an account", text: "Register in seconds and get a secure, token-based session." },
  { n: "02", title: "Discover auctions", text: "Browse live and upcoming auctions and watch prices move in real time." },
  { n: "03", title: "Place your bid", text: "Every bid is validated and processed safely, even when many bidders compete at once." },
  { n: "04", title: "Win the item", text: "Bids in the final minutes extend the clock, so nobody can snipe the win." },
];

export default function Home() {
  const [auctions, setAuctions] = useState([]);
  const [loaded, setLoaded] = useState(false);

  useEffect(() => {
    apiFetch("/auctions/all")
      .then(setAuctions)
      .catch(() => setAuctions([]))
      .finally(() => setLoaded(true));
  }, []);

  const stats = useMemo(() => {
    const live = auctions.filter((a) => a.status === "ACTIVE").length;
    const totalBids = auctions.reduce((sum, a) => sum + (a.bidCount || 0), 0);
    const volume = auctions.reduce((sum, a) => sum + Number(a.currentHighestBid || 0), 0);
    return { total: auctions.length, live, totalBids, volume };
  }, [auctions]);

  const featured = useMemo(
    () =>
      auctions
        .filter((a) => a.status === "ACTIVE")
        .sort((a, b) => (b.bidCount || 0) - (a.bidCount || 0))
        .slice(0, 3),
    [auctions]
  );

  const recent = auctions.slice(0, 6);

  return (
    <div>
      <section className="hero">
        <div className="container">
          <span className="eyebrow">Real-time auction platform</span>
          <h1>
            Discover. Bid. <span className="gradient-text">Win.</span>
          </h1>
          <p>
            Every bid is live, every second counts, and fair play is built in. Join auctions as
            they happen and watch the price move in real time.
          </p>
          <div className="hero-cta">
            <Link to="/auctions" className="btn btn-primary">Explore Auctions</Link>
            <Link to="/create" className="btn btn-ghost">Create Auction</Link>
          </div>
        </div>
      </section>

      <div className="container">
        <div className="stats">
          <div className="stat">
            <div className="stat-value">{stats.total}</div>
            <div className="stat-label">Total auctions</div>
          </div>
          <div className="stat">
            <div className="stat-value cyan">{stats.live}</div>
            <div className="stat-label">Live right now</div>
          </div>
          <div className="stat">
            <div className="stat-value">{stats.totalBids}</div>
            <div className="stat-label">Bids placed</div>
          </div>
          <div className="stat">
            <div className="stat-value">${Math.round(stats.volume).toLocaleString()}</div>
            <div className="stat-label">Leading bid volume</div>
          </div>
        </div>

        <section className="section">
          <div className="section-head">
            <div>
              <h2 className="section-title">Featured live auctions</h2>
              <p className="section-sub">The most active auctions happening right now.</p>
            </div>
            <Link to="/auctions" className="btn btn-ghost btn-sm">View all</Link>
          </div>
          {featured.length > 0 ? (
            <div className="grid">
              {featured.map((a) => <AuctionCard key={a.id} auction={a} />)}
            </div>
          ) : (
            <div className="empty">
              {loaded ? "No live auctions right now. Check back soon or create one." : "Loading…"}
            </div>
          )}
        </section>

        {recent.length > 0 && (
          <section className="section">
            <div className="section-head">
              <div>
                <h2 className="section-title">Recently added</h2>
                <p className="section-sub">Fresh listings from the community.</p>
              </div>
            </div>
            <div className="grid">
              {recent.map((a) => <AuctionCard key={a.id} auction={a} />)}
            </div>
          </section>
        )}

        <section className="section">
          <div className="section-head">
            <div>
              <h2 className="section-title">How it works</h2>
              <p className="section-sub">From sign-up to winning in four steps.</p>
            </div>
          </div>
          <div className="steps">
            {STEPS.map((s) => (
              <div className="step" key={s.n}>
                <div className="step-num">{s.n}</div>
                <h3 className="step-title">{s.title}</h3>
                <p className="muted small">{s.text}</p>
              </div>
            ))}
          </div>
        </section>

        <section className="cta-banner">
          <h2>Ready to list your first item?</h2>
          <p>Set a starting price, pick your times, and let the bids roll in.</p>
          <Link to="/create" className="btn btn-primary">Create Auction</Link>
        </section>
      </div>
    </div>
  );
}